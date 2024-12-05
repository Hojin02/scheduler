package com.example.scheduler.dto.scheduleDto;

import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class ScheduleRequestDto {
    private String author; // 작성자명
    private String password; // 비밀번호
    private String contents; // 할일
    private LocalDateTime updatedAt; // 최종수정일
}
