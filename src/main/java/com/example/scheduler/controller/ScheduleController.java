package com.example.scheduler.controller;

import com.example.scheduler.dto.scheduleDto.ScheduleRequestDto;
import com.example.scheduler.dto.scheduleDto.ScheduleResponseDto;
import com.example.scheduler.service.scheduleService.ScheduleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ResponseEntity<ScheduleResponseDto> createSchedule(@RequestBody ScheduleRequestDto dto, HttpSession session) {
        return new ResponseEntity<>(scheduleService.saveSchedule(dto,session), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> findSchedulesByFilters(
            @RequestParam(required = false) String authorId, // Optional 파라미터
            @RequestParam(required = false) String updated_at   // Optional 파라미터
    ) {
        return new ResponseEntity<>(scheduleService.findSchedulesByFilters(authorId, updated_at), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> findScheduleById(@PathVariable Long id) {
        return new ResponseEntity<>(scheduleService.findScheduleById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id, @RequestBody String password) {
        int resultRow = scheduleService.deleteSchedule(id, password);
        if (resultRow == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @PatchMapping("/{id}")
//    public ResponseEntity<ScheduleResponseDto> updateAuthorAndContents(@PathVariable Long id,@RequestBody ScheduleRequestDto dto){
//        return new ResponseEntity<>(scheduleService.updateAuthorAndContents(id,dto.getPassword(),dto.getAuthor(),dto.getContents()),HttpStatus.OK);
//    }
}
