INSERT INTO agreement (id, content, version, type)
VALUES (0x7CFAA172AB2111EFBBD800155DC6E466, '개인정보 처리방침 내용입니다.', '1.0', 'PRIVACY_POLICY'),
       (0x7CFA39D8AB2111EFBBD800155DC6E467, '서비스 이용약관 내용입니다.', '1.0', 'TERMS_OF_SERVICE'),
       (0x7CFA9E06AB2111EFBBD800155DC6E468, '마케팅 정보 수신 동의 내용입니다.', '1.0',
        'MARKETING_INFO_RECEIVE_AGREEMENT');

INSERT INTO user (id, created_at, updated_at, deleted_at, email, identity_provider,
                  nick_name, password, profile_image, provider_id, user_role)
VALUES (0x1494D6C779D34086A551E2C06FD97803, '2024-12-02 15:07:32.338585',
        '2024-12-03 18:26:47.329096', null, 'skrtl365@gmail.com', 'NONE', '이정현',
        '$2a$10$vujZoivZW30hPkFg8ift9.zUOa38mzkJh758ExpVvXHDUv5gMkhXO',
        'https:https://gathering-21.s3.ap-northeast-2.amazonaws.com/IMG_5127.JPG', null,
        'ROLE_USER'),
       (0xF60B81ACB2234340BE8B28621BF14BE7, '2024-11-25 20:39:17.952910',
        '2024-12-03 18:26:48.924095', null, 'nbcteam21test@gmail.com', 'NONE', '홍길동',
        '$2a$10$vujZoivZW30hPkFg8ift9.zUOa38mzkJh758ExpVvXHDUv5gMkhXO',
        'https:https://gathering-21.s3.ap-northeast-2.amazonaws.com/IMG_5127.JPG', null,
        'ROLE_ADMIN');

INSERT INTO user_agreements (id, created_at, updated_at, agreed_at, status, agreement_id, user_id)
VALUES (0x3866D935ACE84E9E874FCBE3D3A04702, '2024-12-02 15:07:32.349586', null,
        '2024-12-02 15:07:32.308589', 'AGREED', 0x7CFAA172AB2111EFBBD800155DC6E466,
        0x1494D6C779D34086A551E2C06FD97803),
       (0x99E5A1A000A94420A7331CFB5DFC4D30, '2024-12-02 15:07:32.348586', null,
        '2024-12-02 15:07:32.308589', 'AGREED', 0x7CFA39D8AB2111EFBBD800155DC6E467,
        0x1494D6C779D34086A551E2C06FD97803),
       (0xC4EB9CDBAB6E4B68A761523E24427DB7, '2024-12-02 15:07:32.349586', null,
        '2024-12-02 15:07:32.308589', 'AGREED', 0x7CFA9E06AB2111EFBBD800155DC6E468,
        0x1494D6C779D34086A551E2C06FD97803),
       (0x3876D935ACE84E9E874FCBE3D3A04702, '2024-12-02 15:07:32.349586', null,
        '2024-12-02 15:07:32.308589', 'AGREED', 0x7CFAA172AB2111EFBBD800155DC6E466,
        0xF60B81ACB2234340BE8B28621BF14BE7),
       (0x52E5A1A000A94420A7331CFB5DFC4D30, '2024-12-02 15:07:32.348586', null,
        '2024-12-02 15:07:32.308589', 'AGREED', 0x7CFA39D8AB2111EFBBD800155DC6E467,
        0xF60B81ACB2234340BE8B28621BF14BE7),
       (0xC5EB1CDBAB6E4B68A761523E24427DB7, '2024-12-02 15:07:32.349586', null,
        '2024-12-02 15:07:32.308589', 'AGREED', 0x7CFA9E06AB2111EFBBD800155DC6E468,
        0xF60B81ACB2234340BE8B28621BF14BE7);

INSERT INTO category (id, created_at, updated_at, deleted_at, user_id, category_name)
VALUES (1, '2024-11-25 20:48:08.608000', null, null, 0xF60B81ACB2234340BE8B28621BF14BE7, '운동'),
       (2, '2024-12-09 18:57:52.000000', null, null, 0xF60B81ACB2234340BE8B28621BF14BE7, '코딩');

INSERT INTO map(id, address_name, latitude, longitude)
VALUES (1, '서울 동작구 노량진로 111', 37.5134034035131, 126.943292835393),
       (2, '서울 동작구 노량진로 111', 37.5134034035131, 126.943292835393),
       (3, '서울 동작구 노량진로 111', 37.5134034035131, 126.943292835393),
       (4, '서울 영등포구 국제금융로 78', 37.5134034035131, 126.943292835393),
       (5, '서울 영등포구 국제금융로 78', 37.5134034035131, 126.943292835393);

INSERT INTO gather(id, created_at, updated_at, deleted_at, category_id, map_id, title, description,
                   like_count)
VALUES (1, '2024-11-25 20:50:52.000000', null, null, 1, 1, '운동할사람', '찾습니다', 0),
       (2, '2024-11-25 20:52:52.000000', null, null, 1, 2, 'test1', 'test1', 0),
       (3, '2024-11-25 20:54:52.000000', null, null, 1, 3, 'test2', 'test2', 0),
       (4, '2024-11-25 20:55:52.000000', null, null, 1, 4, 'test3', 'test3', 0),
       (5, '2024-11-25 20:56:52.000000', null, null, 1, 5, 'test3', 'test4', 0);

INSERT INTO member (id, created_at, updated_at, deleted_at, gather_id, user_id, permission)
VALUES (1, '2024-12-13 10:54:59.000000', '2024-12-13 10:55:02.000000', null, 1,
        0x1494D6C779D34086A551E2C06FD97803, 'MANAGER'),
       (2, '2024-12-13 10:54:59.000000', '2024-12-13 10:55:02.000000', null, 1,
        0xF60B81ACB2234340BE8B28621BF14BE7, 'GUEST');

INSERT INTO member (id, created_at, updated_at, deleted_at, gather_id, user_id, permission)
VALUES (1, '2024-12-13 10:54:59.000000', '2024-12-13 10:55:02.000000', null, 1,
        0x1494D6C779D34086A551E2C06FD97803, 'MANAGER'),
       (2, '2024-12-13 10:54:59.000000', '2024-12-13 10:55:02.000000', null, 1,
        0xF60B81ACB2234340BE8B28621BF14BE7, 'GUEST');