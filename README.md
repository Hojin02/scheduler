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
<p align="center">
  <img src="https://github.com/user-attachments/assets/64954164-d911-4d1e-a58e-61a6eca481b6" width="250" height="450" />
</p>


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

## 2. 로그인
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

## 3. 로그아웃
> 저장된 로그인 세션을 무효화 시킴.
 ---
로그아웃 POST요청
![image](https://github.com/user-attachments/assets/f59710ce-7194-4681-8444-441b8ebf1e80)


### Controller (UserController)
<details>
  <summary>코드 보기</summary>
  
```java
   @GetMapping("/logout") //로그이웃
    public void logout(HttpSession session){
        userService.logout(session);
    }
```

</details>

### Service (UserServiceImpl)
<details>
  <summary>코드 보기</summary>

  ```java
   @Override // 로그아웃
    public void logout(HttpSession session) {
        session.invalidate();
    }
```

</details>

## 4. 새 일정 추가
> 로그인 후 RequestBody에 contents를 넣어 새 일정을 추가함.
 ---
 
일정추가 POST요청
![image](https://github.com/user-attachments/assets/51b92006-bd99-4cb5-b417-ccd892f228e2)
데이터베이스 확인
![image](https://github.com/user-attachments/assets/c3d3f5c9-ed34-4d1e-8219-d7eb6426d872)



### Controller (ScheduleController)
<details>
  <summary>코드 보기</summary>
  
```java
   @PostMapping // 새 일정 추가
    public ResponseEntity<ScheduleResponseDto> createSchedule(@RequestBody ScheduleRequestDto dto) {//RequestBody에는 contents 파라미터 1개
        return new ResponseEntity<>(scheduleService.saveSchedule(dto), HttpStatus.CREATED);
    }
    
```

</details>

### Service (ScheduleServiceImpl)
<details>
  <summary>코드 보기</summary>

  ```java
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
@Override // 로그인 된 사용자인지 확인하는 함수.
    public boolean loginCheck(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user login information.");
    }
```

</details>

### Repository (JdbcTemplateScheduleRepository)
<details>
  <summary>코드 보기</summary>

  ```java
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
```

</details>


## 5. 전체 일정 조회 및 필터링조회(유저id , 날짜)
> 전체 조회 시 파라미터 값은 X(페이지번호, 페이지 크기만 넣음 안넣어두댐) 
> 필터링시 원하는 조건을 넣어 조회
 ---
일정 목록 조회 GET요청 (전체 중 페이지 번호 1, 페이지 일정 수 3)
![image](https://github.com/user-attachments/assets/80162ba8-149d-4ad0-9427-04d3ecd003d6)
유저 아이디로 조회
![image](https://github.com/user-attachments/assets/2149bc92-a6a8-47d3-8b03-f252061de6df)

이외의 날짜로 조회, 날짜 AND 유저ID로 조회 가능

### Controller (ScheduleController)
<details>
  <summary>코드 보기</summary>
  
```java
    @GetMapping  // 일정 목록 조회
    public ResponseEntity<List<ScheduleResponseDto>> findSchedulesByFilters(
            @RequestParam(required = false) String authorId, // Optional 파라미터
            @RequestParam(required = false) String updated_at,   // Optional 파라미터
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int pageSize

    ) {
        return new ResponseEntity<>(scheduleService.findSchedulesByFilters(authorId, updated_at,page-1,pageSize), HttpStatus.OK);
    }
    
```

</details>

### Service (ScheduleServiceImpl)
<details>
  <summary>코드 보기</summary>

  ```java
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
```

</details>

### Repository (JdbcTemplateScheduleRepository)
<details>
  <summary>코드 보기</summary>

  ```java
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
```

</details>

## 6. 단건 일정 조회 (일정id)
> 유저id를 이용하여 해당 일정의 정보를 조회 함.
 ---
일정 단건 조회 GET요청
![image](https://github.com/user-attachments/assets/cc55316c-50a7-4219-8256-a34ff1f8391b)



### Controller (ScheduleController)
<details>
  <summary>코드 보기</summary>
  
```java
    @GetMapping("/{id}")            // 일정 단건 조회
    public ResponseEntity<ScheduleResponseDto> findScheduleById(@PathVariable Long id) {
        return new ResponseEntity<>(scheduleService.findScheduleById(id), HttpStatus.OK);
    }    
```

</details>

### Service (ScheduleServiceImpl)
<details>
  <summary>코드 보기</summary>

  ```java
    @Override               // 스케줄의 고유 id로 스케줄 조회
    public ScheduleResponseDto findScheduleById(Long id) {
        return scheduleRepository.findScheduleByIdOrElseThrow(id);
    }
```

</details>

### Repository (JdbcTemplateScheduleRepository)
<details>
  <summary>코드 보기</summary>

  ```java
 @Override                  // 일정id를 이용하여 단건 조회
    public ScheduleResponseDto findScheduleByIdOrElseThrow(Long id) {
        String sql =
                "SELECT s.id AS id, s.author_id AS author_id, u.name AS author, s.contents AS contents, s.created_at AS created_at, s.updated_at AS updated_at " +
                        "FROM schedule s JOIN user u ON s.author_id = u.id " +
                        "WHERE s.id=?";
        List<ScheduleResponseDto> result=jdbcTemplate.query(sql,scheduleRowMapper(),id);
        return result.stream().findAny().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Does not exitsts id = "+id));
    }
```

</details>

## 7. 일정 내용 수정 
> 로그인된 유저가 본인의 일정 중 일정id를 이용하여 일정 내용 수정
> 이때 비밀번호를 입력하여 본인확인 진행.
 ---
일정 수정 PATCH요청
![image](https://github.com/user-attachments/assets/26130be7-99b1-40c0-afa5-1b08190c100d)


### Controller (ScheduleController)
<details>
  <summary>코드 보기</summary>
  
```java
    @PatchMapping("/{id}")                                                    // 작성글 id, 수정내용, 유저password
    public ResponseEntity<ScheduleResponseDto> updateAuthorAndContents(@PathVariable Long id,@RequestBody ScheduleRequestDto dto){
        return new ResponseEntity<>(scheduleService.updateContents(id,dto),HttpStatus.OK);
    }  
```

</details>

### Service (ScheduleServiceImpl)
<details>
  <summary>코드 보기</summary>

  ```java
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
```

</details>

### Repository (JdbcTemplateScheduleRepository)
<details>
  <summary>코드 보기</summary>

  ```java
 @Override   // 일정 수정
    public int updateContents(Long id,String contents) {
        return jdbcTemplate.update("update schedule set contents=?,updated_at=NOW() where id=?",contents,id);
    }
```

</details>

## 8. 일정 삭제 
> 로그인된 유저가 본인의 일정 중 일정id를 이용하여 일정 삭제
> 이때 비밀번호를 입력하여 본인확인 진행.
 ---
일정 삭제 DELETE요청
![image](https://github.com/user-attachments/assets/d8dcc827-1d0e-40d1-af7c-e4ec4654be4e)

### Controller (ScheduleController)
<details>
  <summary>코드 보기</summary>
  
```java
    @DeleteMapping("/{id}") // 일정 삭제
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id, @RequestBody UserRequestDto dto) {
        int resultRow = scheduleService.deleteSchedule(id, dto);
        if (resultRow == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
```

</details>

### Service (ScheduleServiceImpl)
<details>
  <summary>코드 보기</summary>

  ```java
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
 @Override // 로그인 된 사용자인지 확인하는 함수.
    public boolean loginCheck(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user login information.");
    }
```

</details>

### Repository (JdbcTemplateScheduleRepository)
<details>
  <summary>코드 보기</summary>

  ```java
  @Override   // 일정 삭제
    public int deleteSchedule(Long id) {
        String deleteSql = "delete from schedule where id=?";
        return jdbcTemplate.update(deleteSql,id);
    }
```

</details>


## ⚠️ 트러블 슈팅
#### 가장 많이 겪었던 문제.
> RequestBody로 파라미터를 넘길 때, 여러가지 상황에서 문제가 발생했다.
---
문제1. 
> 컨틀로러 메소드에서 @RequestBody를 여러개 사용하여 값을 가져오려 했는데 에러발생.

 예) 
```java
@PostMapping // 새 일정 추가
    public ResponseEntity<ScheduleResponseDto> createSchedule(@RequestBody userId, @RequestBody contents) {
        return new ResponseEntity<>(scheduleService.saveSchedule(dto), HttpStatus.CREATED);
    }
```

> 위 상황에서 body로 넘어온 파라미터를 하나하나 @RequestBody로 받으려했으나 에러가 발생했다.

> 이유 : @RequestParam은 여러개가 가능하지만 @RequestBody는 1개만 가능!

> 해결 : @RequestBody ScheduleRequestDTO로 받아 해결했다.

> 주의 사항 : DTO필드명과 Body에 json key가 같아야 매핑가능!

문제2.
> @RequestBody하나만 넣었지만 DTO로 받아도 에러가 발생했다.
> 
> 에러 : HttpMessageNotReadableException: JSON parse error: Cannot construct instance of `com.example.scheduler.dto.scheduleDto.ScheduleRequestDto`

> 이유 : DTO객체에 생성자가 여러개였음.

예시.
```java
@Getter
@Setter
@AllArgsConstructor
public class ScheduleRequestDto {
    private String authorId;
    private String contents; // 할일
    private String password;

    public ScheduleRequestDto(String contents, String password) {
        this.contents = contents;
        this.password = password;
    }
}
```

> 위 코드에서는 생성자가 하나 인것 같지만, @AllArgsConstructor로 모든 필드를 받는 생성자 1개
> 내용과 비밀번호를 받는 생성자 1개 총 두개
> 이때 스프링빈이 어떤 생성자를 찾아야하는지 몰라 에러가 발생했다.

> 해결 :  @AllArgsConstructor를 이용하여 모든 필드를 받는 생성자를 사용하고,
> RequestBody에서 필요한 값만 json으로 필드명 맞춰 넣어주었더니 해결되었다.
> 이때, 모든 값을 넣어야 하지 않을까 했지만!
> 필요한 값만 넣어 사용하면 되었다! (넣지않은 값은 null)


> 위와 비슷한 예시로 비슷한 에러가  발생한 적이 있다.
> 그때는 생성자가 아예 없어 생긴 에러였다.. 해결은 위와 같다.


# 😼 후기
작년 학원에서 스프링을 배웠지만 다른 부분이 있었다 바로 DTO객체였다.
학원에서 프로젝트 때에는 DTO가아닌 테이블구조와 같은 도메인엔티티 또는 
각 하나하나 파라미터로 받았었다.
DTO를 이용하면, 테이블 구조가 바뀌어도 DTO를 수정할 필요 없다는점(유지보수)가 있고,
또 엔티티객체 노출을 최소화 하여 요청과 응답에 맞게 필요한 값만 처리 할 수있다는 점이라는걸 알게되었다.

이번에는 깃 커밋은 최대한 많이 하려고 노력했다. 각 기능별로 하려고 노력...했다..아무튼 해씀 ㅠ
킹치만 이번에는 주석에 예를 차리지 못했다 죄송합니다 주석마마..
게으름 + 시간 촉박? 빨리 숙련도 들어가야 할 것 같고 챌린지 과제도 확인해야하는데 불안불안
집중만 제대로 해쓰면 금방 하고 더 많이 했을텐데 반성중이다..
긍데 아팠으니 나자신 조금만 봐주는걸류... 튜텨님 이 글을 보신다면... 핑계..맞습니다 힝구ㅠ

스프링 숙련때는 완저어언 열심히 하게씁니다요!! 아자아자
