package com.example.scheduler.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class User {
    private String id;
    private String password;
    private String name;
    private String email;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
