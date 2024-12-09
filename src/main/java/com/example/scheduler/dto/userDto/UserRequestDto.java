package com.example.scheduler.dto.userDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserRequestDto {
    @Setter
    private String id;
    private String password;
    private String name;
    private String email;

}
