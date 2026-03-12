package com.tqt.englishApp.controller;

import com.tqt.englishApp.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/stats")
public class StatsController {
    @Autowired
    private StatsService statsService;

    @GetMapping
    public String statsPage(
            @RequestParam(value = "type", required = false, defaultValue = "default") String type,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "quarter", required = false) Integer quarter,
            @RequestParam(value = "month", required = false) Integer month,
            Model model) {

        List<Object[]> userStats = new ArrayList<>();
        String statType = type;

        switch (statType) {
            case "year":
                if (year != null) {
                    userStats = statsService.statsUserByYear(year);
                }
                break;

            case "quarter":
                if (year != null && quarter != null) {
                    userStats = statsService.statsUserByQuarter(year, quarter);
                }
                break;

            case "month":
                if (year != null && month != null) {
                    userStats = statsService.statsUserByMonth(year, month);
                }
                break;

            default:
                userStats = statsService.statsUserByEveryYear();
                break;
        }

        model.addAttribute("userStats", userStats);
        model.addAttribute("selectedType", statType);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedQuarter", quarter);
        model.addAttribute("selectedMonth", month);

        return "admin/stats";
    }
}
