package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.SubtitlesResponse;
import com.tqt.englishApp.entity.Subtitles;
import com.tqt.englishApp.mapper.SubtitlesMapper;
import com.tqt.englishApp.repository.SubtitlesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubtitlesService {
    @Autowired
    private SubtitlesRepository subtitlesRepository;

    @Autowired
    private SubtitlesMapper subtitlesMapper;

    public List<SubtitlesResponse> getSubtitlesByVideoId(Integer videoId){
        return subtitlesMapper.toSubtitlesResponse(subtitlesRepository.findByVideoId(videoId));
    }
}
