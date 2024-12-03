-- 데이터베이스 초기화
DROP DATABASE IF EXISTS gathering;
CREATE DATABASE gathering;
USE gathering;

-- 사용자 테이블
CREATE TABLE user
(
    id                BINARY(16)                       NOT NULL,
    created_at        DATETIME(6),
    updated_at        DATETIME(6),
    deleted_at        DATETIME(6),
    email             VARCHAR(255)                     NOT NULL UNIQUE,
    identity_provider ENUM ('KAKAO', 'NONE')           NOT NULL,
    nick_name         VARCHAR(255),
    password          VARCHAR(255),
    profile_image     VARCHAR(255),
    provider_id       VARCHAR(255),
    user_role         ENUM ('ROLE_ADMIN', 'ROLE_USER') NOT NULL,
    PRIMARY KEY (id)
);

-- 카테고리 테이블
CREATE TABLE category
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    created_at    DATETIME(6),
    updated_at    DATETIME(6),
    deleted_at    DATETIME(6),
    user_id       BINARY(16),
    category_name VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
);

-- 약관 테이블
CREATE TABLE agreement
(
    id         BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    content    VARCHAR(255),
    type       ENUM ('MARKETING_INFO_RECEIVE_AGREEMENT', 'PRIVACY_POLICY', 'TERMS_OF_SERVICE'),
    version    VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE agreement_history
(
    id           BINARY(16) NOT NULL,
    created_at   DATETIME(6),
    updated_at   DATETIME(6),
    agreement_id BINARY(16),
    content      VARCHAR(255),
    type         ENUM ('MARKETING_INFO_RECEIVE_AGREEMENT', 'PRIVACY_POLICY', 'TERMS_OF_SERVICE'),
    version      VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (agreement_id) REFERENCES agreement (id)
);

-- 지도 테이블
CREATE TABLE map
(
    id           BIGINT NOT NULL AUTO_INCREMENT,
    address_name VARCHAR(255),
    latitude     DOUBLE,
    longitude    DOUBLE,
    gather_id    BIGINT,
    PRIMARY KEY (id)
);

-- 모임 테이블
CREATE TABLE gather
(
    id          BIGINT  NOT NULL AUTO_INCREMENT,
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    deleted_at  DATETIME(6),
    category_id BIGINT  NULL,
    map_id      BIGINT UNIQUE,
    title       VARCHAR(255),
    description VARCHAR(255),
    like_count  INTEGER NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE SET NULL,
    FOREIGN KEY (map_id) REFERENCES map (id)
);

-- 멤버 테이블
CREATE TABLE member
(
    id         BIGINT     NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    deleted_at DATETIME(6),
    gather_id  BIGINT     NOT NULL,
    user_id    BINARY(16) NOT NULL,
    permission ENUM ('GUEST', 'MANAGER', 'PENDING', 'REFUSAL'),
    PRIMARY KEY (id),
    FOREIGN KEY (gather_id) REFERENCES gather (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
);

create table likes
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    gather_id  BIGINT,
    member_id  BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (gather_id) REFERENCES gather (id)
);

-- 해시태그 테이블
CREATE TABLE hashtag
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    created_at    DATETIME(6),
    updated_at    DATETIME(6),
    deleted_at    DATETIME(6),
    gather_id     BIGINT NOT NULL,
    hash_tag_name VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (gather_id) REFERENCES gather (id) ON DELETE CASCADE
);

-- 일정 테이블
CREATE TABLE schedule
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    created_at       DATETIME(6),
    updated_at       DATETIME(6),
    deleted_at       DATETIME(6),
    schedule_title   VARCHAR(255) NOT NULL,
    schedule_content VARCHAR(255) NOT NULL,
    gather_id        BIGINT       NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (gather_id) REFERENCES gather (id) ON DELETE CASCADE
);

-- 게시판 테이블
CREATE TABLE board
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    created_at    DATETIME(6),
    updated_at    DATETIME(6),
    deleted_at    DATETIME(6),
    board_title   VARCHAR(255) NOT NULL,
    board_content VARCHAR(255) NOT NULL,
    gather_id     BIGINT       NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (gather_id) REFERENCES gather (id) ON DELETE CASCADE
);

-- 댓글 테이블
CREATE TABLE comment
(
    id          BIGINT NOT NULL AUTO_INCREMENT,
    nick_name   VARCHAR(255),
    comment     VARCHAR(255),
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    deleted_at  DATETIME(6),
    schedule_id BIGINT NOT NULL,
    member_id   BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (schedule_id) REFERENCES schedule (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

-- 채팅 메시지 테이블
CREATE TABLE chat_message
(
    id        BIGINT NOT NULL AUTO_INCREMENT,
    content   VARCHAR(255),
    gather_id BIGINT,
    sender    VARCHAR(255),
    timestamp DATETIME(6),
    type      TINYINT CHECK (type BETWEEN 0 AND 2),
    PRIMARY KEY (id),
    FOREIGN KEY (gather_id) REFERENCES gather (id) ON DELETE CASCADE
);

-- 사용자-약관 테이블
CREATE TABLE user_agreements
(
    id           BINARY(16) NOT NULL,
    created_at   DATETIME(6),
    updated_at   DATETIME(6),
    agreed_at    DATETIME(6),
    status       ENUM ('AGREED', 'DISAGREED', 'EXPIRED', 'PENDING_REAGREE'),
    agreement_id BINARY(16),
    user_id      BINARY(16),
    PRIMARY KEY (id),
    FOREIGN KEY (agreement_id) REFERENCES agreement (id),
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE user_agreement_history
(
    id           BINARY(16) NOT NULL,
    agreed_at    DATETIME(6),
    status       ENUM ('AGREED', 'DISAGREED', 'EXPIRED', 'PENDING_REAGREE'),
    agreement_id BINARY(16),
    user_id      BINARY(16),
    PRIMARY KEY (id),
    FOREIGN KEY (agreement_id) REFERENCES agreement (id),
    FOREIGN KEY (user_id) REFERENCES user (id)
);

-- Spring Batch 테이블
CREATE TABLE batch_job_instance
(
    job_instance_id BIGINT       NOT NULL PRIMARY KEY,
    version         BIGINT,
    job_name        VARCHAR(100) NOT NULL,
    job_key         VARCHAR(32)  NOT NULL,
    UNIQUE KEY uk_job_name_key (job_name, job_key)
) ENGINE = InnoDB;

CREATE TABLE batch_job_execution
(
    job_execution_id BIGINT      NOT NULL PRIMARY KEY,
    version          BIGINT,
    job_instance_id  BIGINT      NOT NULL,
    create_time      DATETIME(6) NOT NULL,
    start_time       DATETIME(6),
    end_time         DATETIME(6),
    status           VARCHAR(10),
    exit_code        VARCHAR(2500),
    exit_message     VARCHAR(2500),
    last_updated     DATETIME(6),
    FOREIGN KEY (job_instance_id) REFERENCES batch_job_instance (job_instance_id)
) ENGINE = InnoDB;

CREATE TABLE batch_job_execution_params
(
    job_execution_id BIGINT       NOT NULL,
    parameter_name   VARCHAR(100) NOT NULL,
    parameter_type   VARCHAR(100) NOT NULL,
    parameter_value  VARCHAR(2500),
    identifying      CHAR(1)      NOT NULL,
    FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution (job_execution_id)
) ENGINE = InnoDB;

CREATE TABLE batch_step_execution
(
    step_execution_id  BIGINT       NOT NULL PRIMARY KEY,
    version            BIGINT       NOT NULL,
    step_name          VARCHAR(100) NOT NULL,
    job_execution_id   BIGINT       NOT NULL,
    create_time        DATETIME(6)  NOT NULL,
    start_time         DATETIME(6),
    end_time           DATETIME(6),
    status             VARCHAR(10),
    commit_count       BIGINT,
    read_count         BIGINT,
    filter_count       BIGINT,
    write_count        BIGINT,
    read_skip_count    BIGINT,
    write_skip_count   BIGINT,
    process_skip_count BIGINT,
    rollback_count     BIGINT,
    exit_code          VARCHAR(2500),
    exit_message       VARCHAR(2500),
    last_updated       DATETIME(6),
    FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution (job_execution_id)
) ENGINE = InnoDB;

CREATE TABLE batch_step_execution_context
(
    step_execution_id  BIGINT        NOT NULL PRIMARY KEY,
    short_context      VARCHAR(2500) NOT NULL,
    serialized_context TEXT,
    FOREIGN KEY (step_execution_id) REFERENCES batch_step_execution (step_execution_id)
) ENGINE = InnoDB;

CREATE TABLE batch_job_execution_context
(
    job_execution_id   BIGINT        NOT NULL PRIMARY KEY,
    short_context      VARCHAR(2500) NOT NULL,
    serialized_context TEXT,
    FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution (job_execution_id)
) ENGINE = InnoDB;

CREATE TABLE batch_lock
(
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    job_name  VARCHAR(255) NOT NULL,
    locked_at DATETIME(6)  NOT NULL,
    locked_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_job_name (job_name)
) ENGINE = InnoDB;

create table batch_job_request
(
    id           bigint       not null auto_increment,
    job_name     varchar(255) not null,
    job_param    varchar(255),
    requested_at datetime(6)  not null,
    primary key (id)
) engine = InnoDB;

-- 시퀀스 테이블
CREATE TABLE batch_step_execution_seq
(
    id         BIGINT  NOT NULL,
    unique_key CHAR(1) NOT NULL,
    UNIQUE KEY uk_batch_step_seq (unique_key)
) ENGINE = InnoDB;

INSERT INTO batch_step_execution_seq (id, unique_key)
VALUES (0, '0')
ON DUPLICATE KEY UPDATE id = id;

CREATE TABLE batch_job_execution_seq
(
    id         BIGINT  NOT NULL,
    unique_key CHAR(1) NOT NULL,
    UNIQUE KEY uk_batch_job_exec_seq (unique_key)
) ENGINE = InnoDB;

INSERT INTO batch_job_execution_seq (id, unique_key)
VALUES (0, '0')
ON DUPLICATE KEY UPDATE id = id;

CREATE TABLE batch_job_seq
(
    id         BIGINT  NOT NULL,
    unique_key CHAR(1) NOT NULL,
    UNIQUE KEY uk_batch_job_seq (unique_key)
) ENGINE = InnoDB;

INSERT INTO batch_job_seq (id, unique_key)
VALUES (0, '0')
ON DUPLICATE KEY UPDATE id = id;
