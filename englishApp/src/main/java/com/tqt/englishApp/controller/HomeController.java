package com.tqt.englishApp.controller;

import com.tqt.englishApp.repository.QuizRepository;
import com.tqt.englishApp.repository.UserRepository;
import com.tqt.englishApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubTopicService subTopicService;

    @Autowired
    private MainTopicService  mainTopicService;

    @Autowired
    private VocabularyService vocabularyService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private StatsService statsService;

    @RequestMapping("/admin/")
    public String home(Model model){
        int year = LocalDate.now().getYear();
        List<Object[]> userStats = statsService.statsUserByYear(year);

        model.addAttribute("userNum",userRepository.countActiveUsers());
        model.addAttribute("mainTopicNum", mainTopicService.countMainTopic());
        model.addAttribute("subTopicNum", subTopicService.countSubTopic());
        model.addAttribute("vocabularyNum", vocabularyService.countVocabulary());
        model.addAttribute("videoNum", videoService.countVideo());
        model.addAttribute("quizNum", quizService.countQuiz());
        model.addAttribute("userStats", userStats);
        return "admin/home";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }
}
