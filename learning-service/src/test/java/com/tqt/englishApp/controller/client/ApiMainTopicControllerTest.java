package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.response.mainTopic.MainTopicsDetailResponse;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsResponse;
import com.tqt.englishApp.service.MainTopicService;
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

@WebMvcTest(ApiMainTopicController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiMainTopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MainTopicService mainTopicService;

    @Test
    void getLearningPath_Success() throws Exception {
        MainTopicsResponse response = new MainTopicsResponse();
        response.setId(1);
        response.setName("Topic 1");

        when(mainTopicService.getLearningPathForClient(anyString())).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/secure/learning-path"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result[0].name").value("Topic 1"));
    }

    @Test
    void getMainTopics_Success() throws Exception {
        Page<MainTopicsResponse> page = new PageImpl<>(Collections.emptyList());
        when(mainTopicService.getMainTopicsForClient(anyMap())).thenReturn(page);

        mockMvc.perform(get("/api/main-topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray());
    }

    @Test
    void getMainTopicDetail_Success() throws Exception {
        MainTopicsDetailResponse response = new MainTopicsDetailResponse();
        response.setId(1);
        response.setName("Detail Topic");
        when(mainTopicService.getMainTopicDetailForClient(1)).thenReturn(response);

        mockMvc.perform(get("/api/main-topics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result.name").value("Detail Topic"));
    }
}
