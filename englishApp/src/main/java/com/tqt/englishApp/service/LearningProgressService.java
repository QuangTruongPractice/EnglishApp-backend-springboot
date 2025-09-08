package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.VideoProgressRequest;
import com.tqt.englishApp.dto.request.VocabularyProgressRequest;
import com.tqt.englishApp.dto.response.LeaderBoardResponse;
import com.tqt.englishApp.dto.response.LeaderBoardWrapperResponse;
import com.tqt.englishApp.dto.response.UserVideoResponse;
import com.tqt.englishApp.dto.response.UserVocabularyResponse;
import com.tqt.englishApp.entity.UserVideoProgress;
import com.tqt.englishApp.entity.UserVocabularyProgress;
import com.tqt.englishApp.enums.VocabularyStatus;
import com.tqt.englishApp.mapper.UserVideoMapper;
import com.tqt.englishApp.mapper.UserVocabularyMapper;
import com.tqt.englishApp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LearningProgressService {
    @Autowired
    private UserVocabularyProgressRepository userVocabularyProgressRepository;

    @Autowired
    private UserVideoProgressRepository userVideoProgressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VocabularyRepository  vocabularyRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserVideoMapper userVideoMapper;

    @Autowired
    private UserVocabularyMapper userVocabularyMapper;

    public UserVocabularyProgress updateVocabularyProgress(VocabularyProgressRequest request){
        UserVocabularyProgress progress = userVocabularyProgressRepository
                .findByUserIdAndVocabularyId(request.getUserId(), request.getVocabularyId())
                .orElse(UserVocabularyProgress.builder()
                        .user(userRepository.findById(request.getUserId()).orElse(null))
                        .vocabulary(vocabularyRepository.findById(request.getVocabularyId()).orElse(null))
                        .build());

        if (request.getViewedFlashcard() != null) {
            progress.setViewedFlashcard(request.getViewedFlashcard());
        } else if (progress.getViewedFlashcard() == null) {
            progress.setViewedFlashcard(false);
        }

        if (request.getPracticedPronunciation() != null) {
            progress.setPracticedPronunciation(request.getPracticedPronunciation());
        } else if (progress.getPracticedPronunciation() == null) {
            progress.setPracticedPronunciation(false);
        }
        return userVocabularyProgressRepository.save(progress);
    }

    public UserVideoProgress updateVideoProgress(VideoProgressRequest request){
        UserVideoProgress progress = userVideoProgressRepository
                .findByUserIdAndVideoId(request.getUserId(), request.getVideoId())
                .orElse(UserVideoProgress.builder()
                        .user(userRepository.findById(request.getUserId()).orElse(null))
                        .video(videoRepository.findById(request.getVideoId()).orElse(null))
                        .build());
        if (request.getWatchedDuration() != null) {
            progress.setWatchedDuration(request.getWatchedDuration());
        }
        if (request.getLastPosition() != null) {
            progress.setLastPosition(request.getLastPosition());
        }

        if (request.getVideoDuration() != null && request.getVideoDuration() > 0) {
            double percentage = (double) progress.getWatchedDuration() / request.getVideoDuration() * 100;
            progress.setProgressPercentage(Math.min(100.0, Math.round(percentage * 10.0) / 10.0));
        }

        return userVideoProgressRepository.save(progress);
    }

    public List<UserVocabularyResponse> getUserVocabularyProgress(String userId) {
        return userVocabularyMapper.toUserVocabularyResponse(userVocabularyProgressRepository.findByUserId(userId));
    }

    public List<UserVideoResponse> getUserVideoProgress(String userId) {
        return userVideoMapper.toUserVideoResponse(userVideoProgressRepository.findByUserId(userId));
    }

    public LeaderBoardWrapperResponse getLeaderBoardWithCurrentUser(String userId) {
        List<LeaderBoardResponse> rawResult = userVocabularyProgressRepository.getUserRanking();
        List<LeaderBoardResponse> leaderBoard = new ArrayList<>();
        LeaderBoardResponse currentUser = null;
        for (LeaderBoardResponse item : rawResult) {
            leaderBoard.add(item);

            if (item.getUserId().equals(userId)) {
                currentUser = item;
            }
        }

        return new LeaderBoardWrapperResponse(leaderBoard, currentUser);
    }

}
