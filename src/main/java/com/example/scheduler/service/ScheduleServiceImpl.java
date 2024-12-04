package com.example.scheduler.service;

import com.example.scheduler.dto.ScheduleRequestDto;
import com.example.scheduler.dto.ScheduleResponseDto;
import com.example.scheduler.entity.Schedule;
import com.example.scheduler.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

    @Override
    public List<ScheduleResponseDto> findSchedulesByFilters(String author,String date) {
        List<ScheduleResponseDto> result = scheduleRepository.findSchedulesByFilters(author,date);
        if(result.isEmpty()){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND,"A value that matches the search criteria is empty.");
        }
        return result;
    }

    @Override
    public ScheduleResponseDto findScheduleById(Long id) {
        return scheduleRepository.findScheduleByIdOrElseThrow(id);
    }

    @Override
    public int deleteSchedule(Long id, String password) {
        return scheduleRepository.deleteSchedule(id,password);
    }
}
