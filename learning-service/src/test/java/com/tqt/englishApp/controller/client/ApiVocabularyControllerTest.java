package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.response.vocabulary.VocabulariesResponse;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesSimpleResponse;
import com.tqt.englishApp.service.VocabularyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    void getVocabulary_Success() throws Exception {
        VocabulariesResponse response = new VocabulariesResponse();
        when(vocabularyService.getVocabularyById(eq(1), any())).thenReturn(response);

        mockMvc.perform(get("/api/vocabulary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void getSaveVocabularies_Success() throws Exception {
        Page<VocabulariesSimpleResponse> page = new PageImpl<>(Collections.emptyList());
        when(vocabularyService.getSaveVocabularies(any(), anyMap())).thenReturn(page);

        mockMvc.perform(get("/api/secure/vocabulary/save")
                .principal(() -> "testUserId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray());
    }

    @Test
    void toggleSaveVocabulary_Success() throws Exception {
        mockMvc.perform(post("/api/secure/vocabulary/1/toggle")
                .principal(() -> "testUserId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Toggle save vocabulary successfully"));

        verify(vocabularyService).toggleSaveVocabulary(eq(1), any());
    }
}
