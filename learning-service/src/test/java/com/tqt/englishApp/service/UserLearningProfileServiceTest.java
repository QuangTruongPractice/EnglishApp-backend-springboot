package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.UserProfileRequest;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.enums.Level;
import com.tqt.englishApp.enums.LearningGoal;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLearningProfileServiceTest {

    @InjectMocks
    private UserLearningProfileService service;

    @Mock
    private UserLearningProfileRepository userLearningProfileRepository;

    private UserLearningProfile profile;
    private UserProfileRequest request;

    @BeforeEach
    void setUp() {
        profile = new UserLearningProfile();
        profile.setUserId("user-1");
        profile.setLevel(Level.A1);
        profile.setDailyTarget(15);
        profile.setGoal(LearningGoal.TRAVEL);
        profile.setOnboardingCompleted(true);

        request = new UserProfileRequest();
        request.setLevel(Level.B1);
        request.setDailyTarget(30);
        request.setGoal(LearningGoal.DAILY_COMMUNICATION);
    }

    // -----------------------------------------------------------------------
    // getProfile
    // -----------------------------------------------------------------------
    @Test
    void getProfile_Found_ReturnsProfile() {
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));

        UserLearningProfile result = service.getProfile("user-1");

        assertNotNull(result);
        assertEquals("user-1", result.getUserId());
    }

    @Test
    void getProfile_NotFound_ReturnsNull() {
        when(userLearningProfileRepository.findByUserId("missing")).thenReturn(Optional.empty());

        UserLearningProfile result = service.getProfile("missing");

        assertNull(result);
    }

    // -----------------------------------------------------------------------
    // createOrUpdateProfile
    // -----------------------------------------------------------------------
    @Test
    void createOrUpdateProfile_NewUser_CreatesProfile() {
        when(userLearningProfileRepository.findByUserId("new-user")).thenReturn(Optional.empty());
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserLearningProfile result = service.createOrUpdateProfile("new-user", request);

        assertNotNull(result);
        assertEquals("new-user", result.getUserId());
        assertEquals(Level.B1, result.getLevel());
        assertEquals(30, result.getDailyTarget());
        assertEquals(LearningGoal.DAILY_COMMUNICATION, result.getGoal());
        assertTrue(result.getOnboardingCompleted());
    }

    @Test
    void createOrUpdateProfile_ExistingUser_UpdatesFields() {
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserLearningProfile result = service.createOrUpdateProfile("user-1", request);

        assertEquals(Level.B1, result.getLevel());
        assertEquals(30, result.getDailyTarget());
        verify(userLearningProfileRepository).save(profile);
    }

    @Test
    void createOrUpdateProfile_MissingGoal_OnboardingNotCompleted() {
        request.setGoal(null);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.empty());
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserLearningProfile result = service.createOrUpdateProfile("user-1", request);

        assertFalse(result.getOnboardingCompleted());
    }

    @Test
    void createOrUpdateProfile_AllFieldsPresent_OnboardingCompleted() {
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.empty());
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserLearningProfile result = service.createOrUpdateProfile("user-1", request);

        assertTrue(result.getOnboardingCompleted());
    }

    // -----------------------------------------------------------------------
    // updateProfile
    // -----------------------------------------------------------------------
    @Test
    void updateProfile_Success_UpdatesFieldsAndDate() {
        profile.setProfileUpdatedAt(LocalDate.now().minusDays(8)); // đủ 7 ngày
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserLearningProfile result = service.updateProfile("user-1", request);

        assertEquals(Level.B1, result.getLevel());
        assertEquals(LocalDate.now(), result.getProfileUpdatedAt());
    }

    @Test
    void updateProfile_UserNotFound_ThrowsAppException() {
        when(userLearningProfileRepository.findByUserId("ghost")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> service.updateProfile("ghost", request));

        assertEquals(ErrorCode.USER_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void updateProfile_UpdatedWithin7Days_ThrowsProfileUpdateLimited() {
        profile.setProfileUpdatedAt(LocalDate.now().minusDays(3)); // chưa đủ 7 ngày
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));

        AppException ex = assertThrows(AppException.class, () -> service.updateProfile("user-1", request));

        assertEquals(ErrorCode.PROFILE_UPDATE_LIMITED, ex.getErrorCode());
    }

    @Test
    void updateProfile_NullLevel_KeepsExistingLevel() {
        profile.setProfileUpdatedAt(LocalDate.now().minusDays(8));
        request.setLevel(null);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserLearningProfile result = service.updateProfile("user-1", request);

        assertEquals(Level.A1, result.getLevel()); // unchanged
    }

    @Test
    void updateProfile_NullDailyTarget_KeepsExistingTarget() {
        profile.setProfileUpdatedAt(LocalDate.now().minusDays(8));
        request.setDailyTarget(null);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserLearningProfile result = service.updateProfile("user-1", request);

        assertEquals(15, result.getDailyTarget()); // unchanged
    }

    @Test
    void updateProfile_OnboardingCompleted_IfAllFieldsPresent() {
        profile.setProfileUpdatedAt(LocalDate.now().minusDays(8));
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserLearningProfile result = service.updateProfile("user-1", request);

        assertTrue(result.getOnboardingCompleted());
    }
}
