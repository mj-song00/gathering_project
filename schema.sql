DROP DATABASE IF EXISTS gathering;

CREATE database gathering;

USE gathering;

CREATE TABLE User (
                      id BINARY(16) PRIMARY KEY, -- UUID를 BINARY(16)로 저장하여 고유 식별자로 사용
                      email VARCHAR(255) UNIQUE NOT NULL, -- 사용자 이메일은 고유하며 필수 입력
                      nick_name VARCHAR(100) NOT NULL, -- 사용자의 닉네임. NULL 불가, 중복 허용
                      password VARCHAR(255), -- 비밀번호. 일반 로그인 사용자는 필요하지만, 소셜 로그인 사용자는 NULL이 가능
                      user_role ENUM('ROLE_USER', 'ROLE_ADMIN') NOT NULL, -- 사용자의 권한 지정 (일반 사용자 또는 관리자)
                      is_delete BOOLEAN DEFAULT FALSE, -- 사용자 탈퇴 여부를 나타내는 플래그. 기본값은 FALSE
                      provider_id VARCHAR(255), -- 소셜 로그인 사용자의 경우 소셜 제공자에서 제공한 고유 사용자 ID
                      identity_provider ENUM('KAKAO', 'GOOGLE', 'NONE') NOT NULL, -- 소셜 로그인 제공자 정보. NONE은 일반 로그인 사용자를 의미
                      profile_image VARCHAR(255), -- 사용자 프로필 이미지 URL. NULL일 시 디폴트 이미지
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 계정 생성 날짜
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 계정 정보가 마지막으로 수정된 날짜
                      CONSTRAINT unique_provider_id UNIQUE (provider_id, identity_provider) -- 소셜 로그인 중복 방지를 위한 제약 조건
);

CREATE TABLE RefreshToken (
                              id BINARY(16) PRIMARY KEY, -- UUID로 저장
                              user_id BINARY(16) NOT NULL, -- User 테이블의 외래 키 (UUID)
                              refresh_token VARCHAR(255) NOT NULL, -- Refresh Token 값
                              expiry_date TIMESTAMP NOT NULL -- Refresh Token 만료 일시
);
