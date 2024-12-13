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
) engine = InnoDB;

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
) engine = InnoDB;

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
) engine = InnoDB;

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
) engine = InnoDB;

-- 지도 테이블
CREATE TABLE map
(
    id           BIGINT NOT NULL AUTO_INCREMENT,
    address_name VARCHAR(255),
    latitude     DOUBLE,
    longitude    DOUBLE,
    PRIMARY KEY (id)
) engine = InnoDB;

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
) engine = InnoDB;

-- 채팅 메시지 테이블
CREATE TABLE chat_message
(
    id           BIGINT                         NOT NULL AUTO_INCREMENT,
    room_id      BIGINT                         NOT NULL,
    sender_id    BINARY(16)                     NOT NULL,
    message      TEXT                           NOT NULL,
    message_type ENUM ('CHAT', 'JOIN', 'LEAVE') NOT NULL,
    created_at   DATETIME(6)                    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (room_id) REFERENCES gather (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE
) engine = InnoDB;

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
) engine = InnoDB;

CREATE TABLE likes
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    gather_id  BIGINT,
    member_id  BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (gather_id) REFERENCES gather (id)
) engine = InnoDB;

-- 해시태그 테이블
CREATE TABLE hashtag
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    created_at    DATETIME(6),
    updated_at    DATETIME(6),
    deleted_at    DATETIME(6),
    hash_tag_name VARCHAR(255),
    PRIMARY KEY (id)
) engine = InnoDB;

-- 모임-해시태그 테이블
CREATE TABLE gather_hashtag
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    gather_id  BIGINT,
    hashtag_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (gather_id) REFERENCES gather (id),
    FOREIGN KEY (hashtag_id) REFERENCES hashtag (id)
) engine = InnoDB;

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
) engine = InnoDB;

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
) engine = InnoDB;

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
) engine = InnoDB;

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
) engine = InnoDB;

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
) engine = InnoDB;

-- Spring Batch 테이블
CREATE TABLE BATCH_JOB_INSTANCE
(
    JOB_INSTANCE_ID BIGINT       NOT NULL PRIMARY KEY,
    VERSION         BIGINT,
    JOB_NAME        VARCHAR(100) NOT NULL,
    JOB_KEY         VARCHAR(32)  NOT NULL,
    UNIQUE KEY UK_JOB_NAME_KEY (JOB_NAME, JOB_KEY)
) ENGINE = INNODB;

CREATE TABLE BATCH_JOB_EXECUTION
(
    JOB_EXECUTION_ID BIGINT      NOT NULL PRIMARY KEY,
    VERSION          BIGINT,
    JOB_INSTANCE_ID  BIGINT      NOT NULL,
    CREATE_TIME      DATETIME(6) NOT NULL,
    START_TIME       DATETIME(6),
    END_TIME         DATETIME(6),
    STATUS           VARCHAR(10),
    EXIT_CODE        VARCHAR(2500),
    EXIT_MESSAGE     VARCHAR(2500),
    LAST_UPDATED     DATETIME(6),
    FOREIGN KEY (JOB_INSTANCE_ID) REFERENCES BATCH_JOB_INSTANCE (JOB_INSTANCE_ID)
) ENGINE = INNODB;

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS
(
    JOB_EXECUTION_ID BIGINT       NOT NULL,
    PARAMETER_NAME   VARCHAR(100) NOT NULL,
    PARAMETER_TYPE   VARCHAR(100) NOT NULL,
    PARAMETER_VALUE  VARCHAR(2500),
    IDENTIFYING      CHAR(1)      NOT NULL,
    FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
) ENGINE = INNODB;

CREATE TABLE BATCH_STEP_EXECUTION
(
    STEP_EXECUTION_ID  BIGINT       NOT NULL PRIMARY KEY,
    VERSION            BIGINT       NOT NULL,
    STEP_NAME          VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID   BIGINT       NOT NULL,
    CREATE_TIME        DATETIME(6)  NOT NULL,
    START_TIME         DATETIME(6),
    END_TIME           DATETIME(6),
    STATUS             VARCHAR(10),
    COMMIT_COUNT       BIGINT,
    READ_COUNT         BIGINT,
    FILTER_COUNT       BIGINT,
    WRITE_COUNT        BIGINT,
    READ_SKIP_COUNT    BIGINT,
    WRITE_SKIP_COUNT   BIGINT,
    PROCESS_SKIP_COUNT BIGINT,
    ROLLBACK_COUNT     BIGINT,
    EXIT_CODE          VARCHAR(2500),
    EXIT_MESSAGE       VARCHAR(2500),
    LAST_UPDATED       DATETIME(6),
    FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
) ENGINE = INNODB;

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT
(
    STEP_EXECUTION_ID  BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    FOREIGN KEY (STEP_EXECUTION_ID) REFERENCES BATCH_STEP_EXECUTION (STEP_EXECUTION_ID)
) ENGINE = INNODB;

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT
(
    JOB_EXECUTION_ID   BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
) ENGINE = INNODB;

-- 커스텀 테이블
CREATE TABLE BATCH_LOCK
(
    ID        BIGINT       NOT NULL AUTO_INCREMENT,
    JOB_NAME  VARCHAR(255) NOT NULL,
    LOCKED_AT DATETIME(6)  NOT NULL,
    LOCKED_BY VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID),
    UNIQUE KEY UK_JOB_NAME (JOB_NAME)
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_REQUEST
(
    ID           BIGINT       NOT NULL AUTO_INCREMENT,
    JOB_NAME     VARCHAR(255) NOT NULL,
    JOB_PARAM    VARCHAR(255),
    REQUESTED_AT DATETIME(6)  NOT NULL,
    PRIMARY KEY (ID)
) ENGINE = InnoDB;

-- 시퀀스 테이블
CREATE TABLE BATCH_STEP_EXECUTION_SEQ
(
    ID         BIGINT  NOT NULL,
    UNIQUE_KEY CHAR(1) NOT NULL,
    UNIQUE KEY UK_BATCH_STEP_SEQ (UNIQUE_KEY)
) ENGINE = INNODB;

INSERT INTO BATCH_STEP_EXECUTION_SEQ (ID, UNIQUE_KEY)
VALUES (0, '0')
ON DUPLICATE KEY UPDATE ID = ID;

CREATE TABLE BATCH_JOB_EXECUTION_SEQ
(
    ID         BIGINT  NOT NULL,
    UNIQUE_KEY CHAR(1) NOT NULL,
    UNIQUE KEY UK_BATCH_JOB_EXEC_SEQ (UNIQUE_KEY)
) ENGINE = INNODB;

INSERT INTO BATCH_JOB_EXECUTION_SEQ (ID, UNIQUE_KEY)
VALUES (0, '0')
ON DUPLICATE KEY UPDATE ID = ID;

CREATE TABLE BATCH_JOB_SEQ
(
    ID         BIGINT  NOT NULL,
    UNIQUE_KEY CHAR(1) NOT NULL,
    UNIQUE KEY UK_BATCH_JOB_SEQ (UNIQUE_KEY)
) ENGINE = INNODB;

INSERT INTO BATCH_JOB_SEQ (ID, UNIQUE_KEY)
VALUES (0, '0')
ON DUPLICATE KEY UPDATE ID = ID;
