package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.SimpleMeaningResponse;
import com.tqt.englishApp.dto.response.WritingPromptResponse;
import com.tqt.englishApp.entity.WritingPrompt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WritingPromptMapper {
    @Mapping(target = "meanings", source = "meanings")
    WritingPromptResponse toWritingPromptResponse(WritingPrompt prompt);

    @Mapping(target = "word", source = "vocabulary.word")
    SimpleMeaningResponse toSimpleMeaningResponse(com.tqt.englishApp.entity.VocabularyMeaning meaning);

    List<WritingPromptResponse> toWritingPromptResponse(List<WritingPrompt> prompts);
}
