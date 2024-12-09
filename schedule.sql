use schedule;
drop table user;
drop table schedule;

-- user 테이블 생성
CREATE TABLE user
(
    id            VARCHAR(255) PRIMARY KEY, -- 고유 사용자 ID
    password      VARCHAR(255) NOT NULL,    -- 사용자 비밀번호
    name          VARCHAR(255) NOT NULL,    -- 사용자 이름
    email         VARCHAR(255) UNIQUE NOT NULL, -- 이메일 (고유)
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 계정 생성일
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 계정 수정일
);

-- schedule 테이블 생성
CREATE TABLE schedule
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY, -- 일정 고유 ID
    author_id   VARCHAR(255),                      -- 유저 ID (user 테이블 참조)
    contents    TEXT         NOT NULL,             -- 일정 내용
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 일정 생성일
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 일정 수정일
    CONSTRAINT fk_schedule_user
        FOREIGN KEY (author_id) REFERENCES user(id) ON DELETE CASCADE -- 외래 키 설정
);


# TRUNCATE TABLE schedule; -- 기존 schedule테이블 안에 데이터 모두 삭제
# schedule 테이블 수정
# ALTER TABLE schedule ADD author_id BIGINT; -- 유저 id추가
#
# ALTER TABLE schedule -- 유저id를 외래키로 설정
#     ADD CONSTRAINT fk_schedule_user
#         FOREIGN KEY (author_id) REFERENCES user(id) ON DELETE CASCADE;
#
# ALTER TABLE schedule DROP COLUMN author; -- 작성자 컬럼 삭제
# ALTER TABLE schedule DROP COLUMN password; -- 글작성 비밀번호 삭제
#
# ALTER TABLE schedule
#     ADD created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
------------------------------------------------------------------------------
-- 페이징을 위해 각 테이블에 100개씩 데이터를 삽입
INSERT INTO user (id, password, name, email)
SELECT
    UUID(), -- 랜덤 고유 ID
    CONCAT('pass', FLOOR(RAND() * 10000)), -- 랜덤 비밀번호
    CONCAT('User', FLOOR(RAND() * 1000)),  -- 랜덤 이름
    CONCAT('user', FLOOR(RAND() * 1000), '@example.com') -- 랜덤 이메일
FROM
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) AS tmp
    LIMIT 100;

INSERT INTO schedule (author_id, contents)
SELECT
    (SELECT id FROM user ORDER BY RAND() LIMIT 1), -- user 테이블에서 랜덤한 사용자 ID 가져오기
    CONCAT('Schedule Content ', FLOOR(RAND() * 10000)) -- 랜덤 일정 내용
FROM
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) AS tmp1
    CROSS JOIN
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) AS tmp2
    LIMIT 100;


