-- 데이터베이스 초기화
DROP DATABASE IF EXISTS gathering;
CREATE DATABASE gathering;
USE gathering;

-- 사용자 정의 테이블

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

CREATE TABLE map
(
    id           BIGINT NOT NULL AUTO_INCREMENT,
    address_name VARCHAR(255),
    latitude     DOUBLE,
    longitude    DOUBLE,
    PRIMARY KEY (id)
);

CREATE TABLE gather
(
    id          BIGINT NOT NULL AUTO_INCREMENT,
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    deleted_at  DATETIME(6),
    category_id BIGINT NULL,
    map_id      BIGINT UNIQUE,
    title       VARCHAR(255),
    description VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE SET NULL,
    FOREIGN KEY (map_id) REFERENCES map (id)
);

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

CREATE TABLE member
(
    id         BIGINT     NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    deleted_at DATETIME(6),
    gather_id  BIGINT     NOT NULL,
    user_id    BINARY(16) NOT NULL,
    permission ENUM ('guest', 'manager', 'pending', 'refusal'),
    PRIMARY KEY (id),
    FOREIGN KEY (gather_id) REFERENCES gather (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
);

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

CREATE TABLE chat_message
(
    id           BIGINT NOT NULL AUTO_INCREMENT,
    content      VARCHAR(255),
    gathering_id BIGINT,
    sender       VARCHAR(255),
    timestamp    DATETIME(6),
    type         TINYINT CHECK (type BETWEEN 0 AND 2),
    PRIMARY KEY (id),
    FOREIGN KEY (gathering_id) REFERENCES gather (id) ON DELETE CASCADE
) ENGINE = InnoDB;

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

-- Spring Batch 메타 테이블

CREATE TABLE BATCH_JOB_INSTANCE
(
    JOB_INSTANCE_ID BIGINT       NOT NULL PRIMARY KEY,
    VERSION         BIGINT,
    JOB_NAME        VARCHAR(100) NOT NULL,
    JOB_KEY         VARCHAR(32)  NOT NULL,
    CONSTRAINT JOB_INST_UN UNIQUE (JOB_NAME, JOB_KEY)
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION
(
    JOB_EXECUTION_ID BIGINT      NOT NULL PRIMARY KEY,
    VERSION          BIGINT,
    JOB_INSTANCE_ID  BIGINT      NOT NULL,
    CREATE_TIME      DATETIME(6) NOT NULL,
    START_TIME       DATETIME(6) DEFAULT NULL,
    END_TIME         DATETIME(6) DEFAULT NULL,
    STATUS           VARCHAR(10),
    EXIT_CODE        VARCHAR(2500),
    EXIT_MESSAGE     VARCHAR(2500),
    LAST_UPDATED     DATETIME(6),
    CONSTRAINT JOB_INST_EXEC_FK FOREIGN KEY (JOB_INSTANCE_ID)
        REFERENCES BATCH_JOB_INSTANCE (JOB_INSTANCE_ID)
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS
(
    JOB_EXECUTION_ID BIGINT       NOT NULL,
    PARAMETER_NAME   VARCHAR(100) NOT NULL,
    PARAMETER_TYPE   VARCHAR(100) NOT NULL,
    PARAMETER_VALUE  VARCHAR(2500),
    IDENTIFYING      CHAR(1)      NOT NULL,
    CONSTRAINT JOB_EXEC_PARAMS_FK FOREIGN KEY (JOB_EXECUTION_ID)
        REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
) ENGINE = InnoDB;

CREATE TABLE BATCH_STEP_EXECUTION
(
    STEP_EXECUTION_ID  BIGINT       NOT NULL PRIMARY KEY,
    VERSION            BIGINT       NOT NULL,
    STEP_NAME          VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID   BIGINT       NOT NULL,
    CREATE_TIME        DATETIME(6)  NOT NULL,
    START_TIME         DATETIME(6) DEFAULT NULL,
    END_TIME           DATETIME(6) DEFAULT NULL,
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
    CONSTRAINT JOB_EXEC_STEP_FK FOREIGN KEY (JOB_EXECUTION_ID)
        REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
) ENGINE = InnoDB;

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT
(
    STEP_EXECUTION_ID  BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    CONSTRAINT STEP_EXEC_CTX_FK FOREIGN KEY (STEP_EXECUTION_ID)
        REFERENCES BATCH_STEP_EXECUTION (STEP_EXECUTION_ID)
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT
(
    JOB_EXECUTION_ID   BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    CONSTRAINT JOB_EXEC_CTX_FK FOREIGN KEY (JOB_EXECUTION_ID)
        REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
) ENGINE = InnoDB;

CREATE TABLE BATCH_STEP_EXECUTION_SEQ
(
    ID         BIGINT  NOT NULL,
    UNIQUE_KEY CHAR(1) NOT NULL,
    CONSTRAINT UNIQUE_KEY_UN UNIQUE (UNIQUE_KEY)
) ENGINE = InnoDB;

INSERT INTO BATCH_STEP_EXECUTION_SEQ (ID, UNIQUE_KEY)
SELECT *
FROM (SELECT 0 AS ID, '0' AS UNIQUE_KEY) AS TMP
WHERE NOT EXISTS (SELECT * FROM BATCH_STEP_EXECUTION_SEQ);

CREATE TABLE BATCH_JOB_EXECUTION_SEQ
(
    ID         BIGINT  NOT NULL,
    UNIQUE_KEY CHAR(1) NOT NULL,
    CONSTRAINT UNIQUE_KEY_UN UNIQUE (UNIQUE_KEY)
) ENGINE = InnoDB;

INSERT INTO BATCH_JOB_EXECUTION_SEQ (ID, UNIQUE_KEY)
SELECT *
FROM (SELECT 0 AS ID, '0' AS UNIQUE_KEY) AS TMP
WHERE NOT EXISTS (SELECT * FROM BATCH_JOB_EXECUTION_SEQ);

CREATE TABLE BATCH_JOB_SEQ
(
    ID         BIGINT  NOT NULL,
    UNIQUE_KEY CHAR(1) NOT NULL,
    CONSTRAINT UNIQUE_KEY_UN UNIQUE (UNIQUE_KEY)
) ENGINE = InnoDB;

INSERT INTO BATCH_JOB_SEQ (ID, UNIQUE_KEY)
SELECT *
FROM (SELECT 0 AS ID, '0' AS UNIQUE_KEY) AS TMP
WHERE NOT EXISTS (SELECT * FROM BATCH_JOB_SEQ);
