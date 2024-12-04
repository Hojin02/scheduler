package com.example.scheduler.service;

import com.example.scheduler.dto.ScheduleRequestDto;
import com.example.scheduler.dto.ScheduleResponseDto;

import java.util.Date;
import java.util.List;

public interface ScheduleService {
    // 일정 추가
    ScheduleResponseDto saveSchedule(ScheduleRequestDto dto);

    List<ScheduleResponseDto> findSchedulesByFilters(String author, String date);

    ScheduleResponseDto findScheduleById(Long id);

    int deleteSchedule(Long id, String password);
}
