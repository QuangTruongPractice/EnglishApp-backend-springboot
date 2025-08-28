package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.SubtitlesResponse;
import com.tqt.englishApp.entity.Subtitles;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubtitlesMapper {
    Subtitles toSubtitles(SubtitlesResponse subResponse);
    List<SubtitlesResponse> toSubtitlesResponse(List<Subtitles> sub);
}
