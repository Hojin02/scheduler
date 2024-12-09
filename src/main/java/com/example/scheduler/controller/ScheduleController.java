package com.example.scheduler.controller;

import com.example.scheduler.dto.scheduleDto.ScheduleRequestDto;
import com.example.scheduler.dto.scheduleDto.ScheduleResponseDto;
import com.example.scheduler.dto.userDto.UserRequestDto;
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

    @PostMapping // 새 일정 추가
    public ResponseEntity<ScheduleResponseDto> createSchedule(@RequestBody ScheduleRequestDto dto) {//RequestBody에는 contents 파라미터 1개
        return new ResponseEntity<>(scheduleService.saveSchedule(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> findSchedulesByFilters(
            @RequestParam(required = false) String authorId, // Optional 파라미터
            @RequestParam(required = false) String updated_at,   // Optional 파라미터
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int pageSize

    ) {
        return new ResponseEntity<>(scheduleService.findSchedulesByFilters(authorId, updated_at,page-1,pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")            // 일정 단건 조회
    public ResponseEntity<ScheduleResponseDto> findScheduleById(@PathVariable Long id) {
        return new ResponseEntity<>(scheduleService.findScheduleById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}") // 일정 삭제
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id, @RequestBody UserRequestDto dto) {
        int resultRow = scheduleService.deleteSchedule(id, dto);
        if (resultRow == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}")                                                    // 작성글 id, 수정내용, 유저password
    public ResponseEntity<ScheduleResponseDto> updateAuthorAndContents(@PathVariable Long id,@RequestBody ScheduleRequestDto dto){
        return new ResponseEntity<>(scheduleService.updateContents(id,dto),HttpStatus.OK);
    }
}
