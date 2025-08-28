package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.SubtitlesResponse;
import com.tqt.englishApp.dto.response.VideoData;
import com.tqt.englishApp.dto.response.VideoProcessingResponse;
import com.tqt.englishApp.entity.Subtitles;
import com.tqt.englishApp.entity.Video;
import com.tqt.englishApp.mapper.SubtitlesMapper;
import com.tqt.englishApp.mapper.VideoMapper;
import com.tqt.englishApp.repository.SubtitlesRepository;
import com.tqt.englishApp.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class VideoProcessingService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private SubtitlesRepository subtitlesRepository;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private SubtitlesMapper subtitlesMapper;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String PROCESSING_API_URL = "https://satyr-dashing-officially.ngrok-free.app/process-video";

    public void processAndSaveVideo(String youtubeUrl) {
        Map<String, String> requestBody = Map.of("url", youtubeUrl);

        VideoProcessingResponse processingResponse = restTemplate.postForObject(
                PROCESSING_API_URL,
                requestBody,
                VideoProcessingResponse.class
        );
        System.out.println("processingResponse: " + processingResponse.getData());

        if (processingResponse.getData() == null) {
            throw new RuntimeException("API trả về null response");
        }

        saveVideoToDatabase(processingResponse.getData());
    }

    private void saveVideoToDatabase(VideoData response) {
        Video video = videoMapper.toVideo(response.getVideo());

        videoRepository.save(video);

        if (response.getSubtitles() != null && !response.getSubtitles().isEmpty()) {
            for (SubtitlesResponse subtitleData : response.getSubtitles()) {
                Subtitles subtitle = subtitlesMapper.toSubtitles(subtitleData);
                subtitle.setVideo(video);
                subtitlesRepository.save(subtitle);
            }
        }
    }
}
