package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.SubTopicRequest;
import com.tqt.englishApp.dto.response.subTopic.SubTopicsAdminResponse;
import com.tqt.englishApp.dto.response.subTopic.SubTopicsDetailResponse;
import com.tqt.englishApp.entity.MainTopic;
import com.tqt.englishApp.entity.SubTopic;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.SubTopicMapper;
import com.tqt.englishApp.repository.MainTopicRepository;
import com.tqt.englishApp.repository.SubTopicRepository;
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

    public SubTopicsAdminResponse createSubTopic(SubTopicRequest request) {
        SubTopic subTopic = subTopicMapper.toSubTopic(request);
        if (request.getMainTopic() != null) {
            MainTopic mainTopic = mainTopicRepository.findById(request.getMainTopic())
                    .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED));
            subTopic.setMainTopic(mainTopic);
        }
        return subTopicMapper.toSubTopicsAdminResponse(subTopicRepository.save(subTopic));
    }

    public SubTopicsAdminResponse updateSubTopic(Integer subTopicId, SubTopicRequest request) {
        SubTopic subTopic = subTopicRepository.findById(subTopicId)
                .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED));
        subTopicMapper.updateSubTopic(subTopic, request);
        if (request.getMainTopic() != null) {
            MainTopic mainTopic = mainTopicRepository.findById(request.getMainTopic())
                    .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED));
            subTopic.setMainTopic(mainTopic);
        }
        return subTopicMapper.toSubTopicsAdminResponse(subTopicRepository.save(subTopic));
    }

    @Transactional
    public Page<SubTopicsAdminResponse> getSubTopics(Map<String, String> params) {
        String name = params.get("name");
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(PAGE_SIZE)));

        page = Math.max(0, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<SubTopic> result;

        if (name == null || name.trim().isEmpty()) {
            result = subTopicRepository.findAll(pageable);
        } else {
            result = subTopicRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        return result.map(subTopicMapper::toSubTopicsAdminResponse);
    }

    @Transactional
    public SubTopicsAdminResponse getSubTopicDetailForAdmin(Integer id) {
        return subTopicMapper.toSubTopicsAdminResponse(
                subTopicRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED)));
    }

    @Transactional
    public SubTopicsDetailResponse getSubTopicDetailForClient(Integer id, String userId) {
        SubTopicsDetailResponse response = subTopicMapper.toSubTopicsDetailResponse(
                subTopicRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED)));
        response.setUserProgress(calculateProgress(id, userId));
        return response;
    }

    public Long countSubTopic() {
        return subTopicRepository.count();
    }

    @Transactional
    public List<SubTopicsAdminResponse> findAll() {
        return subTopicMapper.toSubTopicsAdminResponse(subTopicRepository.findAll());
    }

    @Transactional
    public void deleteSubTopic(Integer id) {
        subTopicRepository.deleteById(id);
        vocabularyService.deleteAllVocabulary();
    }

    public com.tqt.englishApp.dto.response.mainTopic.UserTopicProgressResponse calculateProgress(Integer subTopicId, String userId) {
        if (userId == null) return null;

        Long total = subTopicRepository.countTotalMeaningsBySubTopic(subTopicId);
        if (total == null || total == 0) return null;

        Long learned = subTopicRepository.countLearnedMeaningsBySubTopic(subTopicId, userId);
        if (learned == null) learned = 0L;

        int pct = (int) Math.round((learned.doubleValue() / total) * 100);
        String status = "not_started";
        if (pct == 100) status = "completed";
        else if (pct > 0) status = "active";

        return com.tqt.englishApp.dto.response.mainTopic.UserTopicProgressResponse.builder()
                .meanings_learned(learned)
                .meanings_total(total)
                .pct(pct)
                .status(status)
                .build();
    }

}
