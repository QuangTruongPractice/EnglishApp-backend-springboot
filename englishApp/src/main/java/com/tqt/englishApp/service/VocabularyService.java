package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.VocabularyRequest;
import com.tqt.englishApp.dto.response.SubTopicResponse;
import com.tqt.englishApp.dto.response.VocabularyResponse;
import com.tqt.englishApp.entity.SubTopic;
import com.tqt.englishApp.entity.Vocabulary;
import com.tqt.englishApp.entity.WordType;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.VocabularyMapper;
import com.tqt.englishApp.repository.SubTopicRepository;
import com.tqt.englishApp.repository.VocabularyRepository;
import com.tqt.englishApp.repository.WordTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class VocabularyService {
    @Autowired
    private SubTopicRepository subTopicRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private WordTypeRepository wordTypeRepository;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    private static final int PAGE_SIZE = 10;

    public VocabularyResponse createVocabulary(VocabularyRequest request){
        Vocabulary vocabulary = vocabularyMapper.toVocabulary(request);
        List<SubTopic> subTopics = subTopicRepository.findAllById(request.getSubTopicIds());
        if (subTopics.isEmpty()) {
            throw new AppException(ErrorCode.TOPIC_NOT_EXISTED);
        }
        vocabulary.setSubTopics(subTopics);
        List<WordType> wordTypes = wordTypeRepository.findAllById(request.getWordTypeIds());
        if (wordTypes.isEmpty()) {
            throw new AppException(ErrorCode.WORDTYPE_NOT_EXISTED);
        }
        vocabulary.setWordTypes(wordTypes);
        return vocabularyMapper.toVocabularyResponse(vocabularyRepository.save(vocabulary));
    }

    public VocabularyResponse updateVocabulary(Integer vocabularyId, VocabularyRequest request){
        Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
                .orElseThrow(() -> new AppException(ErrorCode.VOCABULARY_NOT_EXISTED));
        vocabularyMapper.updateVocabulary(vocabulary, request);
        List<SubTopic> subTopics = subTopicRepository.findAllById(request.getSubTopicIds());
        if (subTopics.isEmpty()) {
            throw new AppException(ErrorCode.TOPIC_NOT_EXISTED);
        }
        vocabulary.setSubTopics(subTopics);
        List<WordType> wordTypes = wordTypeRepository.findAllById(request.getWordTypeIds());
        if (wordTypes.isEmpty()) {
            throw new AppException(ErrorCode.WORDTYPE_NOT_EXISTED);
        }
        vocabulary.setWordTypes(wordTypes);
        return vocabularyMapper.toVocabularyResponse(vocabularyRepository.save(vocabulary));
    }

    public Page<VocabularyResponse> getVocabularies(Map<String, String> params){
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

        return result.map(vocabularyMapper::toVocabularyResponse);
    }

    public VocabularyResponse getVocabularyById(Integer id){
        return vocabularyMapper.toVocabularyResponse(vocabularyRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOCABULARY_NOT_EXISTED)));
    }

    public void deleteVocabulary(Integer id){
        vocabularyRepository.deleteById(id);
    }

}
