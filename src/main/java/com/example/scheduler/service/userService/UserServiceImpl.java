package com.example.scheduler.service.userService;

import com.example.scheduler.dto.userDto.UserLoignRequestDto;
import com.example.scheduler.dto.userDto.UserRequestDto;
import com.example.scheduler.dto.userDto.UserResponseDto;
import com.example.scheduler.entity.User;
import com.example.scheduler.repository.userRepository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto registerUser(UserRequestDto dto) {
        if (userRepository.isUserExists("id",dto.getId())) { //이미 데이터가 있을 때
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The ID is already in use.");
        }else if(userRepository.isUserExists("email",dto.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The Email is already in use.");
        }
        return userRepository.registerUser(dto);
    }

    @Override
    public void login(UserLoignRequestDto dto, HttpSession session) {
        if(userRepository.login(dto)){
            session.setAttribute("userId",dto.getUserId());
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username or password");
        }
    }


}
