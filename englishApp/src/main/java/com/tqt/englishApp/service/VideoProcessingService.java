package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.VideoProcessingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Service
public class VideoProcessingService {
    @Autowired
    private VideoService videoService;

    @Autowired
    private RestTemplate restTemplate;
    @Value("${external.process-video-url}")
    private String processingApiUrl;

    public void processAndSaveVideo(String youtubeUrl) {
        Map<String, String> requestBody = Map.of("url", youtubeUrl);

        VideoProcessingResponse processingResponse = restTemplate.postForObject(
                processingApiUrl,
                requestBody,
                VideoProcessingResponse.class);
        System.out.println("processingResponse: " + processingResponse.getData());

        if (processingResponse.getData() == null) {
            throw new RuntimeException("API trả về null response");
        }

        videoService.saveVideoAndSubtitles(processingResponse.getData());
    }
}
