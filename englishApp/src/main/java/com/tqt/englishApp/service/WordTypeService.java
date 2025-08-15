package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.WordType;
import com.tqt.englishApp.repository.WordTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordTypeService {
    @Autowired
    private WordTypeRepository wordTypeRepository;

    public List<WordType> findAll() {
        return wordTypeRepository.findAll();
    }
}
