package com.example.scheduler.dto.scheduleDto;

import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class ScheduleRequestDto {
    private String authorId;
    private String contents; // 할일

    public ScheduleRequestDto(String contents) {
        this.contents = contents;
    }
}
