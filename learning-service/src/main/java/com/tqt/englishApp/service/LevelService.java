package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.Session;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.enums.Level;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LevelService {
    private final UserLearningProfileRepository profileRepository;

    public boolean addXpAndCheckLevelUp(UserLearningProfile profile, Session session, int xpToAdd) {
        int currentXp = profile.getXp() + xpToAdd;
        profile.setWeeklyXp(profile.getWeeklyXp() + xpToAdd);
        profile.setTotalXp(profile.getTotalXp() + xpToAdd);
        Level currentLevel = profile.getLevel();
        boolean leveledUp = false;

        while (true) {
            int threshold = getThreshold(currentLevel);
            if (threshold > 0 && currentXp >= threshold) {
                currentXp -= threshold;
                currentLevel = getNextLevel(currentLevel);
                leveledUp = true;
            } else {
                break;
            }
        }

        profile.setXp(currentXp);
        profile.setLevel(currentLevel);
        profileRepository.save(profile);

        if (leveledUp) {
            session.setIsLevelUp(true);
        }
        
        return leveledUp;
    }

    private int getThreshold(Level level) {
        switch (level) {
            case A1: return 500;
            case A2: return 1000;
            case B1: return 2000;
            case B2: return 4000;
            case C1: return 10000;
            default: return -1; // Max level
        }
    }

    private Level getNextLevel(Level level) {
        int nextOrdinal = level.ordinal() + 1;
        if (nextOrdinal < Level.values().length) {
            return Level.values()[nextOrdinal];
        }
        return level;
    }
}
