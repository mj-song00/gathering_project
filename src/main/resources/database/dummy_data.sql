INSERT INTO agreement (id, content, version, type)
VALUES (UUID_TO_BIN(UUID()), '개인정보 처리방침 내용입니다.', '1.0', 'PRIVACY_POLICY'),
       (UUID_TO_BIN(UUID()), '서비스 이용약관 내용입니다.', '1.0', 'TERMS_OF_SERVICE'),
       (UUID_TO_BIN(UUID()), '마케팅 정보 수신 동의 내용입니다.', '1.0',
        'MARKETING_INFO_RECEIVE_AGREEMENT');