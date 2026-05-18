package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.SimpleMeaningResponse;
import com.tqt.englishApp.dto.response.WritingPromptResponse;
import com.tqt.englishApp.entity.WritingPrompt;
import com.tqt.englishApp.enums.WritingPromptType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class WritingPromptMapper {

    @Mapping(target = "meanings", ignore = true)
    @Mapping(target = "question", ignore = true)
    public abstract WritingPromptResponse toWritingPromptResponse(WritingPrompt prompt);

    public abstract List<WritingPromptResponse> toWritingPromptResponse(List<WritingPrompt> prompts);

    @AfterMapping
    protected void fillDetails(WritingPrompt prompt, @MappingTarget WritingPromptResponse response) {
        if (prompt.getTargetMeanings() == null || prompt.getTargetMeanings().isEmpty()) return;

        List<SimpleMeaningResponse> meanings = prompt.getTargetMeanings().stream()
                .map(m -> SimpleMeaningResponse.builder()
                        .id(m.getId())
                        .word(m.getVocabulary().getWord())
                        .build())
                .collect(Collectors.toList());

        response.setMeanings(meanings);

        // Generate Question
        String words = meanings.stream()
                .map(SimpleMeaningResponse::getWord)
                .collect(Collectors.joining(", "));

        if (prompt.getType() == WritingPromptType.SENTENCE) {
            response.setQuestion("Hãy viết 1 câu sử dụng các từ sau: " + words);
        } else {
            response.setQuestion("Hãy viết 1 đoạn văn hoặc câu chuyện ngắn sử dụng các từ sau: " + words);
        }
    }
}
