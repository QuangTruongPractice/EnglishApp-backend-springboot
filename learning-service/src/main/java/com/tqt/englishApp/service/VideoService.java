package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.SubtitlesResponse;
import com.tqt.englishApp.dto.response.VideoData;
import com.tqt.englishApp.dto.response.VideoResponse;
import com.tqt.englishApp.entity.Subtitles;
import com.tqt.englishApp.entity.Video;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.SubtitlesMapper;
import com.tqt.englishApp.mapper.VideoMapper;
import com.tqt.englishApp.repository.SubtitlesRepository;
import com.tqt.englishApp.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import com.tqt.englishApp.repository.UserVideoProgressRepository;
import com.tqt.englishApp.entity.UserVideoProgress;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private SubtitlesRepository subtitlesRepository;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private SubtitlesMapper subtitlesMapper;

    @Autowired
    private UserVideoProgressRepository userVideoProgressRepository;

    private static final int PAGE_SIZE = 10;

    @Transactional
    public void saveVideoAndSubtitles(VideoData response) {
        Video video = videoMapper.toVideo(response.getVideo());
        videoRepository.save(video);

        if (response.getSubtitles() != null && !response.getSubtitles().isEmpty()) {
            for (SubtitlesResponse subtitleData : response.getSubtitles()) {
                Subtitles subtitle = subtitlesMapper.toSubtitles(subtitleData);
                subtitle.setVideo(video);
                subtitlesRepository.save(subtitle);
            }
        }
    }

    public Page<VideoResponse> getVideos(Map<String, String> params, String userId) {
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

        Page<VideoResponse> responsePage = result.map(videoMapper::toVideoResponse);

        if (userId != null) {
            List<Integer> videoIds = responsePage.getContent().stream().map(VideoResponse::getId).collect(Collectors.toList());
            if (!videoIds.isEmpty()) {
                List<UserVideoProgress> progresses = userVideoProgressRepository.findByUserIdAndVideoIdIn(userId, videoIds);
                Map<Integer, UserVideoProgress> progressMap = progresses.stream()
                        .collect(Collectors.toMap(p -> p.getVideo().getId(), p -> p));

                responsePage.getContent().forEach(resp -> {
                    UserVideoProgress p = progressMap.get(resp.getId());
                    if (p != null) {
                        resp.setProgressPercentage(p.getProgressPercentage());
                        resp.setIsCompleted(p.getIsCompleted());
                    } else {
                        resp.setProgressPercentage(0.0);
                        resp.setIsCompleted(false);
                    }
                });
            }
        }

        return responsePage;
    }

    public VideoResponse getVideoById(Integer id, String userId) {
        VideoResponse response = videoMapper.toVideoResponse(videoRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED)));

        if (userId != null) {
            UserVideoProgress progress = userVideoProgressRepository.findByUserIdAndVideoId(userId, id).orElse(null);
            if (progress != null) {
                response.setProgressPercentage(progress.getProgressPercentage());
                response.setIsCompleted(progress.getIsCompleted());
            } else {
                response.setProgressPercentage(0.0);
                response.setIsCompleted(false);
            }
        }

        return response;
    }

    public void deleteVideo(Integer id) {
        videoRepository.deleteById(id);
    }

    public Long countVideo() {
        return videoRepository.count();
    }

}
