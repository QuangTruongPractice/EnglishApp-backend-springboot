package com.tqt.englishApp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tqt.englishApp.dto.request.VocabularyRequest;
import com.tqt.englishApp.dto.request.VocabularyMeaningRequest;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesResponse;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesSimpleResponse;
import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.enums.Level;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.VocabularyMapper;
import com.tqt.englishApp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VocabularyService {
    @Autowired
    private SubTopicRepository subTopicRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private UserSavedVocabularyRepository userSavedVocabularyRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserVocabularyProgressRepository userVocabularyProgressRepository;

    private static final int PAGE_SIZE = 10;

    public List<VocabulariesResponse> getRecentVocabularies() {
        return vocabularyRepository.findTop5ByOrderByIdDesc().stream()
                .map(vocabularyMapper::toVocabulariesResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getLevelDistribution() {
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (Level level : Level.values()) {
            distribution.put(level.name(), vocabularyRepository.countByLevel(level));
        }
        return distribution;
    }

    @Transactional
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

    @Transactional
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

    private void updateMeanings(Vocabulary vocabulary, List<VocabularyMeaningRequest> requests) {
        Map<Integer, VocabularyMeaningRequest> requestMap = new HashMap<>();
        List<VocabularyMeaningRequest> newRequests = new ArrayList<>();

        for (VocabularyMeaningRequest req : requests) {
            if (req.getId() != null) {
                requestMap.put(req.getId(), req);
            } else {
                newRequests.add(req);
            }
        }

        Iterator<VocabularyMeaning> iterator = vocabulary.getMeanings().iterator();
        while (iterator.hasNext()) {
            VocabularyMeaning existing = iterator.next();
            if (requestMap.containsKey(existing.getId())) {
                VocabularyMeaningRequest req = requestMap.get(existing.getId());
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

        for (VocabularyMeaningRequest mReq : newRequests) {
            VocabularyMeaning meaning = VocabularyMeaning.builder()
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

    private List<VocabularyMeaning> processMeanings(VocabularyRequest request, Vocabulary vocabulary) {
        if (request.getMeanings() == null)
            return new ArrayList<>();

        return request.getMeanings().stream().map(mReq -> {
            VocabularyMeaning meaning = VocabularyMeaning.builder()
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

    private void processSynonymsAndImages(VocabularyMeaningRequest mReq, VocabularyMeaning meaning) {
        if (mReq.getSynonymWords() != null) {
            Set<VocabularyMeaning> targetMeanings = mReq.getSynonymWords().stream()
                    .map(word -> vocabularyRepository
                            .findByWordContainingIgnoreCase(word, PageRequest.of(0, 1))
                            .getContent().stream().findFirst().orElse(null))
                    .filter(v -> v != null && !v.getMeanings().isEmpty())
                    .map(v -> v.getMeanings().get(0))
                    .filter(v -> meaning.getId() == null || !v.getId().equals(meaning.getId()))
                    .collect(Collectors.toSet());

            List<MeaningSynonym> currentSynonyms = meaning.getSynonyms();
            if (currentSynonyms == null) {
                currentSynonyms = new ArrayList<>();
                meaning.setSynonyms(currentSynonyms);
            }

            Set<VocabularyMeaning> currentTargets = currentSynonyms.stream()
                    .map(MeaningSynonym::getSynonymMeaning)
                    .collect(Collectors.toSet());

            meaning.getSynonyms().clear();
            for (VocabularyMeaning target : targetMeanings) {
                meaning.getSynonyms().add(MeaningSynonym.builder()
                        .meaning(meaning)
                        .synonymMeaning(target)
                        .build());

                if (target.getSynonyms() == null)
                    target.setSynonyms(new ArrayList<>());
                
                boolean alreadyContains = target.getSynonyms().stream()
                        .anyMatch(s -> s.getSynonymMeaning().getId().equals(meaning.getId()));
                
                if (!alreadyContains) {
                    target.getSynonyms().add(MeaningSynonym.builder()
                            .meaning(target)
                            .synonymMeaning(meaning)
                            .build());
                }
            }

            for (VocabularyMeaning oldTarget : currentTargets) {
                if (!targetMeanings.contains(oldTarget)) {
                    if (oldTarget.getSynonyms() != null) {
                        oldTarget.getSynonyms().removeIf(s -> 
                            s.getSynonymMeaning().getId() != null && 
                            s.getSynonymMeaning().getId().equals(meaning.getId()));
                    }
                }
            }
        } else if (meaning.getSynonyms() != null) {
            for (MeaningSynonym s : meaning.getSynonyms()) {
                VocabularyMeaning target = s.getSynonymMeaning();
                if (target.getSynonyms() != null) {
                    target.getSynonyms().removeIf(rs -> 
                        rs.getSynonymMeaning().getId() != null && 
                        rs.getSynonymMeaning().getId().equals(meaning.getId()));
                }
            }
            meaning.getSynonyms().clear();
        }

        List<MeaningImage> images = new ArrayList<>();

        if (mReq.getExistingImageUrls() != null) {
            mReq.getExistingImageUrls().stream()
                    .filter(url -> url != null && !url.trim().isEmpty())
                    .forEach(url -> images
                            .add(MeaningImage.builder().url(url).meaning(meaning).build()));
        }

        if (mReq.getImageFiles() != null) {
            mReq.getImageFiles().stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .forEach(file -> {
                        String uploadedUrl = uploadToCloudinary(file, "image");
                        if (uploadedUrl != null) {
                            images.add(MeaningImage.builder().url(uploadedUrl)
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
            Map<?, ?> res = cloudinary.uploader().upload(
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

    @Transactional
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

    @Transactional
    public List<VocabulariesResponse> searchVocabularies(String word) {
        return vocabularyRepository.findTop5ByWordContainingIgnoreCase(word).stream()
                .map(vocabularyMapper::toVocabulariesResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public VocabulariesResponse getVocabularyById(Integer id, String userId) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOCABULARY_NOT_EXISTED));
        VocabulariesResponse response = vocabularyMapper.toVocabulariesResponse(vocabulary);
        if (userId != null) {
            List<UserVocabularyProgress> progresses = userVocabularyProgressRepository.findByUserIdAndMeaning_Vocabulary_IdIn(userId, List.of(id));
            Map<Integer, UserVocabularyProgress> progressMap = progresses.stream()
                .collect(Collectors.toMap(p -> p.getMeaning().getId(), p -> p));
            if (response.getMeanings() != null) {
                response.getMeanings().forEach(m -> {
                    UserVocabularyProgress prog = progressMap.get(m.getId());
                    if (prog != null) {
                        m.setUserProgress(com.tqt.englishApp.dto.response.UserMeaningProgressResponse.builder()
                            .status(prog.getStatus().name().toLowerCase())
                            .nextReviewAt(prog.getNextReviewAt() != null ? prog.getNextReviewAt().toLocalDate() : null)
                            .build());
                    } else {
                        m.setUserProgress(com.tqt.englishApp.dto.response.UserMeaningProgressResponse.builder()
                            .status("not_started")
                            .nextReviewAt(null)
                            .build());
                    }
                });
            }
        }
        return response;
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

    @Transactional
    public Page<VocabulariesSimpleResponse> getSaveVocabularies(String userId, Map<String, String> params) {
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(PAGE_SIZE)));

        page = Math.max(0, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<UserSavedVocabulary> result = userSavedVocabularyRepository.findByUserId(userId, pageable);

        return result.map(saved -> {
            VocabulariesSimpleResponse res = vocabularyMapper.toVocabulariesSimpleResponse(saved.getVocabulary());
            return res;
        });
    }

    public void toggleSaveVocabulary(Integer id, String userId) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOCABULARY_NOT_EXISTED));

        Optional<UserSavedVocabulary> existing = 
            userSavedVocabularyRepository.findByUserIdAndVocabularyId(userId, id);

        if (existing.isPresent()) {
            userSavedVocabularyRepository.delete(existing.get());
        } else {
            userSavedVocabularyRepository.save(UserSavedVocabulary.builder()
                    .userId(userId)
                    .vocabulary(vocabulary)
                    .build());
        }
    }

}
