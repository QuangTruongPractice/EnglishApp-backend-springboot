package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.MainTopicRequest;
import com.tqt.englishApp.dto.request.QuizRequest;
import com.tqt.englishApp.service.QuizService;
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
@RequestMapping("/admin/quizs")
public class QuizController {
    @Autowired
    private QuizService quizService;

    @GetMapping
    public String listQuizs(Model model, @RequestParam(name = "question",required = false) String question,
                             @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        Map<String, String> params = new HashMap<>();
        if (question != null && !question.isEmpty()) {
            params.put("question", question);
        }
        params.put("page", String.valueOf(page));

        Page<?> quizsPage = quizService.getQuiz(params);
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
    public String addQuizs(@ModelAttribute(value = "quizs") @Valid QuizRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/quizs_form";
        }
        if (request.getId() != null) {
            quizService.updateQuiz(request, request.getId());
        } else {
            quizService.createQuiz(request);
        }
        return "redirect:/admin/quizs";
    }

    @GetMapping("/edit/{quizId}")
    public String updateQuizs(Model model, @PathVariable(value = "quizId") int id ) {
        model.addAttribute("quizs", quizService.getQuizById(id));
        return "admin/quizs_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteQuizs(@PathVariable int id) {
        quizService.deleteQuiz(id);
        return "redirect:/admin/quizs";
    }
}
