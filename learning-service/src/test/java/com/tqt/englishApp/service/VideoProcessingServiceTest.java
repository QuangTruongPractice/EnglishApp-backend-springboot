package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.VideoData;
import com.tqt.englishApp.dto.response.VideoProcessingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoProcessingServiceTest {

    @InjectMocks
    private VideoProcessingService videoProcessingService;

    @Mock
    private VideoService videoService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(videoProcessingService, "processingApiUrl", "http://test-url.com");
    }

    @Test
    void processAndSaveVideo_Success() {
        String youtubeUrl = "https://youtube.com/test";
        VideoData videoData = new VideoData();
        VideoProcessingResponse response = new VideoProcessingResponse(videoData, true);

        when(restTemplate.postForObject(eq("http://test-url.com"), any(Map.class), eq(VideoProcessingResponse.class)))
                .thenReturn(response);

        videoProcessingService.processAndSaveVideo(youtubeUrl);

        verify(videoService).saveVideoAndSubtitles(videoData);
    }

    @Test
    void processAndSaveVideo_Failure_NullData() {
        String youtubeUrl = "https://youtube.com/test";
        VideoProcessingResponse response = new VideoProcessingResponse(null, true);

        when(restTemplate.postForObject(anyString(), any(), eq(VideoProcessingResponse.class)))
                .thenReturn(response);

        assertThrows(RuntimeException.class, () -> videoProcessingService.processAndSaveVideo(youtubeUrl));
    }
}
