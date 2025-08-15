package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.VocabularyRequest;
import com.tqt.englishApp.dto.response.VocabularyResponse;
import com.tqt.englishApp.service.SubTopicService;
import com.tqt.englishApp.service.VocabularyService;
import com.tqt.englishApp.service.WordTypeService;
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
@RequestMapping("/admin/vocabularies")
public class VocabularyController {
    @Autowired
    private SubTopicService subTopicService;
    @Autowired
    private VocabularyService vocabularyService;
    @Autowired
    private WordTypeService wordTypeService;

    @GetMapping
    public String listSubTopics(Model model, @RequestParam(name = "word",required = false) String word,
                                @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        Map<String, String> params = new HashMap<>();
        if (word != null && !word.isEmpty()) {
            params.put("word", word);
        }
        params.put("page", String.valueOf(page - 1));

        Page<?> vocabularyPage = vocabularyService.getVocabularies(params);
        System.out.println("Topics Page: " + vocabularyPage.getContent());
        vocabularyPage.getContent().forEach(System.out::println);
        model.addAttribute("vocabularies", vocabularyPage.getContent());
        model.addAttribute("totalPages", vocabularyPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "/admin/vocabularies";
    }

    @GetMapping("/add")
    public String vocabularyForm(Model model) {
        model.addAttribute("vocabularies", new VocabularyRequest());
        model.addAttribute("subTopics", subTopicService.getSubTopics(new HashMap<>()));
        model.addAttribute("wordTypes", wordTypeService.findAll());
        return "admin/vocabularies_form";
    }

    @PostMapping("/add")
    public String addVocabulary(@ModelAttribute(value = "vocabularies") @Valid VocabularyRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/vocabularies_form";
        }
        VocabularyResponse response = new VocabularyResponse();
        if (request.getId() != null) {
            response = vocabularyService.updateVocabulary(request.getId(), request);
        } else {
            response = vocabularyService.createVocabulary(request);
        }
        System.out.println(response);
        return "redirect:/admin/vocabularies";
    }

    @GetMapping("/edit/{vocabularyId}")
    public String updateVocabulary(Model model, @PathVariable(value = "vocabularyId") int id ) {
        model.addAttribute("vocabularies", vocabularyService.getVocabularyById(id));
        model.addAttribute("subTopics", subTopicService.getSubTopics(new HashMap<>()));
        model.addAttribute("wordTypes", wordTypeService.findAll());
        return "admin/vocabularies_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteVocabulary(@PathVariable int id) {
        vocabularyService.deleteVocabulary(id);
        return "redirect:/admin/vocabularies";
    }
}
