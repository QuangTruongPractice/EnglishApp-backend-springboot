package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.dto.request.VocabularyRequest;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesResponse;
import com.tqt.englishApp.dto.response.subTopic.SubTopicsAdminResponse;
import com.tqt.englishApp.enums.Type;
import com.tqt.englishApp.mapper.VocabularyMapper;
import com.tqt.englishApp.mapper.VocabularyMeaningMapper;
import com.tqt.englishApp.service.SubTopicService;
import com.tqt.englishApp.service.VocabularyService;
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
        private VocabularyMapper vocabularyMapper;

        @MockitoBean
        private VocabularyMeaningMapper VocabularyMeaningMapper;

        @Test
        void listVocabularies_WithoutParams_Success() throws Exception {
                Page<VocabulariesResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
                when(vocabularyService.getVocabularies(anyMap())).thenReturn(page);

                mockMvc.perform(get("/admin/vocabularies"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/vocabularies"))
                                .andExpect(model().attributeExists("vocabularies"))
                                .andExpect(model().attribute("currentPage", 1));
        }

        @Test
        void listVocabularies_WithParams_Success() throws Exception {
                Page<VocabulariesResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
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
                List<SubTopicsAdminResponse> subTopics = Collections.singletonList(new SubTopicsAdminResponse());
                when(subTopicService.findAll()).thenReturn(subTopics);

                mockMvc.perform(get("/admin/vocabularies/add"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/vocabularies_form"))
                                .andExpect(model().attributeExists("vocabularies"))
                                .andExpect(model().attribute("subTopics", subTopics))
                                .andExpect(model().attribute("wordTypes", Type.values()));
        }

        @Test
        void addVocabulary_Create_Success() throws Exception {
                VocabularyRequest request = new VocabularyRequest();
                request.setWord("test");
                request.setPhonetic("/test/");

                mockMvc.perform(post("/admin/vocabularies/add")
                                .flashAttr("vocabularies", request))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/vocabularies"))
                                .andExpect(flash().attributeExists("successMessage"));

                verify(vocabularyService).createVocabulary(any(VocabularyRequest.class));
        }

        @Test
        void addVocabulary_Update_Success() throws Exception {
                VocabularyRequest request = new VocabularyRequest();
                request.setId(1);
                request.setWord("updated");
                request.setPhonetic("/updated/");

                mockMvc.perform(post("/admin/vocabularies/add")
                                .flashAttr("vocabularies", request))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/vocabularies"))
                                .andExpect(flash().attributeExists("successMessage"));

                verify(vocabularyService).updateVocabulary(eq(1), any(VocabularyRequest.class));
        }

        @Test
        void updateVocabulary_Success() throws Exception {
                VocabulariesResponse response = new VocabulariesResponse();
                response.setId(1);
                VocabularyRequest request = new VocabularyRequest();
                request.setId(1);

                when(vocabularyService.getVocabularyById(anyInt())).thenReturn(response);
                when(vocabularyMapper.toVocabularyRequest(any())).thenReturn(request);
                when(subTopicService.findAll()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/admin/vocabularies/edit/{vocabularyId}", 1))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/vocabularies_form"))
                                .andExpect(model().attributeExists("vocabularies"))
                                .andExpect(model().attributeExists("subTopics"))
                                .andExpect(model().attribute("wordTypes", Type.values()));
        }

        @Test
        void deleteVocabulary_Success() throws Exception {
                mockMvc.perform(post("/admin/vocabularies/delete/{id}", 1))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/vocabularies"))
                                .andExpect(flash().attributeExists("successMessage"));

                verify(vocabularyService).deleteVocabulary(1);
        }
}
