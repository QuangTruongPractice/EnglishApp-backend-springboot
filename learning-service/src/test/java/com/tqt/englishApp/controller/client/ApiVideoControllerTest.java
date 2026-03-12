package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.response.VideoResponse;
import com.tqt.englishApp.service.SubtitlesService;
import com.tqt.englishApp.service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiVideoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiVideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VideoService videoService;

    @MockitoBean
    private SubtitlesService subtitlesService;

    @Test
    void getVideos_Success() throws Exception {
        Page<VideoResponse> page = new PageImpl<>(Collections.emptyList());
        when(videoService.getVideos(anyMap())).thenReturn(page);

        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray());
    }

    @Test
    void getVideo_Success() throws Exception {
        VideoResponse video = new VideoResponse();
        when(videoService.getVideoById(anyInt())).thenReturn(video);
        when(subtitlesService.getSubtitlesByVideoId(anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/videos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.video").exists())
                .andExpect(jsonPath("$.result.subtitles").isArray());
    }
}
