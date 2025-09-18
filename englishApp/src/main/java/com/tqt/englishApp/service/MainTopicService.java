package com.tqt.englishApp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tqt.englishApp.dto.request.MainTopicRequest;
import com.tqt.englishApp.dto.response.MainTopicResponse;
import com.tqt.englishApp.entity.MainTopic;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.MainTopicMapper;
import com.tqt.englishApp.repository.MainTopicRepository;
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
    private VocabularyService vocabularyService;

    @Autowired
    private Cloudinary cloudinary;
    private static final int PAGE_SIZE = 8;

    public MainTopicResponse createMainTopic(MainTopicRequest request){
        MultipartFile image = request.getImage();
        if (image == null || image.isEmpty()) {
            throw new AppException(ErrorCode.IMAGE_REQUIRED);
        }
        MainTopic topic =  mainTopicMapper.toMainTopic(request);
        if (!image.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(
                        image.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto")
                );
                topic.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
        return mainTopicMapper.toMainTopicResponse(mainTopicRepository.save(topic));
    }

    public MainTopicResponse updateMainTopic(Integer topicId, MainTopicRequest request){
        MultipartFile image = request.getImage();
        MainTopic topic = mainTopicRepository.findById(topicId)
                .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED));
        mainTopicMapper.updateMainTopic(topic, request);
        if (!image.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(
                        image.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto")
                );
                topic.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
        return mainTopicMapper.toMainTopicResponse(mainTopicRepository.save(topic));
    }

    public Page<MainTopicResponse> getMainTopics(Map<String, String> params){
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

        return result.map(mainTopicMapper::toMainTopicResponse);
    }

    public MainTopicResponse getMainTopicById(int id){
        return mainTopicMapper.toMainTopicResponse(mainTopicRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_EXISTED)));
    }

    public Long countMainTopic(){
        return mainTopicRepository.count();
    }

    public List<MainTopicResponse> findAll(){
        return mainTopicMapper.toMainTopicResponse(mainTopicRepository.findAll());
    }

    @Transactional
    public void deleteMainTopic(int id) {
        mainTopicRepository.deleteVocabularySubTopicRelationsByMainTopic(id);
        mainTopicRepository.deleteById(id);
        vocabularyService.deleteAllVocabulary();
    }
}
