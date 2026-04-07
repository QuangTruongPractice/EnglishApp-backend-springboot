package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.dto.request.AnswerRequest;
import com.tqt.englishApp.service.AnswerService;
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
@RequestMapping("/admin/answers")
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuizService quizService;

    @GetMapping
    public String listAnswers(Model model, @RequestParam(name = "answer", required = false) String answer,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        Map<String, String> params = new HashMap<>();
        if (answer != null && !answer.isEmpty()) {
            params.put("answer", answer);
        }
        params.put("page", String.valueOf(page));

        Page<?> answersPage = answerService.getAnswer(params);
        model.addAttribute("answers", answersPage.getContent());
        model.addAttribute("totalPages", answersPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "admin/answers";
    }

    @GetMapping("/add")
    public String answersForm(Model model) {
        model.addAttribute("answers", new AnswerRequest());
        model.addAttribute("quizs", quizService.getAllQuizzes());
        return "admin/answers_form";
    }

    @PostMapping("/add")
    public String addAnswers(@ModelAttribute(value = "answers") @Valid AnswerRequest request, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("quizs", quizService.getAllQuizzes());
            return "admin/answers_form";
        }
        try {
            if (request.getId() != null) {
                answerService.updateAnswer(request, request.getId());
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật câu trả lời thành công!");
            } else {
                answerService.createAnswer(request);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm câu trả lời mới thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thao tác thất bại: " + e.getMessage());
        }
        return "redirect:/admin/answers";
    }

    @GetMapping("/edit/{answerId}")
    public String updateAnswers(Model model, @PathVariable(value = "answerId") int id) {
        model.addAttribute("answers", answerService.getAnswerRequestById(id));
        model.addAttribute("quizs", quizService.getAllQuizzes());
        return "admin/answers_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteAnswers(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            answerService.deleteAnswer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa câu trả lời thành công!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xóa câu trả lời này vì có dữ liệu liên quan.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xóa thất bại: " + e.getMessage());
        }
        return "redirect:/admin/answers";
    }
}
