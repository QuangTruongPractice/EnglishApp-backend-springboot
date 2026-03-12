package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.SubtitlesResponse;
import com.tqt.englishApp.mapper.SubtitlesMapper;
import com.tqt.englishApp.repository.SubtitlesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubtitlesServiceTest {

    @InjectMocks
    private SubtitlesService subtitlesService;

    @Mock
    private SubtitlesRepository subtitlesRepository;

    @Mock
    private SubtitlesMapper subtitlesMapper;

    @Test
    void getSubtitlesByVideoId_Success() {
        Integer videoId = 1;
        List<com.tqt.englishApp.entity.Subtitles> subtitlesList = new ArrayList<>();
        List<SubtitlesResponse> expectedResponses = new ArrayList<>();

        when(subtitlesRepository.findByVideoId(videoId)).thenReturn(subtitlesList);
        when(subtitlesMapper.toSubtitlesResponse(subtitlesList)).thenReturn(expectedResponses);

        List<SubtitlesResponse> result = subtitlesService.getSubtitlesByVideoId(videoId);

        assertNotNull(result);
        verify(subtitlesRepository).findByVideoId(videoId);
        verify(subtitlesMapper).toSubtitlesResponse(subtitlesList);
    }
}
