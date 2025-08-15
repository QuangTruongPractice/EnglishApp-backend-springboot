package com.tqt.englishApp.service;

import com.tqt.englishApp.repository.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsService {
    @Autowired
    StatsRepository statsRepository;

    public List<Object[]> statsUserByEveryYear(){
        return statsRepository.countUsersEveryYear();
    }

    public List<Object[]> statsUserByYear(int year){
        return statsRepository.countUsersByYear(year);
    }

    public List<Object[]> statsUserByQuarter(int year, int quarter){
        return  statsRepository.countUsersByQuarter(year,quarter);
    }

    public List<Object[]> statsUserByMonth(int year, int month){
        return statsRepository.countUsersByDayInMonth(year,month);
    }
}
