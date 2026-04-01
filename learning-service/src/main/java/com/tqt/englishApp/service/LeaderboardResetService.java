package com.tqt.englishApp.service;

import com.tqt.englishApp.repository.UserLearningProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardResetService {
    private final UserLearningProfileRepository profileRepository;

    // Reset every Monday at 00:00
    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void resetWeeklyXp() {
        log.info("Resetting all users' weekly XP for the new week...");
        profileRepository.findAll().forEach(profile -> {
            profile.setWeeklyXp(0);
            profileRepository.save(profile);
        });
        log.info("Weekly XP reset completed.");
    }
}
