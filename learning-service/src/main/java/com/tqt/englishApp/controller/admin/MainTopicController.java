package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.dto.request.MainTopicRequest;
import com.tqt.englishApp.enums.LearningGoal;
import com.tqt.englishApp.service.MainTopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/topics")
public class MainTopicController {
    @Autowired
    private MainTopicService mainTopicService;

    @GetMapping
    public String listTopics(Model model, @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        Map<String, String> params = new HashMap<>();
        if (name != null && !name.isEmpty()) {
            params.put("name", name);
        }
        params.put("page", String.valueOf(page));

        Page<?> topicsPage = mainTopicService.getMainTopicsForAdmin(params);
        model.addAttribute("topics", topicsPage.getContent());
        model.addAttribute("totalPages", topicsPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "admin/topics";
    }

    @GetMapping("/add")
    public String topicsForm(Model model) {
        model.addAttribute("topics", new MainTopicRequest());
        model.addAttribute("goals", LearningGoal.values());
        return "admin/topics_form";
    }

    @PostMapping("/add")
    public String addTopics(@ModelAttribute(value = "topics") @Valid MainTopicRequest request, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("goals", LearningGoal.values());
            return "admin/topics_form";
        }
        try {
            if (request.getId() != null) {
                mainTopicService.updateMainTopic(request.getId(), request);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chủ đề thành công!");
            } else {
                mainTopicService.createMainTopic(request);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm chủ đề mới thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thao tác thất bại: " + e.getMessage());
        }
        return "redirect:/admin/topics";
    }

    @GetMapping("/edit/{topicsId}")
    public String updateTopics(Model model, @PathVariable(value = "topicsId") int id) {
        model.addAttribute("topics", mainTopicService.getMainTopicByIdForAdmin(id));
        model.addAttribute("goals", LearningGoal.values());
        return "admin/topics_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteTopics(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            mainTopicService.deleteMainTopic(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chủ đề thành công!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xóa chủ đề này vì có dữ liệu liên quan (ví dụ: các chủ đề con hoặc từ vựng).");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xóa thất bại: " + e.getMessage());
        }
        return "redirect:/admin/topics";
    }

}
