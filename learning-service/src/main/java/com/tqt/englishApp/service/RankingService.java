package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.LeaderBoardResponse;
import com.tqt.englishApp.dto.response.LeaderBoardWrapperResponse;
import com.tqt.englishApp.repository.UserVocabularyProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RankingService {
    @Autowired
    private UserVocabularyProgressRepository userVocabularyProgressRepository;

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
