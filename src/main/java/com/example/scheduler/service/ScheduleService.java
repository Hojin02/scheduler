package com.example.scheduler.service;

import com.example.scheduler.dto.ScheduleRequestDto;
import com.example.scheduler.dto.ScheduleResponseDto;

public interface ScheduleService {
    // 일정 추가
    ScheduleResponseDto saveSchedule(ScheduleRequestDto dto);

}
