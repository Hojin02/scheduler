package com.example.scheduler.service.scheduleService;

import com.example.scheduler.dto.scheduleDto.ScheduleRequestDto;
import com.example.scheduler.dto.scheduleDto.ScheduleResponseDto;
import com.example.scheduler.dto.userDto.UserLoignRequestDto;
import com.example.scheduler.dto.userDto.UserRequestDto;
import com.example.scheduler.entity.Schedule;
import com.example.scheduler.repository.scheduleReposittory.ScheduleRepository;
import com.example.scheduler.repository.userRepository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final HttpSession session;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, UserRepository userRepository,
                               HttpSession session) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.session = session;
    }

    @Override
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto dto) {
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
    public List<ScheduleResponseDto> findSchedulesByFilters(String authorId, String date, int page, int size) {
        List<ScheduleResponseDto> result = scheduleRepository.findSchedulesByFilters(authorId, date);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A value that matches the search criteria is " +
                    "empty.");
        }
        int total = result.size();
        int start = page * size;
        if(start>=total){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The range you entered is greater than the range of the data.");
        }
        int end = Math.min(start + size, total);
        List<ScheduleResponseDto> pagingList = result.subList(start,end);
        return pagingList;
    }

    @Override
    public ScheduleResponseDto findScheduleById(Long id) {
        return scheduleRepository.findScheduleByIdOrElseThrow(id);
    }

    @Override
    public int deleteSchedule(Long id, UserRequestDto dto) {
        if (!loginCheck(session)) {// 로그인 한 상태여야함.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This service requires login.");
        }
        dto.setId((String) session.getAttribute("userId"));
        // 로그인한 사람과 작성글의 아이디가 같아야함.
        String scheduleAuthorId = scheduleRepository.findScheduleByIdOrElseThrow(id).getAuthorId();
        String loginedUserId = dto.getId();
        if (!scheduleAuthorId.equals(loginedUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only delete your posts");
        }
        // 로그인한 사람의 비밀번호와 입력한 비밀번호가 같아야함
        UserLoignRequestDto userLoignRequestDto = new UserLoignRequestDto(loginedUserId, dto.getPassword());
        if (!userRepository.login(userLoignRequestDto)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input. Please check again.");
        }
        return scheduleRepository.deleteSchedule(id);
    }

    @Override
    public ScheduleResponseDto updateContents(Long id, ScheduleRequestDto dto) {
        if (!loginCheck(session)) {// 로그인 한 상태여야함.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This service requires login.");
        }
        dto.setAuthorId((String) session.getAttribute("userId"));
        String scheduleAuthorId = scheduleRepository.findScheduleByIdOrElseThrow(id).getAuthorId();
        String loginedUserId = dto.getAuthorId();
        if (!scheduleAuthorId.equals(loginedUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only delete your posts");
        }
        // 로그인한 사람의 비밀번호와 입력한 비밀번호가 같아야함
        UserLoignRequestDto userLoignRequestDto = new UserLoignRequestDto(loginedUserId, dto.getPassword());
        if (!userRepository.login(userLoignRequestDto)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input. Please check again.");
        }
        int updateRow = scheduleRepository.updateContents(id,dto.getContents());
        if (updateRow == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The input value is invalid and cannot be " +
                    "modified.");
        }
        ScheduleResponseDto responseDto = scheduleRepository.findScheduleByIdOrElseThrow(id);
        return responseDto;
    }

    @Override
    public boolean loginCheck(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user login information.");
    }
}
