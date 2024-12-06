package com.example.scheduler.repository.scheduleReposittory;

import com.example.scheduler.dto.scheduleDto.ScheduleResponseDto;
import com.example.scheduler.entity.Schedule;
import com.example.scheduler.repository.userRepository.JdbcTemplateUserRepository;
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
        LocalDateTime date = LocalDateTime.now();
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

    @Override
    public List<ScheduleResponseDto> findSchedulesByFilters(String author, String date) {
//        String sql = "select * from schedule where 1=1 ";
//
//        List<Object> params = new ArrayList<>();
//
//        if (author != null && !author.isEmpty()) {
//            sql += "and author =? ";
//            params.add(author);
//        }
//
//        if (date != null && !date.isEmpty()) {
//            sql += "and date(updated_at) =? ";
//            params.add(date);
//        }
//
//        sql += "order by updated_at desc";
//        return jdbcTemplate.query(sql, params.toArray(), scheduleRowMapper());
        return null;
    }

    //
    @Override
    public ScheduleResponseDto findScheduleByIdOrElseThrow(Long id) {
//        List<ScheduleResponseDto> result=jdbcTemplate.query("select * from schedule where id = ?",scheduleRowMapper
//        (),id);
//        return result.stream().findAny().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Does not
//        exitsts id = "+id));
        return null;
    }

    @Override
    public int deleteSchedule(Long id, String password) {
//        System.out.println(id);
//        System.out.println(password);
//        return jdbcTemplate.update("delete from schedule where id=? and password=?", id,password);
        return 0;
    }

    @Override
    public int updateTitle(Long id, String password, String author, String contents, LocalDateTime date) {
//        return jdbcTemplate.update("update schedule set author=?, contents=?,updated_at=? where id=? and
//       password=? ",author,contents,date,id,password);
        return 0;
    }

    private RowMapper<ScheduleResponseDto> scheduleRowMapper() {
//        return (rs, rowNum) -> new ScheduleResponseDto(
//                rs.getLong("id"),
//                rs.getString("author"),
//                rs.getString("contents"),
//                rs.getTimestamp("created_at").toLocalDateTime(),
//                rs.getTimestamp("updated_at").toLocalDateTime()
//        );
        return null;
}


}