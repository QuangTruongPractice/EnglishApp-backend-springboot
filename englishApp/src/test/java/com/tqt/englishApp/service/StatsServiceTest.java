package com.tqt.englishApp.service;

import com.tqt.englishApp.repository.StatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @InjectMocks
    private StatsService statsService;

    @Mock
    private StatsRepository statsRepository;

    @Test
    void statsUserByEveryYear_Success() {
        List<Object[]> mockResult = new ArrayList<>();
        when(statsRepository.countUsersEveryYear()).thenReturn(mockResult);

        List<Object[]> result = statsService.statsUserByEveryYear();

        assertNotNull(result);
        verify(statsRepository).countUsersEveryYear();
    }

    @Test
    void statsUserByYear_Success() {
        List<Object[]> mockResult = new ArrayList<>();
        when(statsRepository.countUsersByYear(2023)).thenReturn(mockResult);

        List<Object[]> result = statsService.statsUserByYear(2023);

        assertNotNull(result);
        verify(statsRepository).countUsersByYear(2023);
    }

    @Test
    void statsUserByQuarter_Success() {
        List<Object[]> mockResult = new ArrayList<>();
        when(statsRepository.countUsersByQuarter(2023, 1)).thenReturn(mockResult);

        List<Object[]> result = statsService.statsUserByQuarter(2023, 1);

        assertNotNull(result);
        verify(statsRepository).countUsersByQuarter(2023, 1);
    }

    @Test
    void statsUserByMonth_Success() {
        List<Object[]> mockResult = new ArrayList<>();
        when(statsRepository.countUsersByDayInMonth(2023, 5)).thenReturn(mockResult);

        List<Object[]> result = statsService.statsUserByMonth(2023, 5);

        assertNotNull(result);
        verify(statsRepository).countUsersByDayInMonth(2023, 5);
    }
}
