package com.tqt.englishApp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.tqt.englishApp.dto.request.MainTopicRequest;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsAdminResponse;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsDetailResponse;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsResponse;
import com.tqt.englishApp.entity.MainTopic;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.enums.LearningGoal;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.MainTopicMapper;
import com.tqt.englishApp.repository.MainTopicRepository;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainTopicServiceTest {
    @InjectMocks
    MainTopicService mainTopicService;

    @Mock
    MainTopicRepository mainTopicRepository;

    @Mock
    MainTopicMapper mainTopicMapper;

    @Mock
    VocabularyService vocabularyService;

    @Mock
    UserLearningProfileRepository userLearningProfileRepository;

    @Mock
    Cloudinary cloudinary;

    @Mock
    Uploader uploader;

    @Mock
    MultipartFile image;

    MainTopic mainTopic;
    MainTopicsAdminResponse mainTopicResponse;
    MainTopicsResponse clientResponse;
    MainTopicsDetailResponse detailResponse;

    @BeforeEach
    void init() {
        mainTopic = new MainTopic();
        mainTopic.setId(1);
        mainTopic.setName("Topic 1");

        mainTopicResponse = new MainTopicsAdminResponse();
        mainTopicResponse.setId(1);
        mainTopicResponse.setName("Topic 1");

        clientResponse = new MainTopicsResponse();
        clientResponse.setId(1);
        clientResponse.setName("Topic 1");

        detailResponse = new MainTopicsDetailResponse();
        detailResponse.setId(1);
        detailResponse.setName("Topic 1");
    }

    @Test
    void createMainTopic_Success() throws IOException {
        MainTopicRequest req = MainTopicRequest.builder()
                .id(1)
                .name("Topic 1")
                .image(image)
                .build();
        when(image.isEmpty()).thenReturn(false);
        when(image.getBytes()).thenReturn(new byte[10]);
        when(mainTopicMapper.toMainTopic(any())).thenReturn(mainTopic);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(Map.of("secure_url", "http://image.url"));
        when(mainTopicRepository.save(any())).thenReturn(mainTopic);
        when(mainTopicMapper.toMainTopicsAdminResponse(any(MainTopic.class))).thenReturn(mainTopicResponse);

        MainTopicsAdminResponse result = mainTopicService.createMainTopic(req);

        assertNotNull(result);
        assertEquals("Topic 1", result.getName());
        verify(mainTopicRepository).save(any());
        verify(uploader).upload(any(), any());
    }

    @Test
    void createMainTopic_Fail_ImageRequired() {
        MainTopicRequest req = MainTopicRequest.builder()
                .id(1)
                .name("Topic 1")
                .build();
        AppException ex = assertThrows(AppException.class, () -> mainTopicService.createMainTopic(req));
        assertEquals(ErrorCode.IMAGE_REQUIRED, ex.getErrorCode());
    }

    @Test
    void createMainTopic_Fail_ImageEmpty() {
        MainTopicRequest req = MainTopicRequest.builder()
                .id(1)
                .name("Topic")
                .image(image)
                .build();
        when(image.isEmpty()).thenReturn(true);
        AppException ex = assertThrows(AppException.class, () -> mainTopicService.createMainTopic(req));
        assertEquals(ErrorCode.IMAGE_REQUIRED, ex.getErrorCode());
    }

    @Test
    void createMainTopic_CloudinaryFailure() throws IOException {
        MainTopicRequest req = MainTopicRequest.builder()
                .id(1)
                .name("Topic 1")
                .image(image)
                .build();
        when(image.isEmpty()).thenReturn(false);
        when(image.getBytes()).thenReturn(new byte[10]);
        when(mainTopicMapper.toMainTopic(any())).thenReturn(mainTopic);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenThrow(new IOException("Cloudinary error"));

        AppException ex = assertThrows(AppException.class, () -> mainTopicService.createMainTopic(req));
        assertEquals(ErrorCode.UNCATEGORIZED_EXCEPTION, ex.getErrorCode());
        verify(mainTopicRepository, never()).save(any());
    }

    @Test
    void updateMainTopic_Success_WithImage() throws IOException {
        MainTopicRequest req = MainTopicRequest.builder()
                .id(1)
                .name("Updated Topic")
                .image(image)
                .build();
        when(mainTopicRepository.findById(1)).thenReturn(Optional.of(mainTopic));
        when(image.isEmpty()).thenReturn(false);
        when(image.getBytes()).thenReturn(new byte[10]);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(Map.of("secure_url", "http://new-image.url"));
        when(mainTopicRepository.save(any())).thenReturn(mainTopic);
        when(mainTopicMapper.toMainTopicsAdminResponse(any(MainTopic.class))).thenReturn(mainTopicResponse);

        MainTopicsAdminResponse result = mainTopicService.updateMainTopic(1, req);

        assertNotNull(result);
        verify(mainTopicMapper).updateMainTopic(eq(mainTopic), any());
        verify(uploader).upload(any(), any());
        verify(mainTopicRepository).save(mainTopic);
    }

    @Test
    void updateMainTopic_Success_NoImage() {
        MainTopicRequest req = MainTopicRequest.builder()
                .id(1)
                .name("Updated Topic")
                .build();
        when(mainTopicRepository.findById(1)).thenReturn(Optional.of(mainTopic));
        when(mainTopicRepository.save(any())).thenReturn(mainTopic);
        when(mainTopicMapper.toMainTopicsAdminResponse(any(MainTopic.class))).thenReturn(mainTopicResponse);

        MainTopicsAdminResponse result = mainTopicService.updateMainTopic(1, req);

        assertNotNull(result);
        verify(mainTopicMapper).updateMainTopic(eq(mainTopic), any());
        verifyNoInteractions(cloudinary);
        verify(mainTopicRepository).save(mainTopic);
    }

    @Test
    void updateMainTopic_Success_EmptyImage() {
        MainTopicRequest req = MainTopicRequest.builder()
                .id(1)
                .name("Updated Topic")
                .image(image)
                .build();
        when(image.isEmpty()).thenReturn(true);
        when(mainTopicRepository.findById(1)).thenReturn(Optional.of(mainTopic));
        when(mainTopicRepository.save(any())).thenReturn(mainTopic);
        when(mainTopicMapper.toMainTopicsAdminResponse(any(MainTopic.class))).thenReturn(mainTopicResponse);

        MainTopicsAdminResponse result = mainTopicService.updateMainTopic(1, req);

        assertNotNull(result);
        verify(mainTopicMapper).updateMainTopic(eq(mainTopic), any());
        verifyNoInteractions(cloudinary);
        verify(mainTopicRepository).save(mainTopic);
    }

    @Test
    void updateMainTopic_Fail_NotFound() {
        MainTopicRequest req = MainTopicRequest.builder()
                .id(1)
                .name("F")
                .image(image)
                .build();
        when(mainTopicRepository.findById(99)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> mainTopicService.updateMainTopic(99, req));
        assertEquals(ErrorCode.TOPIC_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void updateMainTopic_CloudinaryFailure() throws IOException {
        MainTopicRequest req = MainTopicRequest.builder()
                .id(1)
                .name("Updated Topic")
                .image(image)
                .build();
        when(mainTopicRepository.findById(1)).thenReturn(Optional.of(mainTopic));
        when(image.isEmpty()).thenReturn(false);
        when(image.getBytes()).thenReturn(new byte[10]);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenThrow(new IOException("Upload failed"));

        AppException ex = assertThrows(AppException.class, () -> mainTopicService.updateMainTopic(1, req));
        assertEquals(ErrorCode.UNCATEGORIZED_EXCEPTION, ex.getErrorCode());
        verify(mainTopicRepository, never()).save(any());
    }

    @Test
    void getMainTopics_WithKeyword() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Topic");
        params.put("page", "1");
        params.put("size", "5");

        Page<MainTopic> page = new PageImpl<>(List.of(mainTopic));

        org.mockito.ArgumentCaptor<Pageable> pageableCaptor = org.mockito.ArgumentCaptor.forClass(Pageable.class);

        when(mainTopicRepository.findByNameContainingIgnoreCase(eq("Topic"), pageableCaptor.capture()))
                .thenReturn(page);
        when(mainTopicMapper.toMainTopicsAdminResponse(any(MainTopic.class))).thenReturn(mainTopicResponse);

        Page<MainTopicsAdminResponse> result = mainTopicService.getMainTopicsForAdmin(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(5, capturedPageable.getPageSize());
    }

    @Test
    void getMainTopics_WithoutKeyword_DefaultPagination() {
        Map<String, String> params = new HashMap<>();
        Page<MainTopic> page = new PageImpl<>(List.of(mainTopic));

        org.mockito.ArgumentCaptor<Pageable> pageableCaptor = org.mockito.ArgumentCaptor.forClass(Pageable.class);

        when(mainTopicRepository.findAll(pageableCaptor.capture())).thenReturn(page);
        when(mainTopicMapper.toMainTopicsAdminResponse(any(MainTopic.class))).thenReturn(mainTopicResponse);

        Page<MainTopicsAdminResponse> result = mainTopicService.getMainTopicsForAdmin(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(8, capturedPageable.getPageSize());
    }

    @Test
    void getMainTopics_WithEmptyName_ReturnsAll() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "   ");
        Page<MainTopic> page = new PageImpl<>(List.of(mainTopic));

        when(mainTopicRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mainTopicMapper.toMainTopicsAdminResponse(any(MainTopic.class))).thenReturn(mainTopicResponse);

        Page<MainTopicsAdminResponse> result = mainTopicService.getMainTopicsForAdmin(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(mainTopicRepository).findAll(any(Pageable.class));
    }

    @Test
    void getMainTopics_NegativePage_ReturnsFirstPage() {
        Map<String, String> params = new HashMap<>();
        params.put("page", "-5");

        Page<MainTopic> page = new PageImpl<>(List.of(mainTopic));
        org.mockito.ArgumentCaptor<Pageable> pageableCaptor = org.mockito.ArgumentCaptor.forClass(Pageable.class);

        when(mainTopicRepository.findAll(pageableCaptor.capture())).thenReturn(page);
        when(mainTopicMapper.toMainTopicsAdminResponse(any(MainTopic.class))).thenReturn(mainTopicResponse);

        mainTopicService.getMainTopicsForAdmin(params);

        assertEquals(0, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void getMainTopicById_Success() {
        when(mainTopicRepository.findById(1)).thenReturn(Optional.of(mainTopic));
        when(mainTopicMapper.toMainTopicsAdminResponse(mainTopic)).thenReturn(mainTopicResponse);

        MainTopicsAdminResponse result = mainTopicService.getMainTopicByIdForAdmin(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getMainTopicById_Fail_NotFound() {
        when(mainTopicRepository.findById(99)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> mainTopicService.getMainTopicByIdForAdmin(99));
        assertEquals(ErrorCode.TOPIC_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void countMainTopic_Success() {
        when(mainTopicRepository.count()).thenReturn(10L);
        Long count = mainTopicService.countMainTopic();
        assertEquals(10L, count);
    }

    @Test
    void findAll_Success() {
        when(mainTopicRepository.findAll()).thenReturn(List.of(mainTopic));
        when(mainTopicMapper.toMainTopicsAdminResponse(anyList())).thenReturn(List.of(mainTopicResponse));

        List<MainTopicsAdminResponse> result = mainTopicService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void deleteMainTopic_Success() {
        mainTopicService.deleteMainTopic(1);
        verify(mainTopicRepository).deleteVocabularySubTopicRelationsByMainTopic(1);
        verify(mainTopicRepository).deleteById(1);
        verify(vocabularyService).deleteAllVocabulary();
    }

    @Test
    void deleteMainTopic_PropagatesException() {
        doThrow(new RuntimeException("DB Error")).when(mainTopicRepository).deleteById(1);

        assertThrows(RuntimeException.class, () -> mainTopicService.deleteMainTopic(1));

        verify(mainTopicRepository).deleteVocabularySubTopicRelationsByMainTopic(1);
        verify(mainTopicRepository).deleteById(1);
    }

    @Test
    void getMainTopicsForClient_Success() {
        Map<String, String> params = new HashMap<>();
        Page<MainTopic> page = new PageImpl<>(List.of(mainTopic));
        when(mainTopicRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mainTopicMapper.toMainTopicsResponse(any(MainTopic.class))).thenReturn(clientResponse);

        Page<MainTopicsResponse> result = mainTopicService.getMainTopicsForClient(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getMainTopicDetailForClient_Success() {
        when(mainTopicRepository.findById(1)).thenReturn(Optional.of(mainTopic));
        when(mainTopicMapper.toMainTopicsDetailResponse(mainTopic)).thenReturn(detailResponse);

        MainTopicsDetailResponse result = mainTopicService.getMainTopicDetailForClient(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getLearningPathForClient_Success() {
        UserLearningProfile profile = new UserLearningProfile();
        profile.setGoal(LearningGoal.DAILY_COMMUNICATION);
        when(userLearningProfileRepository.findByUserId("user1")).thenReturn(Optional.of(profile));
        when(mainTopicRepository.findByGoalOrderByTopicOrderAsc(LearningGoal.DAILY_COMMUNICATION))
                .thenReturn(List.of(mainTopic));
        when(mainTopicMapper.toMainTopicsResponse(anyList())).thenReturn(List.of(clientResponse));

        List<MainTopicsResponse> result = mainTopicService.getLearningPathForClient("user1");

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
