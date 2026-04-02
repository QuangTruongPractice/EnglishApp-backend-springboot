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
    void createSubTopic_Success() throws Exception {
        SubTopicsAdminResponse response = new SubTopicsAdminResponse();
        when(subTopicService.createSubTopic(any())).thenReturn(response);

        mockMvc.perform(post("/api/sub-topics")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "New SubTopic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void getSubTopics_Success() throws Exception {
        Page<SubTopicsAdminResponse> page = new PageImpl<>(Collections.emptyList());
        when(subTopicService.getSubTopics(anyMap())).thenReturn(page);

        mockMvc.perform(get("/api/sub-topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray());
    }

    @Test
    void getSubTopic_Success() throws Exception {
        SubTopicsDetailResponse response = new SubTopicsDetailResponse();
        when(subTopicService.getSubTopicDetailForClient(1, null)).thenReturn(response);

        mockMvc.perform(get("/api/sub-topics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void updateSubTopic_Success() throws Exception {
        SubTopicsAdminResponse response = new SubTopicsAdminResponse();
        when(subTopicService.updateSubTopic(eq(1), any())).thenReturn(response);

        mockMvc.perform(put("/api/sub-topics/1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "Updated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void deleteSubTopic_Success() throws Exception {
        mockMvc.perform(delete("/api/sub-topics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Delete sub Topic Sucessfully"));

        verify(subTopicService).deleteSubTopic(1);
    }
}
