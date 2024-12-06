package com.example.scheduler.service.scheduleService;

import com.example.scheduler.dto.scheduleDto.ScheduleRequestDto;
import com.example.scheduler.dto.scheduleDto.ScheduleResponseDto;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface ScheduleService {
    // 일정 추가
    ScheduleResponseDto saveSchedule(ScheduleRequestDto dto, HttpSession session);

    List<ScheduleResponseDto> findSchedulesByFilters(String author, String date);

    ScheduleResponseDto findScheduleById(Long id);

    int deleteSchedule(Long id, String password);

    ScheduleResponseDto updateAuthorAndContents(Long id, String password, String author, String contents);
    boolean loginCheck(HttpSession session);
}
