package com.example.scheduler.service.scheduleService;

import com.example.scheduler.dto.scheduleDto.ScheduleRequestDto;
import com.example.scheduler.dto.scheduleDto.ScheduleResponseDto;
import com.example.scheduler.dto.userDto.UserRequestDto;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface ScheduleService {
    // 일정 추가
    ScheduleResponseDto saveSchedule(ScheduleRequestDto dto);

    List<ScheduleResponseDto> findSchedulesByFilters(String author, String date,int page,int size);

    ScheduleResponseDto findScheduleById(Long id);

    int deleteSchedule(Long id, UserRequestDto dto);

    ScheduleResponseDto updateContents(Long id,ScheduleRequestDto dto);

    boolean loginCheck(HttpSession session);
}
