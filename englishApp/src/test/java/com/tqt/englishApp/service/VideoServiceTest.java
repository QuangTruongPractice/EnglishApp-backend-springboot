package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.SubtitlesResponse;
import com.tqt.englishApp.dto.response.VideoData;
import com.tqt.englishApp.dto.response.VideoResponse;
import com.tqt.englishApp.entity.Subtitles;
import com.tqt.englishApp.entity.Video;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.SubtitlesMapper;
import com.tqt.englishApp.mapper.VideoMapper;
import com.tqt.englishApp.repository.SubtitlesRepository;
import com.tqt.englishApp.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @InjectMocks
    private VideoService videoService;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private SubtitlesRepository subtitlesRepository;

    @Mock
    private VideoMapper videoMapper;

    @Mock
    private SubtitlesMapper subtitlesMapper;

    private Video video;
    private VideoResponse videoResponse;

    @BeforeEach
    void init() {
        video = Video.builder().id(1).title("Test Video").build();
        videoResponse = VideoResponse.builder().id(1).title("Test Video").build();
    }

    @Test
    void saveVideoAndSubtitles_WithSubtitles_Success() {
        VideoResponse videoResp = VideoResponse.builder().id(1).build();
        SubtitlesResponse subResp = SubtitlesResponse.builder().segmentId(1).originalText("Hello").build();
        VideoData videoData = VideoData.builder()
                .video(videoResp)
                .subtitles(List.of(subResp))
                .build();

        Video videoEntity = Video.builder().id(1).build();
        Subtitles subtitleEntity = new Subtitles();

        when(videoMapper.toVideo(videoResp)).thenReturn(videoEntity);
        when(subtitlesMapper.toSubtitles(subResp)).thenReturn(subtitleEntity);

        videoService.saveVideoAndSubtitles(videoData);

        verify(videoRepository).save(videoEntity);
        verify(subtitlesRepository).save(subtitleEntity);
        assertEquals(videoEntity, subtitleEntity.getVideo());
    }

    @Test
    void saveVideoAndSubtitles_NoSubtitles_Success() {
        VideoResponse videoResp = VideoResponse.builder().id(1).build();
        VideoData videoData = VideoData.builder()
                .video(videoResp)
                .subtitles(null)
                .build();

        Video videoEntity = Video.builder().id(1).build();
        when(videoMapper.toVideo(videoResp)).thenReturn(videoEntity);

        videoService.saveVideoAndSubtitles(videoData);

        verify(videoRepository).save(videoEntity);
        verify(subtitlesRepository, never()).save(any());
    }

    @Test
    void getVideos_NoFilter_Success() {
        Map<String, String> params = new HashMap<>();
        Page<Video> videoPage = new PageImpl<>(List.of(video));

        when(videoRepository.findAll(any(Pageable.class))).thenReturn(videoPage);
        when(videoMapper.toVideoResponse(any(Video.class))).thenReturn(videoResponse);

        Page<VideoResponse> result = videoService.getVideos(params);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getVideos_WithFilter_Success() {
        Map<String, String> params = new HashMap<>();
        params.put("title", " Test ");
        Page<Video> videoPage = new PageImpl<>(List.of(video));

        when(videoRepository.findByTitleContainingIgnoreCase(eq("Test"), any(Pageable.class)))
                .thenReturn(videoPage);
        when(videoMapper.toVideoResponse(any(Video.class))).thenReturn(videoResponse);

        Page<VideoResponse> result = videoService.getVideos(params);

        assertNotNull(result);
        verify(videoRepository).findByTitleContainingIgnoreCase(eq("Test"), any(Pageable.class));
    }

    @Test
    void getVideoById_Success() {
        when(videoRepository.findById(1)).thenReturn(Optional.of(video));
        when(videoMapper.toVideoResponse(video)).thenReturn(videoResponse);

        VideoResponse result = videoService.getVideoById(1);

        assertNotNull(result);
    }

    @Test
    void getVideoById_NotFound() {
        when(videoRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> videoService.getVideoById(1));

        assertEquals(ErrorCode.VIDEO_NOT_EXISTED, exception.getErrorCode());
    }

    @Test
    void deleteVideo_Success() {
        videoService.deleteVideo(1);
        verify(videoRepository).deleteById(1);
    }

    @Test
    void countVideo_Success() {
        when(videoRepository.count()).thenReturn(5L);
        assertEquals(5L, videoService.countVideo());
    }
}
