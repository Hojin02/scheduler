package com.example.scheduler.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Schedule {
    private Long id; // 고유식별자id
    private String author; // 작성자명
    private String password; // 비밀번호
    private String contents; // 할일
    private LocalDateTime updatedAt; // 최종수정일

    public Schedule(String author, String password, String contents) {
        this.author = author;
        this.password = password;
        this.contents = contents;
    }
}
