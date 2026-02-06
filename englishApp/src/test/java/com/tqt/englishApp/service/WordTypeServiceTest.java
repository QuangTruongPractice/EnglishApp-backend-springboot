package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.WordType;
import com.tqt.englishApp.repository.WordTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WordTypeServiceTest {

    @InjectMocks
    private WordTypeService wordTypeService;

    @Mock
    private WordTypeRepository wordTypeRepository;

    @Test
    void findAll_Success() {
        List<WordType> expected = new ArrayList<>();
        expected.add(new WordType());

        when(wordTypeRepository.findAll()).thenReturn(expected);

        List<WordType> result = wordTypeService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(wordTypeRepository).findAll();
    }
}
