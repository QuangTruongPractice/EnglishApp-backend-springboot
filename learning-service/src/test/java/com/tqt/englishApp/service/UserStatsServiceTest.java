package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.LearningSummaryResponse;
import com.tqt.englishApp.dto.response.StreakCalendarResponse;
import com.tqt.englishApp.entity.Session;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.enums.VocabularyStatus;
import com.tqt.englishApp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserStatsServiceTest {

    @InjectMocks
    private UserStatsService service;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserLearningProfileRepository profileRepository;

    @Mock
    private UserVocabularyProgressRepository vocabularyProgressRepository;

    @Mock
    private UserVideoProgressRepository videoProgressRepository;

    @Mock
    private UserSavedVocabularyRepository savedVocabularyRepository;

    private Session buildSession(LocalDate date, int xp) {
        Session s = new Session();
        s.setDate(date);
        s.setTotalXP(xp);
        return s;
    }

    // -----------------------------------------------------------------------
    // getStreakCalendar
    // -----------------------------------------------------------------------
    @Test
    void getStreakCalendar_ReturnsStudiedDatesWithXpAboveZero() {
        LocalDate today = LocalDate.now();
        List<Session> sessions = List.of(
                buildSession(today, 50),
                buildSession(today.minusDays(1), 0) // không tính (XP=0)
        );
        when(sessionRepository.findByUserIdAndDateBetween(eq("user-1"), any(), any()))
                .thenReturn(sessions);
        when(sessionRepository.findByUserIdOrderByDateDesc("user-1")).thenReturn(sessions);

        StreakCalendarResponse result = service.getStreakCalendar("user-1", today.getMonthValue(), today.getYear());

        assertNotNull(result);
        assertEquals(1, result.getStudiedDates().size());
        assertTrue(result.getStudiedDates().contains(today));
    }

    @Test
    void getStreakCalendar_EmptySessions_ReturnsZeroStreaks() {
        when(sessionRepository.findByUserIdAndDateBetween(anyString(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(sessionRepository.findByUserIdOrderByDateDesc(anyString()))
                .thenReturn(Collections.emptyList());

        StreakCalendarResponse result = service.getStreakCalendar("user-1", 4, 2026);

        assertEquals(0, result.getCurrentStreak());
        assertEquals(0, result.getLongestStreak());
        assertTrue(result.getStudiedDates().isEmpty());
    }

    // -----------------------------------------------------------------------
    // getLearningSummary
    // -----------------------------------------------------------------------
    @Test
    void getLearningSummary_ReturnsMergedStats() {
        UserLearningProfile profile = new UserLearningProfile();
        profile.setUserId("user-1");
        profile.setTotalXp(5000);

        when(profileRepository.findByUserId("user-1")).thenReturn(java.util.Optional.of(profile));
        when(sessionRepository.findByUserIdOrderByDateDesc("user-1")).thenReturn(Collections.emptyList());
        when(vocabularyProgressRepository.countByUserIdAndStatus("user-1", VocabularyStatus.LEARNING)).thenReturn(10L);
        when(vocabularyProgressRepository.countByUserIdAndStatus("user-1", VocabularyStatus.MASTERED)).thenReturn(50L);
        when(videoProgressRepository.countByUserId("user-1")).thenReturn(5L);
        when(savedVocabularyRepository.countByUserId("user-1")).thenReturn(20L);
        when(profileRepository.getWeeklyRank("user-1")).thenReturn(3);

        LearningSummaryResponse result = service.getLearningSummary("user-1");

        assertNotNull(result);
        assertEquals(5000, result.getTotalXP());
        assertEquals(10L, result.getLearningCount());
        assertEquals(50L, result.getMasteredCount());
        assertEquals(5L, result.getVideoCount());
        assertEquals(20L, result.getSavedVocabularyCount());
        assertEquals(3, result.getWeeklyRank());
    }

    @Test
    void getLearningSummary_ProfileNotFound_ThrowsException() {
        when(profileRepository.findByUserId("ghost")).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getLearningSummary("ghost"));
    }

    // -----------------------------------------------------------------------
    // calculateCurrentStreak
    // -----------------------------------------------------------------------
    @Test
    void calculateCurrentStreak_ConsecutiveDaysIncludingToday_ReturnsCorrectStreak() {
        LocalDate today = LocalDate.now();
        List<Session> sessions = List.of(
                buildSession(today, 10),
                buildSession(today.minusDays(1), 20),
                buildSession(today.minusDays(2), 30)
        );
        when(sessionRepository.findByUserIdOrderByDateDesc("user-1")).thenReturn(sessions);

        int streak = service.calculateCurrentStreak("user-1");

        assertEquals(3, streak);
    }

    @Test
    void calculateCurrentStreak_StartsFromYesterday_CountsStreak() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Session> sessions = List.of(
                buildSession(yesterday, 10),
                buildSession(yesterday.minusDays(1), 20)
        );
        when(sessionRepository.findByUserIdOrderByDateDesc("user-1")).thenReturn(sessions);

        int streak = service.calculateCurrentStreak("user-1");

        assertEquals(2, streak);
    }

    @Test
    void calculateCurrentStreak_GapInDates_BreaksStreak() {
        LocalDate today = LocalDate.now();
        List<Session> sessions = List.of(
                buildSession(today, 10),
                buildSession(today.minusDays(3), 20) // gap of 2 days
        );
        when(sessionRepository.findByUserIdOrderByDateDesc("user-1")).thenReturn(sessions);

        int streak = service.calculateCurrentStreak("user-1");

        assertEquals(1, streak); // chỉ hôm nay
    }

    @Test
    void calculateCurrentStreak_NoSessions_ReturnsZero() {
        when(sessionRepository.findByUserIdOrderByDateDesc("user-1")).thenReturn(Collections.emptyList());

        int streak = service.calculateCurrentStreak("user-1");

        assertEquals(0, streak);
    }

    @Test
    void calculateCurrentStreak_OnlyZeroXpSessions_ReturnsZero() {
        LocalDate today = LocalDate.now();
        List<Session> sessions = List.of(buildSession(today, 0));
        when(sessionRepository.findByUserIdOrderByDateDesc("user-1")).thenReturn(sessions);

        int streak = service.calculateCurrentStreak("user-1");

        assertEquals(0, streak);
    }

    @Test
    void calculateCurrentStreak_MissedToday_ButStudiedYesterdayAndBefore_Counts() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Session> sessions = List.of(
                buildSession(yesterday, 50),
                buildSession(yesterday.minusDays(1), 40),
                buildSession(yesterday.minusDays(2), 30)
        );
        when(sessionRepository.findByUserIdOrderByDateDesc("user-1")).thenReturn(sessions);

        int streak = service.calculateCurrentStreak("user-1");

        assertEquals(3, streak);
    }

    // -----------------------------------------------------------------------
    // calculateLongestStreak
    // -----------------------------------------------------------------------
    @Test
    void calculateLongestStreak_LongestIsDetected() {
        LocalDate base = LocalDate.of(2026, 1, 10);
        List<Session> sessions = new ArrayList<>();
        // Streak 1: 3 ngày liên tiếp
        sessions.add(buildSession(base, 10));
        sessions.add(buildSession(base.minusDays(1), 10));
        sessions.add(buildSession(base.minusDays(2), 10));
        // Gap
        // Streak 2: 5 ngày liên tiếp (dài hơn)
        sessions.add(buildSession(base.minusDays(5), 10));
        sessions.add(buildSession(base.minusDays(6), 10));
        sessions.add(buildSession(base.minusDays(7), 10));
        sessions.add(buildSession(base.minusDays(8), 10));
        sessions.add(buildSession(base.minusDays(9), 10));

        when(sessionRepository.findByUserIdOrderByDateDesc("user-1")).thenReturn(sessions);

        int longest = service.calculateLongestStreak("user-1");

        assertEquals(5, longest);
    }

    @Test
    void calculateLongestStreak_NoSessions_ReturnsZero() {
        when(sessionRepository.findByUserIdOrderByDateDesc("user-1")).thenReturn(Collections.emptyList());

        int longest = service.calculateLongestStreak("user-1");

        assertEquals(0, longest);
    }

    @Test
    void calculateLongestStreak_SingleDay_Returns1() {
        when(sessionRepository.findByUserIdOrderByDateDesc("user-1"))
                .thenReturn(List.of(buildSession(LocalDate.now(), 100)));

        int longest = service.calculateLongestStreak("user-1");

        assertEquals(1, longest);
    }
}
