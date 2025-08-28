package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.SubtitlesResponse;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.dto.response.VideoData;
import com.tqt.englishApp.dto.response.VideoResponse;
import com.tqt.englishApp.service.SubtitlesService;
import com.tqt.englishApp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiVideoController {
    @Autowired
    private VideoService videoService;

    @Autowired
    private SubtitlesService subtitlesService;

    @GetMapping("/videos")
    public ApiResponse<Page<VideoResponse>> getVideos(@RequestParam Map<String, String> params) {
        ApiResponse<Page<VideoResponse>> response = new ApiResponse<>();
        response.setResult(videoService.getVideos(params));
        return response;
    }

    @GetMapping("/videos/{videoId}")
    public ApiResponse<VideoData> getVideo(@PathVariable("videoId") Integer videoId) {
        ApiResponse<VideoData> response = new ApiResponse<>();
        VideoResponse video = videoService.getVideoById(videoId);
        List<SubtitlesResponse> subtitles = subtitlesService.getSubtitlesByVideoId(videoId);

        VideoData videoData = VideoData.builder()
                .video(video)
                .subtitles(subtitles)
                .build();

        response.setResult(videoData);
        return response;
    }
}
