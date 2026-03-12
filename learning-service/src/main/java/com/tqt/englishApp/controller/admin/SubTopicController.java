package com.tqt.englishApp.controller.admin;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;

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
    public String listSubTopics(Model model, @RequestParam(name = "name", required = false) String name,
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
    public String addSubTopics(@ModelAttribute(value = "subTopics") @Valid SubTopicRequest request,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("mainTopics", mainTopicService.findAll());
            return "admin/subTopics_form";
        }
        try {
            if (request.getId() != null) {
                subTopicService.updateSubTopic(request.getId(), request);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chủ đề con thành công!");
            } else {
                subTopicService.createSubTopic(request);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm chủ đề con mới thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thao tác thất bại: " + e.getMessage());
        }
        return "redirect:/admin/subTopics";
    }

    @GetMapping("/edit/{subTopicsId}")
    public String updateSubTopics(Model model, @PathVariable(value = "subTopicsId") int id) {
        model.addAttribute("subTopics", subTopicService.getSubTopicDetailForAdmin(id));
        model.addAttribute("mainTopics", mainTopicService.findAll());
        return "admin/subTopics_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteSubTopics(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            subTopicService.deleteSubTopic(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chủ đề con thành công!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xóa chủ đề con này vì có dữ liệu liên quan (ví dụ: các từ vựng thuộc chủ đề này).");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xóa thất bại: " + e.getMessage());
        }
        return "redirect:/admin/subTopics";
    }
}
