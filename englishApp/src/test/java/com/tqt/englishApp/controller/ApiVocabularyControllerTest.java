package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.response.VocabularyResponse;
import com.tqt.englishApp.service.VocabularyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiVocabularyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiVocabularyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VocabularyService vocabularyService;

    @Test
    void createVocabulary_Success() throws Exception {
        VocabularyResponse response = new VocabularyResponse();
        when(vocabularyService.createVocabulary(any())).thenReturn(response);

        mockMvc.perform(post("/api/vocabulary")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("word", "hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void getVocabularies_Success() throws Exception {
        Page<VocabularyResponse> page = new PageImpl<>(Collections.emptyList());
        when(vocabularyService.getVocabularies(anyMap())).thenReturn(page);

        mockMvc.perform(get("/api/vocabulary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray());
    }

    @Test
    void getVocabulary_Success() throws Exception {
        VocabularyResponse response = new VocabularyResponse();
        when(vocabularyService.getVocabularyById(1)).thenReturn(response);

        mockMvc.perform(get("/api/vocabulary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void updateVocabulary_Success() throws Exception {
        VocabularyResponse response = new VocabularyResponse();
        when(vocabularyService.updateVocabulary(eq(1), any())).thenReturn(response);

        mockMvc.perform(put("/api/vocabulary/1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("word", "updated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void deleteVocabulary_Success() throws Exception {
        mockMvc.perform(delete("/api/vocabulary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Delete vocabulary Sucessfully"));

        verify(vocabularyService).deleteVocabulary(1);
    }
}
