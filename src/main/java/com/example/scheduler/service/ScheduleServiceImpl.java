package com.example.scheduler.service;

import com.example.scheduler.dto.ScheduleRequestDto;
import com.example.scheduler.dto.ScheduleResponseDto;
import com.example.scheduler.entity.Schedule;
import com.example.scheduler.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ScheduleServiceImpl implements ScheduleService{

    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto dto) {
       if(dto.getAuthor()==null||dto.getContents()==null||dto.getPassword()==null){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The author,password,contents are required values.");
       }
        Schedule schedule = new Schedule(
                dto.getAuthor(),
                dto.getPassword(),
                dto.getContents());
        return scheduleRepository.saveSchedule(schedule);
    }
}
