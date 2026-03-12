
package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.dto.request.MainTopicRequest;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsAdminResponse;
import com.tqt.englishApp.service.MainTopicService;
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
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainTopicController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MainTopicControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private MainTopicService mainTopicService;

        @Test
        void listTopics_WithoutParams_Success() throws Exception {
                Page<MainTopicsAdminResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
                when(mainTopicService.getMainTopicsForAdmin(anyMap())).thenReturn(page);

                mockMvc.perform(get("/admin/topics"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/topics"))
                                .andExpect(model().attributeExists("topics"))
                                .andExpect(model().attribute("currentPage", 1));
        }

        @Test
        void listTopics_WithParams_Success() throws Exception {
                Page<MainTopicsAdminResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
                Map<String, String> expectedParams = new HashMap<>();
                expectedParams.put("name", "Test");
                expectedParams.put("page", "2");

                when(mainTopicService.getMainTopicsForAdmin(anyMap())).thenReturn(page);

                mockMvc.perform(get("/admin/topics")
                                .param("name", "Test")
                                .param("page", "2"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/topics"))
                                .andExpect(model().attribute("currentPage", 2));

                verify(mainTopicService)
                                .getMainTopicsForAdmin(
                                                argThat(params -> "Test".equals(params.get("name"))
                                                                && "2".equals(params.get("page"))));
        }

        @Test
        void topicsForm_Success() throws Exception {
                mockMvc.perform(get("/admin/topics/add"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/topics_form"))
                                .andExpect(model().attributeExists("topics"));
        }

        @Test
        void addTopics_ValidationErrors() throws Exception {
                mockMvc.perform(post("/admin/topics/add")
                                .param("name", "")) // Assuming name is @NotBlank
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/topics_form"));

                verifyNoInteractions(mainTopicService);
        }

        @Test
        void addTopics_Create_Success() throws Exception {
                MainTopicRequest request = new MainTopicRequest();
                request.setName("New Topic");

                mockMvc.perform(post("/admin/topics/add")
                                .flashAttr("topics", request))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/topics"))
                                .andExpect(flash().attributeExists("successMessage"));

                verify(mainTopicService).createMainTopic(any(MainTopicRequest.class));
        }

        @Test
        void addTopics_Update_Success() throws Exception {
                MainTopicRequest request = new MainTopicRequest();
                request.setId(1);
                request.setName("Updated Topic");

                mockMvc.perform(post("/admin/topics/add")
                                .flashAttr("topics", request))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/topics"))
                                .andExpect(flash().attributeExists("successMessage"));

                verify(mainTopicService).updateMainTopic(eq(1), any(MainTopicRequest.class));
        }

        @Test
        void updateTopics_Success() throws Exception {
                when(mainTopicService.getMainTopicByIdForAdmin(anyInt())).thenReturn(new MainTopicsAdminResponse());

                mockMvc.perform(get("/admin/topics/edit/{topicsId}", 1))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/topics_form"))
                                .andExpect(model().attributeExists("topics"));
        }

        @Test
        void deleteTopics_Success() throws Exception {
                mockMvc.perform(post("/admin/topics/delete/{id}", 1))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/topics"))
                                .andExpect(flash().attributeExists("successMessage"));

                verify(mainTopicService).deleteMainTopic(1);
        }
}
