package com.tqt.englishApp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsAdminResponse;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsDetailResponse;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsResponse;
import com.tqt.englishApp.dto.response.mainTopic.UserTopicProgressResponse;
import com.tqt.englishApp.dto.request.MainTopicRequest;
import com.tqt.englishApp.entity.MainTopic;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.enums.LearningGoal;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.MainTopicMapper;
import com.tqt.englishApp.repository.MainTopicRepository;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class MainTopicService {
    @Autowired
    private MainTopicRepository mainTopicRepository;

    @Autowired
    private MainTopicMapper mainTopicMapper;

    @Autowired
    private UserLearningProfileRepository userLearningProfileRepository;

    @Autowired
    private VocabularyService vocabularyService;

    @Autowired
    private SubTopicService subTopicService;

    @Autowired
    private Cloudinary cloudinary;
    private static final int PAGE_SIZE = 8;

    public MainTopicsAdminResponse createMainTopic(MainTopicRequest request) {
        MultipartFile image = request.getImage();
        if (image == null || image.isEmpty()) {
            throw new AppException(ErrorCode.IMAGE_REQUIRED);
        }
        MainTopic topic = mainTopicMapper.toMainTopic(request);
        try {
            Map res = cloudinary.uploader().upload(
                    image.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));
            topic.setImage(res.get("secure_url").toString());
        } catch (IOException ex) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        return mainTopicMapper.toMainTopicsAdminResponse(mainTopicRepository.save(topic));
    }

    public MainTopicsAdminResponse updateMainTopic(Integer topicId, MainTopicRequest request) {
        MultipartFile image = request.getImage();
        MainTopic topic = mainTopicRepository.findById(topicId)
                .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED));
        mainTopicMapper.updateMainTopic(topic, request);
        if (image != null && !image.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(
                        image.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                topic.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
        }
        return mainTopicMapper.toMainTopicsAdminResponse(mainTopicRepository.save(topic));
    }

    @Transactional
    public Page<MainTopicsAdminResponse> getMainTopicsForAdmin(Map<String, String> params) {
        String name = params.get("name");
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(PAGE_SIZE)));

        page = Math.max(0, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<MainTopic> result;

        if (name == null || name.trim().isEmpty()) {
            result = mainTopicRepository.findAll(pageable);
        } else {
            result = mainTopicRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        }

        return result.map(mainTopicMapper::toMainTopicsAdminResponse);
    }

    @Transactional
    public MainTopicsAdminResponse getMainTopicByIdForAdmin(int id) {
        return mainTopicMapper.toMainTopicsAdminResponse(
                mainTopicRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED)));
    }

    public Long countMainTopic() {
        return mainTopicRepository.count();
    }

    @Transactional
    public List<MainTopicsAdminResponse> findAll() {
        return mainTopicMapper.toMainTopicsAdminResponse(mainTopicRepository.findAll());
    }

    @Transactional
    public void deleteMainTopic(int id) {
        mainTopicRepository.deleteVocabularySubTopicRelationsByMainTopic(id);
        mainTopicRepository.deleteById(id);
        vocabularyService.deleteAllVocabulary();
    }

    @Transactional
    public List<MainTopicsResponse> getLearningPathForClient(String userId) {
        UserLearningProfile profile = userLearningProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        LearningGoal goal = profile.getGoal();
        if (goal == null) {
            return List.of();
        }
        List<MainTopic> topics = mainTopicRepository.findByGoalOrderByTopicOrderAsc(goal);

        List<MainTopicsResponse> responses = mainTopicMapper.toMainTopicsResponse(topics);
        for (MainTopicsResponse res : responses) {
            res.setUserProgress(calculateProgress(res.getId(), userId));
        }

        return responses;
    }

    @Transactional
    public Page<MainTopicsResponse> getMainTopicsForClient(Map<String, String> params, String userId) {
        String name = params.get("name");
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(PAGE_SIZE)));

        page = Math.max(0, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<MainTopic> result;

        if (name == null || name.trim().isEmpty()) {
            result = mainTopicRepository.findAll(pageable);
        } else {
            result = mainTopicRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        }

        Page<MainTopicsResponse> responsePage = result.map(mainTopicMapper::toMainTopicsResponse);
        responsePage.forEach(res -> res.setUserProgress(calculateProgress(res.getId(), userId)));
        return responsePage;
    }

    @Transactional
    public MainTopicsDetailResponse getMainTopicDetailForClient(int id, String userId) {
        MainTopicsDetailResponse response = mainTopicMapper.toMainTopicsDetailResponse(
                mainTopicRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED)));
        response.setUserProgress(calculateProgress(id, userId));
        
        if (response.getSubTopics() != null && !response.getSubTopics().isEmpty()) {
            response.getSubTopics().forEach(st -> {
                st.setUserProgress(subTopicService.calculateProgress(st.getId(), userId));
            });
        }
        
        return response;
    }

    private UserTopicProgressResponse calculateProgress(Integer mainTopicId, String userId) {
        if (userId == null) return null;
        Long total = mainTopicRepository.countTotalMeaningsByMainTopic(mainTopicId);
        if (total == null || total == 0) return null;

        Long learned = mainTopicRepository.countLearnedMeaningsByMainTopic(mainTopicId, userId);
        int pct = (int) ((learned * 100) / total);
        
        String status = "not_started";
        if (learned > 0 && learned < total) {
            status = "active";
        } else if (learned.equals(total)) {
            status = "completed";
        }

        return UserTopicProgressResponse.builder()
                .meanings_learned(learned)
                .meanings_total(total)
                .pct(pct)
                .status(status)
                .build();
    }
}
