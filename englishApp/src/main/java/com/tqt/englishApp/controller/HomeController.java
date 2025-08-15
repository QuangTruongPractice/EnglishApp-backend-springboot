package com.tqt.englishApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/admin/")
    public String home(){
        return "admin/home";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }
}
