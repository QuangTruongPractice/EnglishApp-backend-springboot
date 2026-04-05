package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.SimpleMeaningResponse;
import com.tqt.englishApp.dto.response.WritingPromptResponse;
import com.tqt.englishApp.entity.WritingPrompt;
import com.tqt.englishApp.enums.WritingPromptType;
import com.tqt.englishApp.repository.VocabularyMeaningRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class WritingPromptMapper {

    @Autowired
    protected VocabularyMeaningRepository meaningRepository;

    @Mapping(target = "meanings", ignore = true)
    @Mapping(target = "question", ignore = true)
    public abstract WritingPromptResponse toWritingPromptResponse(WritingPrompt prompt);

    public abstract List<WritingPromptResponse> toWritingPromptResponse(List<WritingPrompt> prompts);

    @AfterMapping
    protected void fillDetails(WritingPrompt prompt, @MappingTarget WritingPromptResponse response) {
        if (prompt.getTargetMeaningIds() == null || prompt.getTargetMeaningIds().isEmpty()) return;

        List<Integer> ids = Arrays.stream(prompt.getTargetMeaningIds().split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<SimpleMeaningResponse> meanings = ids.stream()
                .map(id -> meaningRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
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
