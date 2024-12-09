package com.example.scheduler.repository.scheduleReposittory;

import com.example.scheduler.dto.scheduleDto.ScheduleRequestDto;
import com.example.scheduler.dto.scheduleDto.ScheduleResponseDto;
import com.example.scheduler.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;


public interface ScheduleRepository {
    ScheduleResponseDto saveSchedule(Schedule schedule);

    List<ScheduleResponseDto> findSchedulesByFilters(String author,String date);

    ScheduleResponseDto findScheduleByIdOrElseThrow(Long id);

    int deleteSchedule(Long id);

    int updateContents(Long id,String contents);
}
