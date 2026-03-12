package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubTopicService subTopicService;

    @MockitoBean
    private MainTopicService mainTopicService;

    @MockitoBean
    private VocabularyService vocabularyService;

    @MockitoBean
    private VideoService videoService;

    @MockitoBean
    private QuizService quizService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void home_Success() throws Exception {
        when(mainTopicService.countMainTopic()).thenReturn(5L);
        when(subTopicService.countSubTopic()).thenReturn(20L);
        when(vocabularyService.countVocabulary()).thenReturn(100L);
        when(videoService.countVideo()).thenReturn(15L);
        when(quizService.countQuiz()).thenReturn(30L);

        mockMvc.perform(get("/admin/"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"))
                .andExpect(model().attribute("mainTopicNum", 5L))
                .andExpect(model().attribute("subTopicNum", 20L))
                .andExpect(model().attribute("vocabularyNum", 100L))
                .andExpect(model().attribute("videoNum", 15L))
                .andExpect(model().attribute("quizNum", 30L));
    }

    @Test
    void root_RedirectsToAdmin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/"));
    }

    @Test
    void login_Success() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}
