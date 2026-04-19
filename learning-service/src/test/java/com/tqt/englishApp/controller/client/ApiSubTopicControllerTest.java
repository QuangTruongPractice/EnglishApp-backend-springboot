package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.response.subTopic.SubTopicsAdminResponse;
import com.tqt.englishApp.dto.response.subTopic.SubTopicsDetailResponse;
import com.tqt.englishApp.service.SubTopicService;
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

@WebMvcTest(ApiSubTopicController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiSubTopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubTopicService subTopicService;

    @Test
    void getSubTopic_Success() throws Exception {
        SubTopicsDetailResponse response = new SubTopicsDetailResponse();
        when(subTopicService.getSubTopicDetailForClient(1, null)).thenReturn(response);

        mockMvc.perform(get("/api/secure/sub-topics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }
}
