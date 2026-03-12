package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @Autowired
    private SubTopicService subTopicService;

    @Autowired
    private MainTopicService mainTopicService;

    @Autowired
    private VocabularyService vocabularyService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private QuizService quizService;

    @RequestMapping("/admin/")
    public String home(Model model) {
        model.addAttribute("mainTopicNum", mainTopicService.countMainTopic());
        model.addAttribute("subTopicNum", subTopicService.countSubTopic());
        model.addAttribute("vocabularyNum", vocabularyService.countVocabulary());
        model.addAttribute("videoNum", videoService.countVideo());
        model.addAttribute("quizNum", quizService.countQuiz());
        return "admin/home";
    }

    @RequestMapping("/")
    public String root() {
        return "redirect:/admin/";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }
}
