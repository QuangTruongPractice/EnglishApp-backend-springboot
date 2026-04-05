package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.LearningSummaryResponse;
import com.tqt.englishApp.dto.response.StreakCalendarResponse;
import com.tqt.englishApp.entity.Session;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.enums.VocabularyStatus;
import com.tqt.englishApp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStatsService {
    private final SessionRepository sessionRepository;
    private final UserLearningProfileRepository profileRepository;
    private final UserVocabularyProgressRepository vocabularyProgressRepository;
    private final UserVideoProgressRepository videoProgressRepository;
    private final UserSavedVocabularyRepository savedVocabularyRepository;

    public StreakCalendarResponse getStreakCalendar(String userId, int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<Session> sessions = sessionRepository.findByUserIdAndDateBetween(userId, start, end);
        List<LocalDate> studiedDates = sessions.stream()
                .filter(s -> s.getTotalXP() != null && s.getTotalXP() > 0)
                .map(Session::getDate)
                .distinct()
                .collect(Collectors.toList());

        int currentStreak = calculateCurrentStreak(userId);
        int longestStreak = calculateLongestStreak(userId);

        return StreakCalendarResponse.builder()
                .studiedDates(studiedDates)
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .build();
    }

    public LearningSummaryResponse getLearningSummary(String userId) {
        UserLearningProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        int streak = calculateCurrentStreak(userId);
        long learningCount = vocabularyProgressRepository.countByUserIdAndStatus(userId, VocabularyStatus.LEARNING);
        long masteredCount = vocabularyProgressRepository.countByUserIdAndStatus(userId, VocabularyStatus.MASTERED);
        long videoCount = videoProgressRepository.countByUserId(userId);
        long savedCount = savedVocabularyRepository.countByUserId(userId);
        int rank = profileRepository.getWeeklyRank(userId);

        return LearningSummaryResponse.builder()
                .streak(streak)
                .totalXP(profile.getTotalXp())
                .learningCount(learningCount)
                .masteredCount(masteredCount)
                .videoCount(videoCount)
                .weeklyRank(rank)
                .savedVocabularyCount(savedCount)
                .build();
    }

    public int calculateCurrentStreak(String userId) {
        List<Session> sessions = sessionRepository.findByUserIdOrderByDateDesc(userId);
        if (sessions.isEmpty()) return 0;

        List<LocalDate> uniqueDates = sessions.stream()
                .filter(s -> s.getTotalXP() != null && s.getTotalXP() > 0)
                .map(Session::getDate)
                .distinct()
                .collect(Collectors.toList());

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        if (!uniqueDates.contains(today) && !uniqueDates.contains(yesterday)) {
            return 0;
        }

        int streak = 0;
        LocalDate currentCheck = uniqueDates.contains(today) ? today : yesterday;

        for (LocalDate date : uniqueDates) {
            if (date.equals(currentCheck)) {
                streak++;
                currentCheck = currentCheck.minusDays(1);
            } else if (date.isBefore(currentCheck)) {
                break;
            }
        }

        return streak;
    }

    public int calculateLongestStreak(String userId) {
        List<Session> sessions = sessionRepository.findByUserIdOrderByDateDesc(userId);
        if (sessions.isEmpty()) return 0;

        List<LocalDate> sortedDates = sessions.stream()
                .filter(s -> s.getTotalXP() != null && s.getTotalXP() > 0)
                .map(Session::getDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        int maxStreak = 0;
        int currentStreak = 0;
        LocalDate lastDate = null;

        for (LocalDate date : sortedDates) {
            if (lastDate == null || date.equals(lastDate.plusDays(1))) {
                currentStreak++;
            } else {
                maxStreak = Math.max(maxStreak, currentStreak);
                currentStreak = 1;
            }
            lastDate = date;
        }

        return Math.max(maxStreak, currentStreak);
    }
}
