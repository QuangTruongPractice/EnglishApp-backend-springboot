package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.MainTopicRequest;
import com.tqt.englishApp.service.MainTopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/topics")
public class MainTopicController {
    @Autowired
    private MainTopicService mainTopicService;

    @GetMapping
    public String listTopics(Model model, @RequestParam(name = "name",required = false) String name,
                            @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        Map<String, String> params = new HashMap<>();
        if (name != null && !name.isEmpty()) {
            params.put("name", name);
        }
        params.put("page", String.valueOf(page));

        Page<?> topicsPage = mainTopicService.getMainTopics(params);
        model.addAttribute("topics", topicsPage.getContent());
        model.addAttribute("totalPages", topicsPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "admin/topics";
    }

    @GetMapping("/add")
    public String topicsForm(Model model) {
        model.addAttribute("topics", new MainTopicRequest());
        return "admin/topics_form";
    }

    @PostMapping("/add")
    public String addTopics(@ModelAttribute(value = "topics") @Valid MainTopicRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/topics_form";
        }
        if (request.getId() != null) {
            mainTopicService.updateMainTopic(request.getId(), request);
        } else {
            mainTopicService.createMainTopic(request);
        }
        return "redirect:/admin/topics";
    }

    @GetMapping("/edit/{topicsId}")
    public String updateTopics(Model model, @PathVariable(value = "topicsId") int id ) {
        model.addAttribute("topics", mainTopicService.getMainTopicById(id));
        return "admin/topics_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteTopics(@PathVariable int id) {
        mainTopicService.deleteMainTopic(id);
        return "redirect:/admin/topics";
    }

}
