DROP DATABASE IF EXISTS gathering;

CREATE database gathering;

USE gathering;

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
    id            BINARY(16) NOT NULL,
    created_at    DATETIME(6),
    updated_at    DATETIME(6),
    deleted_at    DATETIME(6),
    user_id       BINARY(16),
    category_name VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE gather
(
    id          BIGINT NOT NULL AUTO_INCREMENT,
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    deleted_at  DATETIME(6),
    category_id BINARY(16),
    title       VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE hashtag
(
    id            BINARY(16) NOT NULL,
    created_at    DATETIME(6),
    updated_at    DATETIME(6),
    deleted_at    DATETIME(6),
    gather_id     BIGINT,
    hash_tag_name VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (gather_id) REFERENCES gather (id)
);

CREATE TABLE member
(
    id         BIGINT     NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    deleted_at DATETIME(6),
    gather_id  BIGINT     NOT NULL,
    user_id    BINARY(16) NOT NULL,
    permission ENUM ('GUEST', 'MANAGER', 'PENDDING', 'REFUSAL'),
    PRIMARY KEY (id),
    FOREIGN KEY (gather_id) REFERENCES gather (id),
    FOREIGN KEY (user_id) REFERENCES user (id)
);
