package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.dto.request.VideoRequest;
import com.tqt.englishApp.dto.response.VideoResponse;
import com.tqt.englishApp.service.VideoProcessingService;
import com.tqt.englishApp.service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VideoService videoService;

    @MockitoBean
    private VideoProcessingService videoProcessingService;

    @Test
    void listVideos_WithoutParams_Success() throws Exception {
        Page<VideoResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(videoService.getVideos(anyMap())).thenReturn(page);

        mockMvc.perform(get("/admin/videos"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/videos"))
                .andExpect(model().attributeExists("videos"))
                .andExpect(model().attribute("currentPage", 1));
    }

    @Test
    void listVideos_WithParams_Success() throws Exception {
        Page<VideoResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(videoService.getVideos(anyMap())).thenReturn(page);

        mockMvc.perform(get("/admin/videos")
                .param("title", "English")
                .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/videos"))
                .andExpect(model().attribute("currentPage", 2));

        verify(videoService)
                .getVideos(argThat(params -> "English".equals(params.get("title")) && "2".equals(params.get("page"))));
    }

    @Test
    void videosForm_Success() throws Exception {
        mockMvc.perform(get("/admin/videos/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/videos_form"))
                .andExpect(model().attributeExists("videos"));
    }

    @Test
    void addVideo_ValidationErrors() throws Exception {
        VideoRequest request = new VideoRequest();
        request.setYoutubeUrl("");

        mockMvc.perform(post("/admin/videos/add")
                .flashAttr("videos", request))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/videos_form"));

        verifyNoInteractions(videoProcessingService);
    }

    @Test
    void addVideo_Success() throws Exception {
        VideoRequest request = new VideoRequest();
        request.setYoutubeUrl("https://youtube.com/watch?v=123");

        mockMvc.perform(post("/admin/videos/add")
                .flashAttr("videos", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/videos"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(videoProcessingService).processAndSaveVideo("https://youtube.com/watch?v=123");
    }

    @Test
    void updateVideo_Success() throws Exception {
        when(videoService.getVideoById(anyInt())).thenReturn(new VideoResponse());

        mockMvc.perform(get("/admin/videos/edit/{videoId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/videos_form"))
                .andExpect(model().attributeExists("videos"));
    }

    @Test
    void deleteVideo_Success() throws Exception {
        mockMvc.perform(post("/admin/videos/delete/{id}", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/videos"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(videoService).deleteVideo(1);
    }
}
