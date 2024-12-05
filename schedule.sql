use schedule;
CREATE TABLE schedule
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    author     VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    contents   TEXT         NOT NULL,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

----------------------------------------------
TRUNCATE TABLE schedule; -- 기존 schedule테이블 안에 데이터 모두 삭제

CREATE TABLE user -- user 테이블 추가
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY, -- 고유 사용자 ID
    name          VARCHAR(255) NOT NULL,            -- 사용자 이름
    email         VARCHAR(255) UNIQUE NOT NULL,     -- 이메일 (고유)
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 계정 생성일
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 계정 수정일
);
-- schedule 테이블 수정
ALTER TABLE schedule ADD author_id BIGINT; -- 유저 id추가

ALTER TABLE schedule -- 유저id를 외래키로 설정
    ADD CONSTRAINT fk_schedule_user
        FOREIGN KEY (author_id) REFERENCES user(id) ON DELETE CASCADE;

ALTER TABLE schedule DROP COLUMN author; -- 작성자 컬럼 삭제
ALTER TABLE schedule DROP COLUMN password; -- 글작성 비밀번호 삭제

ALTER TABLE user ADD password varchar(255) NOT NULL;
ALTER TABLE schedule
    ADD created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;



