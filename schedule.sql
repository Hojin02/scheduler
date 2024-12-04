use schedule;
CREATE TABLE schedule
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    author     VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    contents   TEXT         NOT NULL,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
