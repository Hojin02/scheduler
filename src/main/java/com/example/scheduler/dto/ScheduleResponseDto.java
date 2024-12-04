package com.example.scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleResponseDto {
    private Long id; // 고유식별자id
    private String author; // 작성자명
    private String contents; // 할일
    private LocalDateTime updatedAt; // 최종수정일
}

