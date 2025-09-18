package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.SubTopicRequest;
import com.tqt.englishApp.dto.response.MainTopicResponse;
import com.tqt.englishApp.dto.response.SubTopicResponse;
import com.tqt.englishApp.entity.MainTopic;
import com.tqt.englishApp.entity.SubTopic;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.SubTopicMapper;
import com.tqt.englishApp.repository.MainTopicRepository;
import com.tqt.englishApp.repository.SubTopicRepository;
import com.tqt.englishApp.repository.VocabularyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SubTopicService {
    @Autowired
    private SubTopicMapper subTopicMapper;

    @Autowired
    private SubTopicRepository subTopicRepository;

    @Autowired
    private MainTopicRepository mainTopicRepository;

    @Autowired
    private VocabularyService vocabularyService;

    private static final int PAGE_SIZE = 10;

    public SubTopicResponse createSubTopic(SubTopicRequest request){
        SubTopic subTopic = subTopicMapper.toSubTopic(request);
        MainTopic mainTopic = mainTopicRepository.findById(request.getMainTopic())
                .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED));
        subTopic.setMainTopic(mainTopic);
        return subTopicMapper.toSubTopicResponse(subTopicRepository.save(subTopic));
    }

    public SubTopicResponse updateSubTopic(Integer subTopicId, SubTopicRequest request){
        SubTopic subTopic = subTopicRepository.findById(subTopicId)
                .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED));
        subTopicMapper.updateSubTopic(subTopic, request);
        MainTopic mainTopic = mainTopicRepository.findById(request.getMainTopic())
                .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED));
        subTopic.setMainTopic(mainTopic);
        return subTopicMapper.toSubTopicResponse(subTopicRepository.save(subTopic));
    }

    public Page<SubTopicResponse> getSubTopics(Map<String, String> params){
        String name = params.get("name");
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(PAGE_SIZE)));

        page = Math.max(0, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<SubTopic> result;

        if (name == null || name.trim().isEmpty()) {
            result = subTopicRepository.findAll(pageable);
        } else {
            result = subTopicRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        }

        return result.map(subTopicMapper::toSubTopicResponse);
    }

    public SubTopicResponse getSubTopicById(Integer id){
        return subTopicMapper.toSubTopicResponse(subTopicRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED)));
    }

    public Long countSubTopic(){
        return subTopicRepository.count();
    }

    public List<SubTopicResponse> findAll(){
        return subTopicMapper.toSubTopicResponse(subTopicRepository.findAll());
    }

    @Transactional
    public void deleteSubTopic(Integer id) {
        subTopicRepository.deleteVocabularySubTopicRelations(id);
        subTopicRepository.deleteById(id);
        vocabularyService.deleteAllVocabulary();
    }

}
