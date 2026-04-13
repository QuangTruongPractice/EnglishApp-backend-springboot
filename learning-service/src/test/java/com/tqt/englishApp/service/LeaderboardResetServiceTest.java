package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardResetServiceTest {

    @InjectMocks
    private LeaderboardResetService service;

    @Mock
    private UserLearningProfileRepository profileRepository;

    @Test
    void resetWeeklyXp_ResetsAllProfilesWeeklyXpToZero() {
        UserLearningProfile p1 = new UserLearningProfile();
        p1.setUserId("user-1");
        p1.setWeeklyXp(500);

        UserLearningProfile p2 = new UserLearningProfile();
        p2.setUserId("user-2");
        p2.setWeeklyXp(1200);

        when(profileRepository.findAll()).thenReturn(List.of(p1, p2));
        when(profileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.resetWeeklyXp();

        // Cả hai profile đều bị reset về 0
        verify(profileRepository, times(2)).save(any(UserLearningProfile.class));
        org.junit.jupiter.api.Assertions.assertEquals(0, p1.getWeeklyXp());
        org.junit.jupiter.api.Assertions.assertEquals(0, p2.getWeeklyXp());
    }

    @Test
    void resetWeeklyXp_NoProfiles_DoesNotThrow() {
        when(profileRepository.findAll()).thenReturn(List.of());

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.resetWeeklyXp());

        verify(profileRepository, never()).save(any());
    }

    @Test
    void resetWeeklyXp_SingleProfile_SavedOnce() {
        UserLearningProfile p = new UserLearningProfile();
        p.setUserId("user-solo");
        p.setWeeklyXp(300);

        when(profileRepository.findAll()).thenReturn(List.of(p));
        when(profileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.resetWeeklyXp();

        verify(profileRepository, times(1)).save(p);
        org.junit.jupiter.api.Assertions.assertEquals(0, p.getWeeklyXp());
    }
}
