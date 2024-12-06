package com.example.scheduler.service.scheduleService;

import com.example.scheduler.dto.scheduleDto.ScheduleRequestDto;
import com.example.scheduler.dto.scheduleDto.ScheduleResponseDto;
import com.example.scheduler.entity.Schedule;
import com.example.scheduler.repository.scheduleReposittory.ScheduleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto dto, HttpSession session) {
        if (!loginCheck(session)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This service requires login.");
        }
        if (dto.getContents() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The author is required value.");
        }
        Schedule schedule = new Schedule(
                (String) session.getAttribute("userId"),
                dto.getContents()
        );
        String authorName = (String) session.getAttribute("userName");
        ScheduleResponseDto scheduleResponseDto = scheduleRepository.saveSchedule(schedule);
        scheduleResponseDto.setAuthor(authorName);
        return scheduleResponseDto;
    }

    @Override
    public List<ScheduleResponseDto> findSchedulesByFilters(String author, String date) {
        List<ScheduleResponseDto> result = scheduleRepository.findSchedulesByFilters(author, date);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A value that matches the search criteria is " +
                    "empty.");
        }
        return result;
    }

    @Override
    public ScheduleResponseDto findScheduleById(Long id) {
        return scheduleRepository.findScheduleByIdOrElseThrow(id);
    }

    @Override
    public int deleteSchedule(Long id, String password) {
        return scheduleRepository.deleteSchedule(id, password);
    }

    @Override
    public ScheduleResponseDto updateAuthorAndContents(Long id, String password, String author, String contents) {
        if (password == null || contents == null || author == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The author,password,contents are required");
        }
        int updateRow = scheduleRepository.updateTitle(id, password, author, contents, LocalDateTime.now());
        if (updateRow == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The input value is invalid and cannot be " +
                    "modified.");
        }
        return null;
    }

    @Override
    public boolean loginCheck(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user login information.");
    }
}
