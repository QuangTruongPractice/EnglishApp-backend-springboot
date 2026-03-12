package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.SubTopicRequest;
import com.tqt.englishApp.dto.response.subTopic.SubTopicsAdminResponse;
import com.tqt.englishApp.entity.MainTopic;
import com.tqt.englishApp.entity.SubTopic;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.SubTopicMapper;
import com.tqt.englishApp.repository.MainTopicRepository;
import com.tqt.englishApp.repository.SubTopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubTopicServiceTest {
    @InjectMocks
    SubTopicService subTopicService;

    @Mock
    SubTopicMapper subTopicMapper;

    @Mock
    SubTopicRepository subTopicRepository;

    @Mock
    MainTopicRepository mainTopicRepository;

    @Mock
    VocabularyService vocabularyService;

    SubTopic subTopic;
    SubTopicsAdminResponse subTopicResponse;
    MainTopic mainTopic;

    @BeforeEach
    void init() {
        mainTopic = new MainTopic();
        mainTopic.setId(1);
        mainTopic.setName("Main Topic");

        subTopic = new SubTopic();
        subTopic.setId(1);
        subTopic.setName("Sub Topic");
        subTopic.setMainTopic(mainTopic);

        subTopicResponse = new SubTopicsAdminResponse();
        subTopicResponse.setId(1);
        subTopicResponse.setName("Sub Topic");
    }

    @Test
    void createSubTopic_Success() {
        SubTopicRequest req = new SubTopicRequest(1, "Sub Topic", 1, null);
        when(subTopicMapper.toSubTopic(any())).thenReturn(subTopic);
        when(mainTopicRepository.findById(1)).thenReturn(Optional.of(mainTopic));
        when(subTopicRepository.save(any())).thenReturn(subTopic);
        when(subTopicMapper.toSubTopicsAdminResponse(any(SubTopic.class))).thenReturn(subTopicResponse);

        SubTopicsAdminResponse result = subTopicService.createSubTopic(req);

        assertNotNull(result);
        assertEquals("Sub Topic", result.getName());
        verify(subTopicRepository).save(any());
    }

    @Test
    void createSubTopic_Fail_MainTopicNotFound() {
        SubTopicRequest req = new SubTopicRequest(1, "Sub Topic", 99, null);
        when(subTopicMapper.toSubTopic(any())).thenReturn(subTopic);
        when(mainTopicRepository.findById(99)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> subTopicService.createSubTopic(req));
        assertEquals(ErrorCode.TOPIC_NOT_EXISTED, ex.getErrorCode());
        verify(subTopicRepository, never()).save(any());
    }

    @Test
    void updateSubTopic_Success() {
        SubTopicRequest req = new SubTopicRequest(1, "Updated Sub", 1, null);
        when(subTopicRepository.findById(1)).thenReturn(Optional.of(subTopic));
        when(mainTopicRepository.findById(1)).thenReturn(Optional.of(mainTopic));
        when(subTopicRepository.save(any())).thenReturn(subTopic);
        when(subTopicMapper.toSubTopicsAdminResponse(any(SubTopic.class))).thenReturn(subTopicResponse);

        SubTopicsAdminResponse result = subTopicService.updateSubTopic(1, req);

        assertNotNull(result);
        verify(subTopicMapper).updateSubTopic(eq(subTopic), any());
        verify(subTopicRepository).save(subTopic);
    }

    @Test
    void updateSubTopic_Fail_SubTopicNotFound() {
        SubTopicRequest req = new SubTopicRequest(1, "F", 1, null);
        when(subTopicRepository.findById(99)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> subTopicService.updateSubTopic(99, req));
        assertEquals(ErrorCode.TOPIC_NOT_EXISTED, ex.getErrorCode());
        verify(subTopicRepository, never()).save(any());
    }

    @Test
    void updateSubTopic_Fail_MainTopicNotFound() {
        SubTopicRequest req = new SubTopicRequest(1, "F", 99, null);
        when(subTopicRepository.findById(1)).thenReturn(Optional.of(subTopic));
        when(mainTopicRepository.findById(99)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> subTopicService.updateSubTopic(1, req));
        assertEquals(ErrorCode.TOPIC_NOT_EXISTED, ex.getErrorCode());
        verify(subTopicRepository, never()).save(any());
    }

    @Test
    void getSubTopics_WithKeyword() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Sub");
        params.put("page", "1");
        params.put("size", "10");

        Page<SubTopic> page = new PageImpl<>(List.of(subTopic));

        org.mockito.ArgumentCaptor<Pageable> pageableCaptor = org.mockito.ArgumentCaptor.forClass(Pageable.class);

        when(subTopicRepository.findByNameContainingIgnoreCase(eq("Sub"), pageableCaptor.capture())).thenReturn(page);
        when(subTopicMapper.toSubTopicsAdminResponse(any(SubTopic.class))).thenReturn(subTopicResponse);

        Page<SubTopicsAdminResponse> result = subTopicService.getSubTopics(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(10, capturedPageable.getPageSize());
    }

    @Test
    void getSubTopics_DefaultPagination() {
        Map<String, String> params = new HashMap<>();
        Page<SubTopic> page = new PageImpl<>(List.of(subTopic));

        org.mockito.ArgumentCaptor<Pageable> pageableCaptor = org.mockito.ArgumentCaptor.forClass(Pageable.class);

        when(subTopicRepository.findAll(pageableCaptor.capture())).thenReturn(page);
        when(subTopicMapper.toSubTopicsAdminResponse(any(SubTopic.class))).thenReturn(subTopicResponse);

        Page<SubTopicsAdminResponse> result = subTopicService.getSubTopics(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(10, capturedPageable.getPageSize());
    }

    @Test
    void getSubTopics_WithBlankName_ReturnsAll() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "   ");
        Page<SubTopic> page = new PageImpl<>(List.of(subTopic));

        when(subTopicRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(subTopicMapper.toSubTopicsAdminResponse(any(SubTopic.class))).thenReturn(subTopicResponse);

        Page<SubTopicsAdminResponse> result = subTopicService.getSubTopics(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(subTopicRepository).findAll(any(Pageable.class));
    }

    @Test
    void getSubTopics_WithNegativePage_ReturnsFirstPage() {
        Map<String, String> params = new HashMap<>();
        params.put("page", "-5");
        Page<SubTopic> page = new PageImpl<>(List.of(subTopic));
        org.mockito.ArgumentCaptor<Pageable> pageableCaptor = org.mockito.ArgumentCaptor.forClass(Pageable.class);

        when(subTopicRepository.findAll(pageableCaptor.capture())).thenReturn(page);
        when(subTopicMapper.toSubTopicsAdminResponse(any(SubTopic.class))).thenReturn(subTopicResponse);

        subTopicService.getSubTopics(params);

        assertEquals(0, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void getSubTopicDetailForAdmin_Success() {
        when(subTopicRepository.findById(1)).thenReturn(Optional.of(subTopic));
        when(subTopicMapper.toSubTopicsAdminResponse(subTopic)).thenReturn(subTopicResponse);

        SubTopicsAdminResponse result = subTopicService.getSubTopicDetailForAdmin(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getSubTopicDetailForAdmin_Fail_NotFound() {
        when(subTopicRepository.findById(99)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> subTopicService.getSubTopicDetailForAdmin(99));
        assertEquals(ErrorCode.TOPIC_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void findAll_Success() {
        when(subTopicRepository.findAll()).thenReturn(List.of(subTopic));
        when(subTopicMapper.toSubTopicsAdminResponse(anyList())).thenReturn(List.of(subTopicResponse));

        List<SubTopicsAdminResponse> result = subTopicService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void countSubTopic_Success() {
        when(subTopicRepository.count()).thenReturn(15L);
        Long result = subTopicService.countSubTopic();
        assertEquals(15L, result);
        verify(subTopicRepository).count();
    }

    @Test
    void deleteSubTopic_Success() {
        subTopicService.deleteSubTopic(1);
        verify(subTopicRepository).deleteById(1);
        verify(vocabularyService).deleteAllVocabulary();
    }

    @Test
    void deleteSubTopic_PropagatesException() {
        doThrow(new RuntimeException("Database error")).when(subTopicRepository).deleteById(1);

        assertThrows(RuntimeException.class, () -> subTopicService.deleteSubTopic(1));

        verify(subTopicRepository).deleteById(1);
    }
}
