package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.VocabularyRequest;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesResponse;
import com.tqt.englishApp.entity.Vocabulary;
import com.tqt.englishApp.entity.VocabularyMeaning;
import com.tqt.englishApp.enums.Type;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {VocabularyMapperImpl.class, VocabularyMeaningMapperImpl.class})
class VocabularyMapperTest {

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Test
    void toVocabulariesResponse_Success() {
        VocabularyMeaning meaning = VocabularyMeaning.builder()
                .id(1)
                .type(Type.NOUN)
                .definition("A programming language")
                .vnDefinition("Một ngôn ngữ lập trình")
                .build();

        Vocabulary vocabulary = Vocabulary.builder()
                .id(100)
                .word("Java")
                .phonetic("/ˈdʒɑːvə/")
                .audioUrl("http://audio.com")
                .meanings(List.of(meaning))
                .build();

        VocabulariesResponse response = vocabularyMapper.toVocabulariesResponse(vocabulary);

        assertNotNull(response);
        assertEquals(100, response.getId());
        assertEquals("Java", response.getWord());
        assertEquals("/ˈdʒɑːvə/", response.getPhonetic());
        assertEquals(1, response.getMeanings().size());
        assertEquals("A programming language", response.getMeanings().get(0).getDefinition());
    }

    @Test
    void toVocabulary_FromRequest_Success() {
        VocabularyRequest request = VocabularyRequest.builder()
                .word("Spring")
                .phonetic("/sprɪŋ/")
                .audioUrl("http://spring.io")
                .build();

        Vocabulary vocabulary = vocabularyMapper.toVocabulary(request);

        assertNotNull(vocabulary);
        assertEquals("Spring", vocabulary.getWord());
        assertEquals("/sprɪŋ/", vocabulary.getPhonetic());
        // verify ignored fields
        assertNull(vocabulary.getAudioUrl()); // mapped as ignore in mapper
    }
}
