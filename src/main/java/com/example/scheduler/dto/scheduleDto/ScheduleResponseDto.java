package com.example.scheduler.dto.scheduleDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleResponseDto {
    private Long id; // 고유식별자id
    private String authorId; // 작성자아이디
    @Setter
    private String author; // 작성자명
    private String contents; // 할일
    private LocalDateTime createdAt; // 최종수정일
    private LocalDateTime updatedAt; // 최종수정일
}

