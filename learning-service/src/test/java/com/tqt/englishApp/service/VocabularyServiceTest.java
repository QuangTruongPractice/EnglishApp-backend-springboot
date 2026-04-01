package com.tqt.englishApp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.tqt.englishApp.dto.request.VocabularyRequest;
import com.tqt.englishApp.dto.request.VocabularyMeaningRequest;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesResponse;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesSimpleResponse;
import com.tqt.englishApp.entity.SubTopic;
import com.tqt.englishApp.entity.Vocabulary;
import com.tqt.englishApp.enums.Type;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.VocabularyMapper;
import com.tqt.englishApp.repository.SubTopicRepository;
import com.tqt.englishApp.repository.VocabularyRepository;
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
class VocabularyServiceTest {
    @InjectMocks
    VocabularyService vocabularyService;

    @Mock
    SubTopicRepository subTopicRepository;

    @Mock
    VocabularyRepository vocabularyRepository;

    @Mock
    VocabularyMapper vocabularyMapper;

    @Mock
    Cloudinary cloudinary;

    @Mock
    Uploader uploader;

    @Mock
    MultipartFile audioFile;

    Vocabulary vocabulary;
    VocabulariesResponse vocabularyResponse;
    VocabulariesSimpleResponse vocabulariesSimpleResponse;
    SubTopic subTopic;

    @BeforeEach
    void init() {
        subTopic = new SubTopic();
        subTopic.setId(1);

        vocabulary = new Vocabulary();
        vocabulary.setId(1);
        vocabulary.setWord("Hello");

        vocabularyResponse = new VocabulariesResponse();
        vocabularyResponse.setId(1);
        vocabularyResponse.setWord("Hello");

        vocabulariesSimpleResponse = new VocabulariesSimpleResponse();
        vocabulariesSimpleResponse.setId(1);
        vocabulariesSimpleResponse.setWord("Hello");
    }

    @Test
    void createVocabulary_Success_WithFile() throws IOException {
        VocabularyRequest req = VocabularyRequest.builder()
                .id(1).phonetic("ph").word("Hello").audioFile(audioFile).subTopics(List.of(1))
                .meanings(List.of(VocabularyMeaningRequest.builder()
                        .type(Type.NOUN).definition("def").vnWord("vnW").vnDefinition("vnD")
                        .example("ex").vnExample("vnE").build()))
                .build();
        when(vocabularyMapper.toVocabulary(any())).thenReturn(vocabulary);
        when(subTopicRepository.findAllById(anyList())).thenReturn(List.of(subTopic));
        when(audioFile.isEmpty()).thenReturn(false);
        when(audioFile.getBytes()).thenReturn(new byte[10]);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(Map.of("secure_url", "http://audio.url"));
        when(vocabularyRepository.save(any())).thenReturn(vocabulary);
        when(vocabularyMapper.toVocabulariesResponse(any(Vocabulary.class))).thenReturn(vocabularyResponse);

        VocabulariesResponse result = vocabularyService.createVocabulary(req);

        assertNotNull(result);
        assertEquals("Hello", result.getWord());
        verify(vocabularyRepository).save(any());
        verify(uploader).upload(any(), any());
    }

    @Test
    void createVocabulary_Success_WithUrl() {
        VocabularyRequest req = VocabularyRequest.builder()
                .id(1).phonetic("ph").word("Hello").audioUrl("http://fixed.url").subTopics(List.of(1))
                .meanings(List.of(VocabularyMeaningRequest.builder()
                        .type(Type.NOUN).definition("def").vnWord("vnW").vnDefinition("vnD")
                        .example("ex").vnExample("vnE").build()))
                .build();
        when(vocabularyMapper.toVocabulary(any())).thenReturn(vocabulary);
        when(subTopicRepository.findAllById(anyList())).thenReturn(List.of(subTopic));
        when(vocabularyRepository.save(any())).thenReturn(vocabulary);
        when(vocabularyMapper.toVocabulariesResponse(any(Vocabulary.class))).thenReturn(vocabularyResponse);

        VocabulariesResponse result = vocabularyService.createVocabulary(req);

        assertNotNull(result);
        assertEquals("Hello", result.getWord());
    }

    @Test
    void createVocabulary_Fail_TopicNotFound() {
        VocabularyRequest req = VocabularyRequest.builder()
                .id(1).phonetic("ph").word("Hello").subTopics(List.of(99))
                .meanings(List.of(VocabularyMeaningRequest.builder()
                        .type(Type.NOUN).definition("def").vnWord("vnW").vnDefinition("vnD")
                        .example("ex").vnExample("vnE").build()))
                .build();
        when(vocabularyMapper.toVocabulary(any())).thenReturn(vocabulary);
        when(subTopicRepository.findAllById(anyList())).thenReturn(List.of());

        AppException ex = assertThrows(AppException.class, () -> vocabularyService.createVocabulary(req));
        assertEquals(ErrorCode.TOPIC_NOT_EXISTED, ex.getErrorCode());
        verify(vocabularyRepository, never()).save(any());
    }

    @Test
    void createVocabulary_CloudinaryFailure() throws IOException {
        VocabularyRequest req = VocabularyRequest.builder()
                .id(1).phonetic("ph").word("Hello").audioFile(audioFile).subTopics(List.of(1))
                .meanings(List.of(VocabularyMeaningRequest.builder()
                        .type(Type.NOUN).definition("def").vnWord("vnW").vnDefinition("vnD")
                        .example("ex").vnExample("vnE").build()))
                .build();
        when(vocabularyMapper.toVocabulary(any())).thenReturn(vocabulary);
        when(subTopicRepository.findAllById(anyList())).thenReturn(List.of(subTopic));
        when(audioFile.isEmpty()).thenReturn(false);
        when(audioFile.getBytes()).thenReturn(new byte[10]);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenThrow(new IOException("Upload failed"));

        AppException ex = assertThrows(AppException.class, () -> vocabularyService.createVocabulary(req));
        assertEquals(ErrorCode.UNCATEGORIZED_EXCEPTION, ex.getErrorCode());
        verify(vocabularyRepository, never()).save(any());
    }

    @Test
    void updateVocabulary_Success() throws IOException {
        VocabularyRequest req = VocabularyRequest.builder()
                .id(1).phonetic("ph").word("Updated").audioFile(audioFile).subTopics(List.of(1))
                .meanings(List.of(VocabularyMeaningRequest.builder()
                        .type(Type.VERB).definition("def").vnWord("vnW").vnDefinition("vnD")
                        .example("ex").vnExample("vnE").build()))
                .build();
        when(vocabularyRepository.findById(1)).thenReturn(Optional.of(vocabulary));
        when(subTopicRepository.findAllById(anyList())).thenReturn(List.of(subTopic));
        when(audioFile.isEmpty()).thenReturn(false);
        when(audioFile.getBytes()).thenReturn(new byte[10]);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(Map.of("secure_url", "http://new-audio.url"));
        when(vocabularyRepository.save(any())).thenReturn(vocabulary);
        when(vocabularyMapper.toVocabulariesResponse(any(Vocabulary.class))).thenReturn(vocabularyResponse);

        VocabulariesResponse result = vocabularyService.updateVocabulary(1, req);

        assertNotNull(result);
        verify(vocabularyMapper).updateVocabulary(eq(vocabulary), any());
        verify(uploader).upload(any(), any());
        verify(vocabularyRepository).save(vocabulary);
    }

    @Test
    void updateVocabulary_Success_NoFile() {
        VocabularyRequest req = VocabularyRequest.builder()
                .id(1).phonetic("ph").word("Updated").subTopics(List.of(1))
                .meanings(List.of(VocabularyMeaningRequest.builder()
                        .type(Type.NOUN).definition("def").vnWord("vnW").vnDefinition("vnD")
                        .example("ex").vnExample("vnE").build()))
                .build();
        when(vocabularyRepository.findById(1)).thenReturn(Optional.of(vocabulary));
        when(subTopicRepository.findAllById(anyList())).thenReturn(List.of(subTopic));

        when(vocabularyRepository.save(any())).thenReturn(vocabulary);
        when(vocabularyMapper.toVocabulariesResponse(any(Vocabulary.class))).thenReturn(vocabularyResponse);

        VocabulariesResponse result = vocabularyService.updateVocabulary(1, req);

        assertNotNull(result);
        verify(vocabularyMapper).updateVocabulary(eq(vocabulary), any());
        verifyNoInteractions(cloudinary);
        verify(vocabularyRepository).save(vocabulary);
    }

    @Test
    void updateVocabulary_Fail_NotFound() {
        VocabularyRequest req = new VocabularyRequest();
        when(vocabularyRepository.findById(99)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> vocabularyService.updateVocabulary(99, req));
        assertEquals(ErrorCode.VOCABULARY_NOT_EXISTED, ex.getErrorCode());
        verify(vocabularyRepository, never()).save(any());
    }

    @Test
    void getVocabularies_WithKeyword() {
        Map<String, String> params = new HashMap<>();
        params.put("word", "Hello");
        params.put("page", "1");
        params.put("size", "5");

        Page<Vocabulary> page = new PageImpl<>(List.of(vocabulary));

        org.mockito.ArgumentCaptor<Pageable> pageableCaptor = org.mockito.ArgumentCaptor.forClass(Pageable.class);

        when(vocabularyRepository.findByWordContainingIgnoreCase(eq("Hello"), pageableCaptor.capture()))
                .thenReturn(page);
        when(vocabularyMapper.toVocabulariesResponse(any(Vocabulary.class))).thenReturn(vocabularyResponse);

        Page<VocabulariesResponse> result = vocabularyService.getVocabularies(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(5, capturedPageable.getPageSize());
    }

    @Test
    void getVocabularies_WithBlankWord_ReturnsAll() {
        Map<String, String> params = new HashMap<>();
        params.put("word", "   ");
        Page<Vocabulary> page = new PageImpl<>(List.of(vocabulary));

        when(vocabularyRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(vocabularyMapper.toVocabulariesResponse(any(Vocabulary.class))).thenReturn(vocabularyResponse);

        Page<VocabulariesResponse> result = vocabularyService.getVocabularies(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(vocabularyRepository).findAll(any(Pageable.class));
    }

    @Test
    void getVocabularies_NegativePage_ReturnsFirstPage() {
        Map<String, String> params = new HashMap<>();
        params.put("page", "-2");
        Page<Vocabulary> page = new PageImpl<>(List.of(vocabulary));
        org.mockito.ArgumentCaptor<Pageable> pageableCaptor = org.mockito.ArgumentCaptor.forClass(Pageable.class);

        when(vocabularyRepository.findAll(pageableCaptor.capture())).thenReturn(page);
        when(vocabularyMapper.toVocabulariesResponse(any(Vocabulary.class))).thenReturn(vocabularyResponse);

        vocabularyService.getVocabularies(params);

        assertEquals(0, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void getVocabularyById_Success() {
        when(vocabularyRepository.findById(1)).thenReturn(Optional.of(vocabulary));
        when(vocabularyMapper.toVocabulariesResponse(vocabulary)).thenReturn(vocabularyResponse);

        VocabulariesResponse result = vocabularyService.getVocabularyById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getVocabularyById_Fail() {
        when(vocabularyRepository.findById(99)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> vocabularyService.getVocabularyById(99));
        assertEquals(ErrorCode.VOCABULARY_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void deleteAllVocabulary_Success() {
        when(vocabularyRepository.findAllWithoutSubTopics()).thenReturn(List.of(vocabulary));
        vocabularyService.deleteAllVocabulary();
        verify(vocabularyRepository).deleteById(1);
    }

    @Test
    void countVocabulary_Success() {
        when(vocabularyRepository.count()).thenReturn(100L);
        Long result = vocabularyService.countVocabulary();
        assertEquals(100L, result);
        verify(vocabularyRepository).count();
    }

    @Test
    void deleteVocabulary_Success() {
        vocabularyService.deleteVocabulary(1);
        verify(vocabularyRepository).deleteById(1);
    }

    @Test
    void getSaveVocabularies_Success() {
        Map<String, String> params = new HashMap<>();
        Page<Vocabulary> page = new PageImpl<>(List.of(vocabulary));

        when(vocabularyRepository.findByIsSaveTrue(any(Pageable.class))).thenReturn(page);
        when(vocabularyMapper.toVocabulariesSimpleResponse(any(Vocabulary.class)))
                .thenReturn(vocabulariesSimpleResponse);

        Page<VocabulariesSimpleResponse> result = vocabularyService.getSaveVocabularies(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(vocabularyRepository).findByIsSaveTrue(any(Pageable.class));
    }
}
