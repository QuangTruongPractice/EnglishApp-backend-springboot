package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.dto.request.VocabularyRequest;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesResponse;
import com.tqt.englishApp.enums.Type;
import com.tqt.englishApp.mapper.VocabularyMapper;
import com.tqt.englishApp.service.SubTopicService;
import com.tqt.englishApp.service.VocabularyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;

import com.tqt.englishApp.dto.request.VocabularyMeaningRequest;
import com.tqt.englishApp.mapper.VocabularyMeaningMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tqt.englishApp.dto.response.subTopic.SubTopicsSimpleResponse;

@Controller
@RequestMapping("/admin/vocabularies")
public class VocabularyController {
    @Autowired
    private SubTopicService subTopicService;
    @Autowired
    private VocabularyService vocabularyService;
    @Autowired
    private VocabularyMapper vocabularyMapper;
    @Autowired
    private VocabularyMeaningMapper VocabularyMeaningMapper;

    @GetMapping
    public String listVocabularies(Model model, @RequestParam(name = "word", required = false) String word,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        Map<String, String> params = new HashMap<>();
        if (word != null && !word.isEmpty()) {
            params.put("word", word);
        }
        params.put("page", String.valueOf(page));

        Page<?> vocabularyPage = vocabularyService.getVocabularies(params);
        model.addAttribute("vocabularies", vocabularyPage.getContent());
        model.addAttribute("totalPages", vocabularyPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "admin/vocabularies";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<VocabulariesResponse> searchVocabularies(@RequestParam String word) {
        return vocabularyService.searchVocabularies(word);
    }

    @GetMapping("/add")
    public String vocabularyForm(Model model) {
        VocabularyRequest request = new VocabularyRequest();
        request.setMeanings(new ArrayList<>(List.of(new VocabularyMeaningRequest())));
        model.addAttribute("vocabularies", request);
        model.addAttribute("subTopics", subTopicService.findAll());
        model.addAttribute("wordTypes", Type.values());
        return "admin/vocabularies_form";
    }

    @PostMapping("/add")
    public String addVocabulary(@ModelAttribute(value = "vocabularies") @Valid VocabularyRequest request,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("subTopics", subTopicService.findAll());
            model.addAttribute("wordTypes", Type.values());
            return "admin/vocabularies_form";
        }
        try {
            if (request.getId() != null) {
                vocabularyService.updateVocabulary(request.getId(), request);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật từ vựng thành công!");
            } else {
                vocabularyService.createVocabulary(request);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm từ vựng mới thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thao tác thất bại: " + e.getMessage());
        }
        return "redirect:/admin/vocabularies";
    }

    @GetMapping("/edit/{vocabularyId}")
    public String updateVocabulary(Model model, @PathVariable(value = "vocabularyId") int id) {
        VocabulariesResponse response = vocabularyService.getVocabularyById(id);
        VocabularyRequest request = vocabularyMapper.toVocabularyRequest(response);

        // Map sub-topic IDs from response so the form pre-selects them
        if (response.getSubTopics() != null) {
            request.setSubTopics(response.getSubTopics().stream()
                    .map(SubTopicsSimpleResponse::getId)
                    .collect(Collectors.toList()));
        }

        // Map all meanings from response to request
        if (response.getMeanings() != null) {
            request.setMeanings(VocabularyMeaningMapper.toVocabularyMeaningRequest(response.getMeanings()));
        }

        if (request.getMeanings() == null || request.getMeanings().isEmpty()) {
            request.setMeanings(new ArrayList<>(List.of(new VocabularyMeaningRequest())));
        }

        model.addAttribute("vocabularies", request);
        model.addAttribute("subTopics", subTopicService.findAll());
        model.addAttribute("wordTypes", Type.values());
        return "admin/vocabularies_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteVocabulary(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            vocabularyService.deleteVocabulary(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa từ vựng thành công!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xóa từ vựng này vì đang có dữ liệu liên quan (ví dụ: người dùng đang học từ này).");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xóa thất bại: " + e.getMessage());
        }
        return "redirect:/admin/vocabularies";
    }
}
