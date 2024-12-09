package com.example.scheduler.dto.userDto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class UserRequestDto {
    @Setter
    private String id;
    private String password;
    private String name;
    private String email;

    public UserRequestDto(String password) {
        this.password = password;
    }
}
