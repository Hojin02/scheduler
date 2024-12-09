package com.example.scheduler.dto.scheduleDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
public class ScheduleRequestDto {
    private String authorId;
    private String contents; // 할일
    private String password;


    public ScheduleRequestDto(String contents) {
        this.contents = contents;
    }



}
