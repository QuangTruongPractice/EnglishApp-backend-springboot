package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.dto.request.SubTopicRequest;
import com.tqt.englishApp.dto.response.subTopic.SubTopicsAdminResponse;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsAdminResponse;
import com.tqt.englishApp.service.MainTopicService;
import com.tqt.englishApp.service.SubTopicService;
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

@WebMvcTest(SubTopicController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SubTopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubTopicService subTopicService;

    @MockitoBean
    private MainTopicService mainTopicService;

    @Test
    void listSubTopics_WithoutParams_Success() throws Exception {
        Page<SubTopicsAdminResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(subTopicService.getSubTopics(anyMap())).thenReturn(page);

        mockMvc.perform(get("/admin/subTopics"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subTopics"))
                .andExpect(model().attributeExists("subTopics"))
                .andExpect(model().attribute("currentPage", 1));
    }

    @Test
    void listSubTopics_WithParams_Success() throws Exception {
        Page<SubTopicsAdminResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(subTopicService.getSubTopics(anyMap())).thenReturn(page);

        mockMvc.perform(get("/admin/subTopics")
                .param("name", "Sub")
                .param("page", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subTopics"))
                .andExpect(model().attribute("currentPage", 3));

        verify(subTopicService)
                .getSubTopics(argThat(params -> "Sub".equals(params.get("name")) && "3".equals(params.get("page"))));
    }

    @Test
    void subTopicsForm_Success() throws Exception {
        List<MainTopicsAdminResponse> mainTopics = Collections.singletonList(new MainTopicsAdminResponse());
        when(mainTopicService.findAll()).thenReturn(mainTopics);

        mockMvc.perform(get("/admin/subTopics/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subTopics_form"))
                .andExpect(model().attributeExists("subTopics"))
                .andExpect(model().attribute("mainTopics", mainTopics));
    }

    @Test
    void addSubTopics_ValidationErrors() throws Exception {
        mockMvc.perform(post("/admin/subTopics/add")
                .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subTopics_form"));

        verifyNoInteractions(subTopicService);
    }

    @Test
    void addSubTopics_Create_Success() throws Exception {
        SubTopicRequest request = new SubTopicRequest();
        request.setName("New SubTopic");

        mockMvc.perform(post("/admin/subTopics/add")
                .flashAttr("subTopics", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subTopics"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(subTopicService).createSubTopic(any(SubTopicRequest.class));
    }

    @Test
    void addSubTopics_Update_Success() throws Exception {
        SubTopicRequest request = new SubTopicRequest();
        request.setId(1);
        request.setName("Updated SubTopic");

        mockMvc.perform(post("/admin/subTopics/add")
                .flashAttr("subTopics", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subTopics"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(subTopicService).updateSubTopic(eq(1), any(SubTopicRequest.class));
    }

    @Test
    void updateSubTopics_Success() throws Exception {
        when(subTopicService.getSubTopicDetailForAdmin(anyInt())).thenReturn(new SubTopicsAdminResponse());
        when(mainTopicService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/subTopics/edit/{subTopicsId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subTopics_form"))
                .andExpect(model().attributeExists("subTopics"))
                .andExpect(model().attributeExists("mainTopics"));
    }

    @Test
    void deleteSubTopics_Success() throws Exception {
        mockMvc.perform(post("/admin/subTopics/delete/{id}", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subTopics"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(subTopicService).deleteSubTopic(1);
    }
}
