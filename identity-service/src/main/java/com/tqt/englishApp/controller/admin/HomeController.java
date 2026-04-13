package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.repository.UserRepository;
import com.tqt.englishApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatsService statsService;

    @RequestMapping("/admin/")
    public String home(Model model) {
        model.addAttribute("userNum", userRepository.countActiveUsers());

        int currentYear = LocalDate.now().getYear();
        List<Object[]> userStats = statsService.statsUserByYear(currentYear);
        model.addAttribute("userStats", userStats);

        return "admin/home";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

}
