package com.example.scheduler.service.userService;

import com.example.scheduler.dto.userDto.UserRequestDto;
import com.example.scheduler.dto.userDto.UserResponseDto;
import jakarta.servlet.http.HttpSession;

public interface UserService {
    UserResponseDto registerUser(UserRequestDto dto);


}
