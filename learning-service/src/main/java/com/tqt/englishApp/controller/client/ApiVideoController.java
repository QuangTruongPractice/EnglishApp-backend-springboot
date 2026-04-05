package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.SubtitlesResponse;
import com.tqt.englishApp.dto.response.VideoData;
import com.tqt.englishApp.dto.response.VideoResponse;
import com.tqt.englishApp.service.SubtitlesService;
import com.tqt.englishApp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.security.Principal;

@RestController
@RequestMapping("/api/secure")
@CrossOrigin
public class ApiVideoController {
    @Autowired
    private VideoService videoService;

    @Autowired
    private SubtitlesService subtitlesService;

    @GetMapping("/videos")
    public ApiResponse<Page<VideoResponse>> getVideos(@RequestParam Map<String, String> params, Principal principal) {
        ApiResponse<Page<VideoResponse>> response = new ApiResponse<>();
        String userId = principal != null ? principal.getName() : null;
        response.setResult(videoService.getVideos(params, userId));
        return response;
    }

    @GetMapping("/videos/{videoId}")
    public ApiResponse<VideoData> getVideo(@PathVariable("videoId") Integer videoId, Principal principal) {
        ApiResponse<VideoData> response = new ApiResponse<>();
        String userId = principal != null ? principal.getName() : null;
        VideoResponse video = videoService.getVideoById(videoId, userId);
        List<SubtitlesResponse> subtitles = subtitlesService.getSubtitlesByVideoId(videoId);

        VideoData videoData = VideoData.builder()
                .video(video)
                .subtitles(subtitles)
                .build();

        response.setResult(videoData);
        return response;
    }
}
