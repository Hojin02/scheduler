package com.example.scheduler.repository.scheduleReposittory;

import com.example.scheduler.dto.scheduleDto.ScheduleResponseDto;
import com.example.scheduler.entity.Schedule;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcTemplateScheduleRepository implements ScheduleRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateScheduleRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public ScheduleResponseDto saveSchedule(Schedule schedule) {
        LocalDateTime date = LocalDateTime.now(); // 현재시간.
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("schedule").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("author_id", schedule.getAuthorId());
        parameters.put("contents", schedule.getContents());
        parameters.put("created_at", date);
        parameters.put("updated_at", date);

        Number key = jdbcInsert.executeAndReturnKey(parameters);

        return new ScheduleResponseDto(key.longValue(), schedule.getAuthorId(), "", schedule.getContents(),
                date, date);
    }

    @Override                       // 다건 일정 조회(필터링)
    public List<ScheduleResponseDto> findSchedulesByFilters(String authorId, String date) {
        String sql =
                "SELECT s.id AS id, s.author_id AS author_id, u.name AS author, s.contents AS contents, s.created_at AS created_at, s.updated_at AS updated_at " +
                        "FROM schedule s JOIN user u ON s.author_id = u.id " +
                        "WHERE 1=1 "; // 조건문 AND로 붙이기 위해 1=1

        List<Object> params = new ArrayList<>(); // 조건 파라미터 리스트

        if (authorId != null && !authorId.isEmpty()) {
            sql += "and author_id =? ";
            params.add(authorId);
        }

        if (date != null && !date.isEmpty()) {
            sql += "and date(s.updated_at) =? ";
            params.add(date);
        }

        sql += "order by updated_at desc"; // 최신 수정일 기준으로 정렬.
        return jdbcTemplate.query(sql, params.toArray(), scheduleRowMapper());
    }

    //
    @Override                  // 일정id를 이용하여 단건 조회
    public ScheduleResponseDto findScheduleByIdOrElseThrow(Long id) {
        String sql =
                "SELECT s.id AS id, s.author_id AS author_id, u.name AS author, s.contents AS contents, s.created_at AS created_at, s.updated_at AS updated_at " +
                        "FROM schedule s JOIN user u ON s.author_id = u.id " +
                        "WHERE s.id=?";
        List<ScheduleResponseDto> result=jdbcTemplate.query(sql,scheduleRowMapper(),id);
        return result.stream().findAny().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Does not exitsts id = "+id));
    }

    @Override   // 일정 삭제
    public int deleteSchedule(Long id) {
        String deleteSql = "delete from schedule where id=?";
        return jdbcTemplate.update(deleteSql,id);
    }

    @Override   // 일정 수정
    public int updateContents(Long id,String contents) {
        return jdbcTemplate.update("update schedule set contents=?,updated_at=NOW() where id=?",contents,id);
    }

    private RowMapper<ScheduleResponseDto> scheduleRowMapper() {
        return (rs, rowNum) -> new ScheduleResponseDto(
                rs.getLong("id"),
                rs.getString("author_id"),
                rs.getString("author"),
                rs.getString("contents"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }


}