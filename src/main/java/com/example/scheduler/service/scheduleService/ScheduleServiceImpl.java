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

    @Override               // 새 일정 추가
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto dto) {
        if (!loginCheck(session)) { // 로그인 한 사용자만 이용가능한 서비스. 로그인 체크
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This service requires login.");
        }
        if (dto.getContents() == null) { // 필수 입력값(파라미터) : contents
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The author is required value.");
        }
        Schedule schedule = new Schedule( // 로그인한 유저의 아이디와, 입력값(contents) 를 넘기기 위해 객체 생성
                (String) session.getAttribute("userId"),
                dto.getContents()
        );
        String authorName = (String) session.getAttribute("userName"); // 유저의 이름도 함께 Response하기 위해 세션에서 이름 가져옴.
        ScheduleResponseDto scheduleResponseDto = scheduleRepository.saveSchedule(schedule);
        scheduleResponseDto.setAuthor(authorName);
        return scheduleResponseDto;
    }

    @Override                       // 날짜와 작성자 아이디로 필터링 하여 조회, 페이징 구현
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
        // 전체 스케줄 중 페이징하여 잘라냄.
        List<ScheduleResponseDto> pagingList = result.subList(start,end);
        return pagingList;
    }

    @Override               // 스케줄의 고유 id로 스케줄 조회
    public ScheduleResponseDto findScheduleById(Long id) {
        return scheduleRepository.findScheduleByIdOrElseThrow(id);
    }

    @Override // 스케줄의 고유id로 스케줄 삭제
    public int deleteSchedule(Long id, UserRequestDto dto) {
        if (!loginCheck(session)) {// 로그인 한 상태여야함.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This service requires login.");
        }
        dto.setId((String) session.getAttribute("userId"));
        // 로그인한 사람과 작성글의 아이디가 같아야함.
        // 본인의 게시글만 삭제가능
        String scheduleAuthorId = scheduleRepository.findScheduleByIdOrElseThrow(id).getAuthorId();
        String loginedUserId = dto.getId();
        if (!scheduleAuthorId.equals(loginedUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only delete your posts");
        }
        // 로그인한 사람의 비밀번호와 입력한 비밀번호가 같아야함(비밀번호 이중체크)
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
        // 수정 후 해당 일정을 가져와 리턴.
        ScheduleResponseDto responseDto = scheduleRepository.findScheduleByIdOrElseThrow(id);
        return responseDto;
    }

    @Override // 로그인 된 사용자인지 확인하는 함수.
    public boolean loginCheck(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user login information.");
    }
}
