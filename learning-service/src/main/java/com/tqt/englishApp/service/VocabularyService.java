package com.tqt.englishApp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tqt.englishApp.dto.request.VocabularyRequest;
import com.tqt.englishApp.dto.request.WordMeaningRequest;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesResponse;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesSimpleResponse;
import com.tqt.englishApp.entity.SubTopic;
import com.tqt.englishApp.entity.Vocabulary;
import com.tqt.englishApp.entity.WordMeaning;
import com.tqt.englishApp.entity.MeaningSynonym;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.VocabularyMapper;
import com.tqt.englishApp.repository.SubTopicRepository;
import com.tqt.englishApp.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.tqt.englishApp.entity.MeaningImage;

@Service
public class VocabularyService {
    @Autowired
    private SubTopicRepository subTopicRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserLearningProfileService userLearningProfileService;

    private static final int PAGE_SIZE = 10;

    public VocabulariesResponse createVocabulary(VocabularyRequest request) {
        MultipartFile audio = request.getAudioFile();
        String audioUrl = request.getAudioUrl();
        Vocabulary vocabulary = vocabularyMapper.toVocabulary(request);
        List<SubTopic> subTopics = subTopicRepository.findAllById(request.getSubTopics());
        if (subTopics.isEmpty()) {
            throw new AppException(ErrorCode.TOPIC_NOT_EXISTED);
        }
        vocabulary.setSubTopics(subTopics);
        vocabulary.setMeanings(processMeanings(request, vocabulary));
        handleAudioUpload(vocabulary, audio, audioUrl);
        return vocabularyMapper.toVocabulariesResponse(vocabularyRepository.save(vocabulary));
    }

    public VocabulariesResponse updateVocabulary(Integer vocabularyId, VocabularyRequest request) {
        MultipartFile audio = request.getAudioFile();
        String audioUrl = request.getAudioUrl();
        Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
                .orElseThrow(() -> new AppException(ErrorCode.VOCABULARY_NOT_EXISTED));

        vocabularyMapper.updateVocabulary(vocabulary, request);

        List<SubTopic> subTopics = subTopicRepository.findAllById(request.getSubTopics());
        if (subTopics.isEmpty()) {
            throw new AppException(ErrorCode.TOPIC_NOT_EXISTED);
        }
        vocabulary.setSubTopics(subTopics);

        if (request.getMeanings() != null) {
            updateMeanings(vocabulary, request.getMeanings());
        }

        handleAudioUpload(vocabulary, audio, audioUrl);
        return vocabularyMapper.toVocabulariesResponse(vocabularyRepository.save(vocabulary));
    }

    private void updateMeanings(Vocabulary vocabulary, List<WordMeaningRequest> requests) {
        java.util.Map<Integer, WordMeaningRequest> requestMap = new java.util.HashMap<>();
        List<WordMeaningRequest> newRequests = new ArrayList<>();

        for (WordMeaningRequest req : requests) {
            if (req.getId() != null) {
                requestMap.put(req.getId(), req);
            } else {
                newRequests.add(req);
            }
        }

        java.util.Iterator<WordMeaning> iterator = vocabulary.getMeanings().iterator();
        while (iterator.hasNext()) {
            WordMeaning existing = iterator.next();
            if (requestMap.containsKey(existing.getId())) {
                WordMeaningRequest req = requestMap.get(existing.getId());
                existing.setType(req.getType());
                existing.setDefinition(req.getDefinition());
                existing.setVnWord(req.getVnWord());
                existing.setVnDefinition(req.getVnDefinition());
                existing.setExample(req.getExample());
                existing.setVnExample(req.getVnExample());

                processSynonymsAndImages(req, existing);
            } else {
                iterator.remove();
            }
        }

        for (WordMeaningRequest mReq : newRequests) {
            WordMeaning meaning = WordMeaning.builder()
                    .type(mReq.getType())
                    .definition(mReq.getDefinition())
                    .vnWord(mReq.getVnWord())
                    .vnDefinition(mReq.getVnDefinition())
                    .example(mReq.getExample())
                    .vnExample(mReq.getVnExample())
                    .vocabulary(vocabulary)
                    .synonyms(new ArrayList<>())
                    .images(new ArrayList<>())
                    .build();
            processSynonymsAndImages(mReq, meaning);
            vocabulary.getMeanings().add(meaning);
        }
    }

    private List<WordMeaning> processMeanings(VocabularyRequest request, Vocabulary vocabulary) {
        if (request.getMeanings() == null)
            return new ArrayList<>();

        return request.getMeanings().stream().map(mReq -> {
            WordMeaning meaning = WordMeaning.builder()
                    .type(mReq.getType())
                    .definition(mReq.getDefinition())
                    .vnWord(mReq.getVnWord())
                    .vnDefinition(mReq.getVnDefinition())
                    .example(mReq.getExample())
                    .vnExample(mReq.getVnExample())
                    .vocabulary(vocabulary)
                    .synonyms(new ArrayList<>())
                    .images(new ArrayList<>())
                    .build();

            processSynonymsAndImages(mReq, meaning);
            return meaning;
        }).collect(Collectors.toList());
    }

    private void processSynonymsAndImages(WordMeaningRequest mReq, WordMeaning meaning) {
        if (mReq.getSynonymWords() != null) {
            List<com.tqt.englishApp.entity.MeaningSynonym> synonyms = mReq.getSynonymWords().stream()
                    .map(word -> vocabularyRepository
                            .findByWordContainingIgnoreCase(word, org.springframework.data.domain.PageRequest.of(0, 1))
                            .getContent().stream().findFirst().orElse(null))
                    .filter(v -> v != null && !v.getMeanings().isEmpty())
                    .map(v -> com.tqt.englishApp.entity.MeaningSynonym.builder()
                            .meaning(meaning)
                            .synonymMeaning(v.getMeanings().get(0))
                            .build())
                    .collect(Collectors.toList());
            if (meaning.getSynonyms() == null)
                meaning.setSynonyms(new ArrayList<>());
            meaning.getSynonyms().clear();
            meaning.getSynonyms().addAll(synonyms);
        } else if (meaning.getSynonyms() != null) {
            meaning.getSynonyms().clear();
        }

        List<com.tqt.englishApp.entity.MeaningImage> images = new ArrayList<>();

        if (mReq.getExistingImageUrls() != null) {
            mReq.getExistingImageUrls().stream()
                    .filter(url -> url != null && !url.trim().isEmpty())
                    .forEach(url -> images
                            .add(com.tqt.englishApp.entity.MeaningImage.builder().url(url).meaning(meaning).build()));
        }

        if (mReq.getImageFiles() != null) {
            mReq.getImageFiles().stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .forEach(file -> {
                        String uploadedUrl = uploadToCloudinary(file, "image");
                        if (uploadedUrl != null) {
                            images.add(com.tqt.englishApp.entity.MeaningImage.builder().url(uploadedUrl)
                                    .meaning(meaning).build());
                        }
                    });
        }

        if (meaning.getImages() == null)
            meaning.setImages(new ArrayList<>());
        meaning.getImages().clear();
        meaning.getImages().addAll(images);
    }

    private String uploadToCloudinary(MultipartFile file, String resourceType) {
        try {
            Map res = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", resourceType));
            return res.get("secure_url").toString();
        } catch (IOException ex) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private void handleAudioUpload(Vocabulary vocabulary, MultipartFile audio, String audioUrl) {
        if (audio != null && !audio.isEmpty()) {
            vocabulary.setAudioUrl(uploadToCloudinary(audio, "auto"));
        } else if (audioUrl != null && !audioUrl.trim().isEmpty()) {
            vocabulary.setAudioUrl(audioUrl);
        }
    }

    public Page<VocabulariesResponse> getVocabularies(Map<String, String> params) {
        String word = params.get("word");
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(PAGE_SIZE)));

        page = Math.max(0, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<Vocabulary> result;

        if (word == null || word.trim().isEmpty()) {
            result = vocabularyRepository.findAll(pageable);
        } else {
            result = vocabularyRepository.findByWordContainingIgnoreCase(word.trim(), pageable);
        }

        return result.map(vocabularyMapper::toVocabulariesResponse);
    }

    public List<VocabulariesResponse> searchVocabularies(String word) {
        return vocabularyRepository.findTop5ByWordContainingIgnoreCase(word).stream()
                .map(vocabularyMapper::toVocabulariesResponse)
                .collect(Collectors.toList());
    }

    public VocabulariesResponse getVocabularyById(Integer id) {
        return vocabularyMapper.toVocabulariesResponse(vocabularyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOCABULARY_NOT_EXISTED)));
    }

    public void deleteAllVocabulary() {
        List<Vocabulary> list = vocabularyRepository.findAllWithoutSubTopics();
        System.out.println("Số từ vựng cần xóa: " + list.size());
        list.forEach(v -> deleteVocabulary(v.getId()));
    }

    public void deleteVocabulary(Integer id) {
        vocabularyRepository.deleteById(id);
    }

    public Long countVocabulary() {
        return vocabularyRepository.count();
    }

    public Page<VocabulariesSimpleResponse> getSaveVocabularies(Map<String, String> params) {
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(PAGE_SIZE)));

        page = Math.max(0, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<Vocabulary> result = vocabularyRepository.findByIsSaveTrue(pageable);

        return result.map(vocabularyMapper::toVocabulariesSimpleResponse);
    }

    public void toggleSaveVocabulary(Integer id) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOCABULARY_NOT_EXISTED));

        vocabulary.setIsSave(!vocabulary.getIsSave());
        vocabularyRepository.save(vocabulary);
    }

}
