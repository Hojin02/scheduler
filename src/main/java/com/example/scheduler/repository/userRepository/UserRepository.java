package com.example.scheduler.repository.userRepository;

import com.example.scheduler.dto.userDto.UserLoignRequestDto;
import com.example.scheduler.dto.userDto.UserRequestDto;
import com.example.scheduler.dto.userDto.UserResponseDto;

import java.util.List;

public interface UserRepository {

    boolean isUserExists(String target,String param);
    UserResponseDto  registerUser(UserRequestDto dto);

    boolean login(UserLoignRequestDto dto);
}
