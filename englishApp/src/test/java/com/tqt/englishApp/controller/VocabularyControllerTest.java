package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.VocabularyRequest;
import com.tqt.englishApp.dto.response.VocabularyResponse;
import com.tqt.englishApp.dto.response.SubTopicResponse;
import com.tqt.englishApp.entity.WordType;
import com.tqt.englishApp.enums.Type;
import com.tqt.englishApp.service.SubTopicService;
import com.tqt.englishApp.service.VocabularyService;
import com.tqt.englishApp.service.WordTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VocabularyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class VocabularyControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private VocabularyService vocabularyService;

        @MockitoBean
        private SubTopicService subTopicService;

        @MockitoBean
        private WordTypeService wordTypeService;

        @Test
        void listVocabularies_WithoutParams_Success() throws Exception {
                Page<VocabularyResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
                when(vocabularyService.getVocabularies(anyMap())).thenReturn(page);

                mockMvc.perform(get("/admin/vocabularies"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/vocabularies"))
                                .andExpect(model().attributeExists("vocabularies"))
                                .andExpect(model().attribute("currentPage", 1));
        }

        @Test
        void listVocabularies_WithParams_Success() throws Exception {
                Page<VocabularyResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
                when(vocabularyService.getVocabularies(anyMap())).thenReturn(page);

                mockMvc.perform(get("/admin/vocabularies")
                                .param("word", "hello")
                                .param("page", "2"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/vocabularies"))
                                .andExpect(model().attribute("currentPage", 2));

                verify(vocabularyService).getVocabularies(
                                argThat(params -> "hello".equals(params.get("word"))
                                                && "2".equals(params.get("page"))));
        }

        @Test
        void vocabularyForm_Success() throws Exception {
                List<SubTopicResponse> subTopics = Collections.singletonList(new SubTopicResponse());
                WordType wordType = WordType.builder()
                                .id(1)
                                .type(Type.NOUN)
                                .build();
                List<WordType> wordTypes = Collections.singletonList(wordType);
                when(subTopicService.findAll()).thenReturn(subTopics);
                when(wordTypeService.findAll()).thenReturn(wordTypes);

                mockMvc.perform(get("/admin/vocabularies/add"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/vocabularies_form"))
                                .andExpect(model().attributeExists("vocabularies"))
                                .andExpect(model().attribute("subTopics", subTopics))
                                .andExpect(model().attribute("wordTypes", wordTypes));
        }

        @Test
        void addVocabulary_ValidationErrors() throws Exception {
                VocabularyRequest request = new VocabularyRequest();
                request.setWord(""); // Triggers validation error in controller if @NotBlank

                mockMvc.perform(post("/admin/vocabularies/add")
                                .flashAttr("vocabularies", request))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/vocabularies"));

                verify(vocabularyService).createVocabulary(any(VocabularyRequest.class));
        }

        @Test
        void addVocabulary_Create_Success() throws Exception {
                VocabularyRequest request = new VocabularyRequest();
                request.setWord("test");

                mockMvc.perform(post("/admin/vocabularies/add")
                                .flashAttr("vocabularies", request))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/vocabularies"));

                verify(vocabularyService).createVocabulary(any(VocabularyRequest.class));
        }

        @Test
        void addVocabulary_Update_Success() throws Exception {
                VocabularyRequest request = new VocabularyRequest();
                request.setId(1);
                request.setWord("updated");

                mockMvc.perform(post("/admin/vocabularies/add")
                                .flashAttr("vocabularies", request))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/vocabularies"));

                verify(vocabularyService).updateVocabulary(eq(1), any(VocabularyRequest.class));
        }

        @Test
        void updateVocabulary_Success() throws Exception {
                when(vocabularyService.getVocabularyById(anyInt())).thenReturn(new VocabularyResponse());
                when(subTopicService.findAll()).thenReturn(Collections.emptyList());
                when(wordTypeService.findAll()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/admin/vocabularies/edit/{vocabularyId}", 1))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/vocabularies_form"))
                                .andExpect(model().attributeExists("vocabularies"))
                                .andExpect(model().attributeExists("subTopics"))
                                .andExpect(model().attributeExists("wordTypes"));
        }

        @Test
        void deleteVocabulary_Success() throws Exception {
                mockMvc.perform(post("/admin/vocabularies/delete/{id}", 1))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/vocabularies"));

                verify(vocabularyService).deleteVocabulary(1);
        }
}
