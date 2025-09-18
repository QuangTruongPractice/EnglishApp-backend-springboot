package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.SubTopicRequest;
import com.tqt.englishApp.service.MainTopicService;
import com.tqt.englishApp.service.SubTopicService;
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
@RequestMapping("/admin/subTopics")
public class SubTopicController {
    @Autowired
    private SubTopicService subTopicService;
    @Autowired
    private MainTopicService mainTopicService;

    @GetMapping
    public String listSubTopics(Model model, @RequestParam(name = "name",required = false) String name,
                             @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        Map<String, String> params = new HashMap<>();
        if (name != null && !name.isEmpty()) {
            params.put("name", name);
        }
        params.put("page", String.valueOf(page));

        Page<?> topicsPage = subTopicService.getSubTopics(params);
        model.addAttribute("subTopics", topicsPage.getContent());
        model.addAttribute("totalPages", topicsPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "admin/subTopics";
    }

    @GetMapping("/add")
    public String subTopicsForm(Model model) {
        model.addAttribute("subTopics", new SubTopicRequest());
        model.addAttribute("mainTopics", mainTopicService.findAll());
        return "admin/subTopics_form";
    }

    @PostMapping("/add")
    public String addSubTopics(@ModelAttribute(value = "subTopics") @Valid SubTopicRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/subTopics_form";
        }
        if (request.getId() != null) {
            subTopicService.updateSubTopic(request.getId(), request);
        } else {
            subTopicService.createSubTopic(request);
        }
        return "redirect:/admin/subTopics";
    }

    @GetMapping("/edit/{subTopicsId}")
    public String updateSubTopics(Model model, @PathVariable(value = "subTopicsId") int id ) {
        model.addAttribute("subTopics", subTopicService.getSubTopicById(id));
        model.addAttribute("mainTopics", mainTopicService.findAll());
        return "admin/subTopics_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteSubTopics(@PathVariable int id) {
        subTopicService.deleteSubTopic(id);
        return "redirect:/admin/subTopics";
    }
}
