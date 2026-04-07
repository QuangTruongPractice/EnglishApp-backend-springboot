package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.VideoProgressRequest;
import com.tqt.englishApp.dto.response.UserVideoResponse;
import com.tqt.englishApp.entity.UserQuizProgress;
import com.tqt.englishApp.entity.UserVideoProgress;
import com.tqt.englishApp.mapper.UserVideoMapper;
import com.tqt.englishApp.repository.QuizRepository;
import com.tqt.englishApp.repository.UserQuizProgressRepository;
import com.tqt.englishApp.repository.UserVideoProgressRepository;
import com.tqt.englishApp.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ContentProgressService {
    @Autowired
    private UserVideoProgressRepository userVideoProgressRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserQuizProgressRepository userQuizProgressRepository;

    @Autowired
    private UserVideoMapper userVideoMapper;

    public UserVideoProgress updateVideoProgress(VideoProgressRequest request) {
        UserVideoProgress progress = userVideoProgressRepository
                .findByUserIdAndVideoId(request.getUserId(), request.getVideoId())
                .orElse(UserVideoProgress.builder()
                        .userId(request.getUserId())
                        .video(videoRepository.findById(request.getVideoId()).orElse(null))
                        .build());

        if (!Boolean.TRUE.equals(progress.getIsCompleted())) {
            if (request.getWatchedDuration() != null) {
                if (progress.getWatchedDuration() == null || request.getWatchedDuration() > progress.getWatchedDuration()) {
                    progress.setWatchedDuration(request.getWatchedDuration());
                }
            }

            if (request.getVideoDuration() != null && request.getVideoDuration() > 0) {
                double percentage = (double) progress.getWatchedDuration() / request.getVideoDuration() * 100;
                progress.setProgressPercentage(Math.min(100.0, Math.round(percentage * 10.0) / 10.0));
            }
        }

        if (request.getLastPosition() != null) {
            progress.setLastPosition(request.getLastPosition());
        }

        return userVideoProgressRepository.save(progress);
    }

    public Page<UserVideoResponse> getUserVideoProgress(String userId, Map<String, String> params) {
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", "10"));
        page = Math.max(0, page);
        Pageable pageable = PageRequest.of(page, size);

        Page<UserVideoProgress> progressPage = userVideoProgressRepository.findByUserId(userId, pageable);
        return progressPage.map(userVideoMapper::toUserVideoResponse);
    }

    public UserQuizProgress updateQuizProgress(String userId, Integer quizId) {
        UserQuizProgress progress = userQuizProgressRepository.findByUserIdAndQuizId(userId, quizId)
                .orElse(UserQuizProgress.builder()
                        .userId(userId)
                        .quiz(quizRepository.findById(quizId).orElse(null))
                        .count(0)
                        .build());

        progress.setCount(progress.getCount() + 1);

        return userQuizProgressRepository.save(progress);
    }
}
