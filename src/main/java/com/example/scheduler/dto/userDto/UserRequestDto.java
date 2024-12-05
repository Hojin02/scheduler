package com.example.scheduler.dto.userDto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserRequestDto {
    private String id;
    private String password;
    private String name;
    private String email;
}
