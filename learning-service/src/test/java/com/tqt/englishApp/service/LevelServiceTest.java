package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.Session;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.enums.Level;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LevelServiceTest {

    @InjectMocks
    private LevelService levelService;

    @Mock
    private UserLearningProfileRepository profileRepository;

    private UserLearningProfile profile;
    private Session session;

    @BeforeEach
    void setUp() {
        profile = new UserLearningProfile();
        profile.setLevel(Level.A1);
        profile.setXp(0);
        profile.setTotalXp(0);
        profile.setWeeklyXp(0);

        session = new Session();
        session.setIsLevelUp(false);
    }

    // -----------------------------------------------------------------------
    // addXpAndCheckLevelUp
    // -----------------------------------------------------------------------
    @Test
    void addXpAndCheckLevelUp_NoLevelUp_XpAccumulated() {
        levelService.addXpAndCheckLevelUp(profile, session, 100);

        assertEquals(100, profile.getXp());
        assertEquals(100, profile.getTotalXp());
        assertEquals(100, profile.getWeeklyXp());
        assertFalse(session.getIsLevelUp());
    }

    @Test
    void addXpAndCheckLevelUp_LevelUp_SetsSessionFlag() {
        profile.setXp(490);
        profile.setTotalXp(490);
        profile.setWeeklyXp(490);
        when(profileRepository.save(any())).thenReturn(profile);

        boolean result = levelService.addXpAndCheckLevelUp(profile, session, 20); // 510 >= 500

        assertTrue(result);
        assertTrue(session.getIsLevelUp());
        assertEquals(Level.A2, profile.getLevel());
    }

    @Test
    void addXpAndCheckLevelUp_NullSession_DoesNotThrow() {
        assertDoesNotThrow(() -> levelService.addXpAndCheckLevelUp(profile, null, 50));
        assertEquals(50, profile.getXp());
    }

    // -----------------------------------------------------------------------
    // syncUserLevel
    // -----------------------------------------------------------------------
    @Test
    void syncUserLevel_BelowThreshold_NoChange() {
        profile.setXp(100);
        profile.setLevel(Level.A1);

        boolean leveled = levelService.syncUserLevel(profile);

        assertFalse(leveled);
        assertEquals(Level.A1, profile.getLevel());
        verify(profileRepository, never()).save(any());
    }

    @Test
    void syncUserLevel_ExactThreshold_LevelsUp() {
        profile.setXp(500); // A1 threshold
        profile.setLevel(Level.A1);
        when(profileRepository.save(any())).thenReturn(profile);

        boolean leveled = levelService.syncUserLevel(profile);

        assertTrue(leveled);
        assertEquals(Level.A2, profile.getLevel());
        assertEquals(0, profile.getXp());
        verify(profileRepository).save(profile);
    }

    @Test
    void syncUserLevel_MultiLevelUp_SkipsMultipleLevels() {
        // Giả sử người dùng có rất nhiều XP: A1(500) + A2(1000) + extra
        profile.setXp(1600);
        profile.setLevel(Level.A1);
        when(profileRepository.save(any())).thenReturn(profile);

        boolean leveled = levelService.syncUserLevel(profile);

        assertTrue(leveled);
        assertEquals(Level.B1, profile.getLevel()); // A1→A2→B1
        assertEquals(100, profile.getXp()); // 1600 - 500 - 1000 = 100
    }

    @Test
    void syncUserLevel_AtMaxLevel_C2_NoLevelUp() {
        profile.setXp(99999);
        profile.setLevel(Level.C2);

        boolean leveled = levelService.syncUserLevel(profile);

        assertFalse(leveled); // C2 returns -1 threshold → no level up
        assertEquals(Level.C2, profile.getLevel());
    }

    @Test
    void syncUserLevel_A2Level_LevelsUpToB1() {
        profile.setXp(1200);
        profile.setLevel(Level.A2); // threshold = 1000
        when(profileRepository.save(any())).thenReturn(profile);

        boolean leveled = levelService.syncUserLevel(profile);

        assertTrue(leveled);
        assertEquals(Level.B1, profile.getLevel());
        assertEquals(200, profile.getXp());
    }

    @Test
    void syncUserLevel_B1Level_LevelsUpToB2() {
        profile.setXp(2500);
        profile.setLevel(Level.B1); // threshold = 2000
        when(profileRepository.save(any())).thenReturn(profile);

        boolean leveled = levelService.syncUserLevel(profile);

        assertTrue(leveled);
        assertEquals(Level.B2, profile.getLevel());
        assertEquals(500, profile.getXp());
    }

    @Test
    void syncUserLevel_B2Level_LevelsUpToC1() {
        profile.setXp(4001);
        profile.setLevel(Level.B2); // threshold = 4000
        when(profileRepository.save(any())).thenReturn(profile);

        boolean leveled = levelService.syncUserLevel(profile);

        assertTrue(leveled);
        assertEquals(Level.C1, profile.getLevel());
        assertEquals(1, profile.getXp());
    }

    @Test
    void syncUserLevel_C1Level_LevelsUpToC2() {
        profile.setXp(10000);
        profile.setLevel(Level.C1); // threshold = 10000
        when(profileRepository.save(any())).thenReturn(profile);

        boolean leveled = levelService.syncUserLevel(profile);

        assertTrue(leveled);
        assertEquals(Level.C2, profile.getLevel());
        assertEquals(0, profile.getXp());
    }
}
