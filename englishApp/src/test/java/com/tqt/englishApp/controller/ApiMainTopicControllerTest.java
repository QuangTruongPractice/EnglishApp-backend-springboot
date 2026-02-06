package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.response.MainTopicResponse;
import com.tqt.englishApp.service.MainTopicService;
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

@WebMvcTest(ApiMainTopicController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiMainTopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MainTopicService mainTopicService;

    @Test
    void createMainTopic_Success() throws Exception {
        MainTopicResponse response = new MainTopicResponse();
        when(mainTopicService.createMainTopic(any())).thenReturn(response);

        mockMvc.perform(post("/api/main-topics")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "New Topic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void getMainTopics_Success() throws Exception {
        Page<MainTopicResponse> page = new PageImpl<>(Collections.emptyList());
        when(mainTopicService.getMainTopics(anyMap())).thenReturn(page);

        mockMvc.perform(get("/api/main-topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray());
    }

    @Test
    void getMainTopic_Success() throws Exception {
        MainTopicResponse response = new MainTopicResponse();
        when(mainTopicService.getMainTopicById(1)).thenReturn(response);

        mockMvc.perform(get("/api/main-topics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void updateMainTopic_Success() throws Exception {
        MainTopicResponse response = new MainTopicResponse();
        when(mainTopicService.updateMainTopic(eq(1), any())).thenReturn(response);

        mockMvc.perform(put("/api/main-topics/1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "Updated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void deleteMainTopic_Success() throws Exception {
        mockMvc.perform(delete("/api/main-topics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Delete main Topic Sucessfully"));

        verify(mainTopicService).deleteMainTopic(1);
    }
}
