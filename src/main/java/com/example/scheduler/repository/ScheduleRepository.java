package com.example.scheduler.repository;

import com.example.scheduler.dto.ScheduleResponseDto;
import com.example.scheduler.entity.Schedule;


public interface ScheduleRepository {
    ScheduleResponseDto saveSchedule(Schedule schedule);
}
