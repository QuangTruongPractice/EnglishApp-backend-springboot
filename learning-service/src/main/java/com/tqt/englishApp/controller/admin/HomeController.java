package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.service.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
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

    @RequestMapping({"/admin", "/admin/"})
    @Transactional(readOnly = true)
    public String home(Model model) {
        model.addAttribute("mainTopicNum", mainTopicService.countMainTopic());
        model.addAttribute("subTopicNum", subTopicService.countSubTopic());
        model.addAttribute("vocabularyNum", vocabularyService.countVocabulary());
        model.addAttribute("videoNum", videoService.countVideo());
        model.addAttribute("quizNum", quizService.countQuiz());

        // Recent Activity
        model.addAttribute("recentVocabularies", vocabularyService.getRecentVocabularies());
        model.addAttribute("recentQuizzes", quizService.getRecentQuizzes());

        // Distribution Data for Charts
        model.addAttribute("levelDistribution", vocabularyService.getLevelDistribution());
        model.addAttribute("typeDistribution", quizService.getTypeDistribution());

        return "admin/home";
    }

    @RequestMapping("/")
    public String root() {
        return "redirect:/admin";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }
}
