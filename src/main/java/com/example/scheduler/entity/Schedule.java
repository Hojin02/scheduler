package com.example.scheduler.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Schedule {
    private Long id; // 고유식별자id
    private String authorId; // 작성자아이디
    private String contents; // 할일
    private LocalDateTime createdAt; // 최종수정일
    private LocalDateTime updatedAt; // 최종수정일

    public Schedule(String authorId, String contents) {
        this.authorId = authorId;
        this.contents = contents;
    }
}
