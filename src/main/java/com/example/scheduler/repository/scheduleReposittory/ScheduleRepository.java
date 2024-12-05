package com.example.scheduler.repository.scheduleReposittory;

import com.example.scheduler.dto.scheduleDto.ScheduleResponseDto;
import com.example.scheduler.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;


public interface ScheduleRepository {
    ScheduleResponseDto saveSchedule(Schedule schedule);

    List<ScheduleResponseDto> findSchedulesByFilters(String author,String date);

    ScheduleResponseDto findScheduleByIdOrElseThrow(Long id);

    int deleteSchedule(Long id, String password);

    int updateTitle(Long id, String password, String author, String contents, LocalDateTime date);
}
