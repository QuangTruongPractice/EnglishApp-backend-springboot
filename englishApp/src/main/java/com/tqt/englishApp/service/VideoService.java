package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.VideoRequest;
import com.tqt.englishApp.dto.response.VideoResponse;
import com.tqt.englishApp.entity.Video;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.VideoMapper;
import com.tqt.englishApp.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoMapper videoMapper;

    private static final int PAGE_SIZE = 10;

    public Page<VideoResponse> getVideos(Map<String, String> params){
        String title = params.get("title");
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(PAGE_SIZE)));

        page = Math.max(0, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<Video> result;

        if (title == null || title.trim().isEmpty()) {
            result = videoRepository.findAll(pageable);
        } else {
            result = videoRepository.findByTitleContainingIgnoreCase(title.trim(), pageable);
        }

        return result.map(videoMapper::toVideoResponse);
    }

    public VideoResponse getVideoById(Integer id){
        return videoMapper.toVideoResponse(videoRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED)));
    }

    public void deleteVideo(Integer id){
        videoRepository.deleteById(id);
    }

    public Long countVideo(){
        return videoRepository.count();
    }

}
