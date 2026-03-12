package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.dto.request.QuizRequest;
import com.tqt.englishApp.dto.response.quiz.QuizDetailResponse;
import com.tqt.englishApp.service.QuizService;
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
@RequestMapping("/admin/quizs")
public class QuizController {
    @Autowired
    private QuizService quizService;

    @GetMapping
    public String listQuizs(Model model, @RequestParam(name = "question", required = false) String question,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        Map<String, String> params = new HashMap<>();
        if (question != null && !question.isEmpty()) {
            params.put("question", question);
        }
        params.put("page", String.valueOf(page));

        Page<QuizDetailResponse> quizsPage = quizService.getQuizzesAdmin(params);
        System.out.println("Quizs Page = " + quizsPage);
        System.out.println("Nội dung quizsPage = " + quizsPage.getContent());
        model.addAttribute("quizs", quizsPage.getContent());
        model.addAttribute("totalPages", quizsPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "admin/quizs";
    }

    @GetMapping("/add")
    public String quizsForm(Model model) {
        model.addAttribute("quizs", new QuizRequest());
        return "admin/quizs_form";
    }

    @PostMapping("/add")
    public String addQuizs(@ModelAttribute(value = "quizs") @Valid QuizRequest request, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/quizs_form";
        }
        try {
            if (request.getId() != null) {
                quizService.updateQuiz(request, request.getId());
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật câu hỏi thành công!");
            } else {
                quizService.createQuiz(request);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm câu hỏi mới thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thao tác thất bại: " + e.getMessage());
        }
        return "redirect:/admin/quizs";
    }

    @GetMapping("/edit/{quizId}")
    public String updateQuizs(Model model, @PathVariable(value = "quizId") int id) {
        model.addAttribute("quizs", quizService.getQuizById(id));
        return "admin/quizs_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteQuizs(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            quizService.deleteQuiz(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa câu hỏi thành công!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xóa câu hỏi này vì có dữ liệu liên quan (ví dụ: các câu trả lời đang tham chiếu tới nó).");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xóa thất bại: " + e.getMessage());
        }
        return "redirect:/admin/quizs";
    }
}
