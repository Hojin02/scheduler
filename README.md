# Spring - JDBCTemplate 프로젝트 Scheduler

## 🗒︎ Index
- 🎞️ 구조
  - API 명세
  - Class Diagram
  - ERD
- ✈️ 기능
- ⚠️ 트러블 슈팅
- 😼 후기
    
## Scheduler API 명세
![image](https://github.com/user-attachments/assets/367705a4-86cf-43aa-aa25-c4bb89f28623)

## Class Diagram
![image](https://github.com/user-attachments/assets/128c5a62-7d23-47a8-b11e-5db6312765c9)
![image](https://github.com/user-attachments/assets/c85e7abe-a9f1-41f8-80ac-0b5a4800799d)

## ERD
![image](https://github.com/user-attachments/assets/64954164-d911-4d1e-a58e-61a6eca481b6)

## DTO
### UserRequestDTO
<details>
  <summary>코드 보기</summary>
  
```java
@Getter
@AllArgsConstructor
public class UserRequestDto {
    @Setter
    private String id;
    private String password;
    private String name;
    private String email;
}
```

</details>

### UserResponseDTO
<details>
  <summary>코드 보기</summary>
  
```java
@Getter
@AllArgsConstructor
public class UserResponseDto {
    private String id;
    private String name;
    private String email;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
```

</details>

### UserLoginRequestDTO
<details>
  <summary>코드 보기</summary>
  
```java
@Getter
@AllArgsConstructor
public class UserLoignRequestDto {
    String userId;
    String userPassword;
}
```

</details>

### ScheduleRequestDTO
<details>
  <summary>코드 보기</summary>
  
```java
@Getter
@Setter
@AllArgsConstructor
public class ScheduleRequestDto {
    private String authorId;
    private String contents; // 할일
    private String password;
}
```

</details>

### UserRequestDTO
<details>
  <summary>코드 보기</summary>
  
```java
@Getter
@AllArgsConstructor
public class UserRequestDto {
    @Setter
    private String id;
    private String password;
    private String name;
    private String email;

}
```

</details>

### ScheduleResponseDTO
<details>
  <summary>코드 보기</summary>
  
```java
@Getter
@AllArgsConstructor
public class UserRequestDto {
    @Getter
    @AllArgsConstructor
    public class ScheduleResponseDto {
      private Long id; // 고유식별자id
      private String authorId; // 작성자아이디
      @Setter
      private String author; // 작성자명
      private String contents; // 할일
      private LocalDateTime createdAt; // 최종수정일
      private LocalDateTime updatedAt; // 최종수정일
}
```

</details>

## 1. 회원가입
> 일정 추가, 수정, 삭제 기능은 회원 전용 서비스 이기 때문에 회원가입이 필요함.
 ---
회원가입 GET요청
![image](https://github.com/user-attachments/assets/4d202944-2ac4-465c-a866-4b31f11d3929)
데이터베이스 확인
![image](https://github.com/user-attachments/assets/5307127c-9574-4580-bb6d-afb2b7cef33a)
### Controller (UserController)
<details>
  <summary>코드 보기</summary>
  
```java
  @PostMapping    // 회원가입
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto dto) {
        return new ResponseEntity<>(userService.registerUser(dto), HttpStatus.CREATED);
    }
```

</details>

### Service (UserServiceImpl)
<details>
  <summary>코드 보기</summary>

  ```java
@Override // 회원가입
    public UserResponseDto registerUser(UserRequestDto dto) {
        // 아이디는 기본키, 이메일은 유니크 키
        if (userRepository.isUserExists("id",dto.getId())) { //이미 데이터가 있을 때
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The ID is already in use.");
        }else if(userRepository.isUserExists("email",dto.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The Email is already in use.");
        }
        return userRepository.registerUser(dto);
    }
```

</details>

### Repository (JdbcTemplateUserRepository)
<details>
  <summary>코드 보기</summary>

  ```java
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
```

</details>

## 1. 로그인
> 회원가입 후 로그인하여 로그인 정보(아이디,유저이름)를 세션에 저장. 일정추가,수정,삭제 기능 사용가능함.
 ---
로그인 POST요청
![image](https://github.com/user-attachments/assets/4467d6c2-715c-4c8a-9032-df8c59e16ed5)


### Controller (UserController)
<details>
  <summary>코드 보기</summary>
  
```java
@PostMapping("/login")  // 로그인
    public ResponseEntity<Void> login(@RequestBody UserLoignRequestDto dto, HttpSession session) {
        userService.login(dto,session);
        return new ResponseEntity<>(HttpStatus.OK);
    }
```

</details>

### Service (UserServiceImpl)
<details>
  <summary>코드 보기</summary>

  ```java
   @Override //로그인
    public void login(UserLoignRequestDto dto, HttpSession session) {
        if(userRepository.login(dto)){
            session.setAttribute("userId",dto.getUserId());
            session.setAttribute("userName",getUserName(dto.getUserId()));
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username or password");
        }
    }
```

</details>

### Repository (JdbcTemplateUserRepository)
<details>
  <summary>코드 보기</summary>

  ```java
 @Override   // 아이디, 비밀번호 DB와 일치하는지 검사.(로그인)
    public boolean login(UserLoignRequestDto dto) {
        String sql = "select 1 from user where id = ? and password =? limit 1";
        try {
            jdbcTemplate.queryForObject(sql,Integer.class,dto.getUserId(),dto.getUserPassword());
            return true;
        }catch (EmptyResultDataAccessException e){
            return false;
        }
    }
```

</details>

