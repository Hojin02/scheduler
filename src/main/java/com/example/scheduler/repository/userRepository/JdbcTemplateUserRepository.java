package com.example.scheduler.repository.userRepository;

import com.example.scheduler.dto.userDto.UserLoignRequestDto;
import com.example.scheduler.dto.userDto.UserRequestDto;
import com.example.scheduler.dto.userDto.UserResponseDto;
import com.example.scheduler.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcTemplateUserRepository implements UserRepository{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateUserRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override       // 해당 데이터가(아이디,이메일 등) 있는지 확인.
    public boolean isUserExists(String target,String param) {
        String sql = "select 1 from user where "+target+" = ? limit 1";

        try {
            jdbcTemplate.queryForObject(sql, Integer.class, param);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override   // 유저 회원가입
    public UserResponseDto registerUser(UserRequestDto dto) {
       int resultRow = jdbcTemplate.update("insert into user (id,password,name,email) values (?,?,?,?)",
                dto.getId(),dto.getPassword(),dto.getName(),dto.getEmail());
       if(resultRow==1){
           LocalDateTime dateTime = LocalDateTime.now();
           UserResponseDto resDto = new UserResponseDto(dto.getId(),dto.getName(),dto.getEmail(),dateTime,dateTime);
           return resDto;
       }
       return null;
    }

    @Override   // 아이디, 비밀번호 DB에 있는지 검사.(로그인)
    public boolean login(UserLoignRequestDto dto) {
        String sql = "select 1 from user where id = ? and password =? limit 1";
        try {
            jdbcTemplate.queryForObject(sql,Integer.class,dto.getUserId(),dto.getUserPassword());
            return true;
        }catch (EmptyResultDataAccessException e){
            return false;
        }
    }

    @Override   // 유저의 아이디로 해당 유저의 이름 가져옴.
    public String getUserName(String userId) {
       return jdbcTemplate.queryForObject("select name from user where id = ? ",new Object[]{userId},String.class);
    }


}
