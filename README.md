![제목을 입력해주세요_-001 (1)](https://github.com/user-attachments/assets/5804c49b-d50a-47a7-9564-05ccd4762812)

# 🎨 [취미 모임 관리 웹사이트](https://www.notion.so/teamsparta/21-5f3e6a5d16e84de48916ea9904b4fc91)

같은 취미를 가진 사람들과 모임을 쉽게 만들고 관리할 수 있는 웹사이트입니다. 사용자는 모임을 생성하고, 멤버를 관리하며, 실시간 소통과 일정 관리를 할 수 있습니다.

### 🛠️ 기술 스택

![](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![](https://img.shields.io/badge/HTML-239120?style=for-the-badge&logo=html5&logoColor=white)
![](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![](https://img.shields.io/badge/Kakao-FFCD00?style=for-the-badge&logo=Kakao&logoColor=white)
![](https://img.shields.io/badge/Amazon_AWS-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)
![](https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white)
![](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)
![](https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white)
![](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)

---

### 🔍 개요

​
이 웹사이트는 취미를 중심으로 모임을 쉽게 생성하고 관리할 수 있는 플랫폼입니다. 사용자는 다음과 같은 기능을 이용할 수 있습니다:
​

- **모임 정보 입력**
- **회원 모집**
- **실시간 소통**
- **일정 및 모임 관리**
  ​

### 🌟 배경

사람들의 관심사가 다양해지면서 비슷한 관심을 가진 사람들과 경험을 공유하려는 욕구가 커지고 있습니다. 하지만 취미 모임을 직접 조직하고 관리하는 일은 홍보, 소통, 일정 관리 등
여러 면에서 어려움을 겪을 수 있습니다. 이 웹사이트은 이러한 과정을 간소화하는 데 목적이 있습니다.


---

### 🤗 프로젝트 멤버 및 기간

2024-10-21 ~ 2024-11-22

| 이름                                     | 역할  |   
|----------------------------------------|-----|
| [송민지 ](https://github.com/mj-song00)   | 리더  |
| [조은형](https://github.com/eunhyeong99)  | 부리더 |        
| [ 고  결 ](https://github.com/gyeol9012) | 팀원  |        
| [번영덕](https://github.com/zerodeok)     | 팀원  |        
| [이정현](https://github.com/LJH4987)      | 팀원  |      

---

### 💨 SWAGGER 사용

![Swagger UI Demo](https://github.com/user-attachments/assets/ddf8ac4a-aff9-484e-8df5-52dd937d1707)

- **Swagger**를 사용하여 API 문서를 자동으로 생성하고, 개발자 간 명세 공유를 용이하게 했습니다.
- `SwaggerConfig`를 통해 문서화에 필요한 기본 정보(제목, 설명, 버전)를 설정
- 보안 강화를 위해 **개발 환경(dev)**에서만 `Swagger`가 활성화되도록 설정

**API 명세 자동화**:
   - RESTful API에 대한 요청/응답 스펙을 Swagger UI를 통해 시각화했습니다.
   - 가적인 어노테이션을 활용해 추가적인 API 설명을 문서화했습니다.

**환경별 접근 제한**:
   - `application.yml`에서 `dev` 프로파일에서만 `Swagger UI`에 접근할 수 있도록 조건부 활성화를 설정했습니다.
   - 이는 보안과 운영 환경의 간소화를 동시에 고려하였습니다. *(dev 환경에서만 가능)*

---

### ⚙️ 핵심 기능

1. **🔐 회원가입 및 로그인**
    - **이메일 회원가입**: 이메일을 통해 회원가입
    - **카카오 회원가입**: 카카오를 통한 회원가입
    - **로그인**: 이메일 또는 카카오를 통한 로그인
    - **회원 탈퇴**: 비밀번호 입력 후 소프트 딜리트
2. **👤 프로필 관리 (CUD)**
    - 프로필 정보 조회 (이미지, 닉네임)
    - 프로필 이미지 등록
    - 프로필 이미지 및 닉네임 변경
3. **🎉 모임 관리 (CRUD)**
    - **생성, 수정, 삭제**: 소규모 취미 모임 생성 및 수정, 삭제
    - **카테고리별 조회**: 선택한 카테고리별로 모임 조회
    - **해시태그 조회**: 해시태그를 통해 모임 검색
    - **title 조회**: 제목을 통한 모임 검색
4. **📝 공지(게시판) 관리 (CUD)**
    - 게시판에서 공지 작성, 수정, 삭제
5. **💬 댓글 관리 (CUD)**
    - 게시글에 댓글 작성, 수정, 삭제
6. **🏷️ 해시태그 (CD)**
    - 콘텐츠를 분류할 수 있는 태그 생성 및 삭제
7. **👍 좋아요 (CD)**
    - 소속된 모임에 좋아요/취소 ​

---

### 🔔 핵심 기능

### 1️⃣ 위치 기반 모임 추천 서비스

- **기능 설명**  
  사용자의 위치를 기준으로 **이용자들이 원하는 거리에 맞게** 모임을 추천합니다.

- **구현 방법**:  
  사용자의 **위도와 경도** 정보를 받아 서버에서 **Redis에서 지원하는 GeoOperation**을 사용해 거리 계산을 수행합니다.

<img width="389" alt="스크린샷 2024-11-06 오후 1 18 13" src="https://github.com/user-attachments/assets/fe573d87-3599-4a1e-8a9e-b3d005e4b9b9"> <br>
<img width="389" alt="프론트 구현" src="https://github.com/user-attachments/assets/4139faf4-bd86-481d-a9ba-9410aa12f8c5">

- **모임 랭킹**:
  zSetOperation자료구조를 이용하여 생성이 많이 된 지역 순으로 랭킹 확인을 할 수 있습니다.

### 2️⃣ 실시간 채팅 서비스

사용자는 **웹소켓**을 통해 사용자가 속한 모임간 실시간으로 메시지를 주고받을 수 있습니다.

### 주요 기능

- 실시간 메시지 전송
- 사용자 인증
- 입/퇴장 안내 메세지
- 메시지 저장 및 조회
- 메시지 브로드캐스트

### 시퀸스 다이어그램

![393754978-f7323992-2726-4e12-8371-5ebee961090e](https://github.com/user-attachments/assets/23e693d8-1473-4492-8db3-2606451ff6a0)


### 메시지 전송 워크플로우

- `굵은 선` : 요청 메시지를 나타냅니다, 요청 메시지는 한 개체가 작업을 수행하도록 지시하거나 데이터를 요청할 때 사용됩니다.
- `점 선` : 요청 메시지에 대한 응답 메시지를 나타냅니다, 요청 결과를 반환하거나 작업 완료 여부를 알려줍니다.</br>
  </br>
- `Client`: 사용자가 메시지를 전송합니다.
- `WebSocket` : 클라이언트로부터 메시지를 받아 `ChatController`로 전달합니다.
- `ChatController` : 메시지 요청을 처리하고 사용자 인증을 수행합니다.
- `AuthenticatedUser` : 사용자 정보를 확인합니다.
- `ChatService` : 메시지 처리 로직을 수행합니다.
- `Database` : 메시지를 저장합니다.
- `Redis` : 메시지를 퍼블리시하여 브로드캐스트합니다.
- `ChatMessageListener` : `Redis`로부터 메시지를 구독하고, `WebSocket`을 통해 다른 사용자들에게 메시지를 브로드캐스트합니다.<br/>
  </br>
- `퍼블리시` (Publish) : 메시지를 특정 채널에 등록해, 해당 채널을 구독하는 모든 리스너가 접근할 수 있도록 발행하는 행위입니다.</br>
- `브로드캐스트` (Broadcast) : 여러 대상에게 메시지를 동시 전달하는 행위로, `WebSocket`을 사용하여 실시간으로 모든 클라이언트에게 메시지를 전송합니다.


### 구현된 채팅 기능 데모
​![chat](https://github.com/user-attachments/assets/72d90215-624b-46cd-b684-90674c24639b)

<details>
<summary>기존 Redis Pub/Sub 만으로 구현된 초기 버전과 차이점</summary>

| 특징         | Redis Pub/Sub                          | 현재 시스템                                     |
|------------|----------------------------------------|--------------------------------------------|
| 메시지 저장     | 저장되지 않음<br>(구독자가 없으면 메시지 유실)           | 메시지를 DB에 저장하여 추후 조회 및 분석 가능                |
| 메시지 전송     | Redis가 구독자들에게 직접 전송                    | Redis에서 메시지를 수신한 후,<br> WebSocket으로 브로드캐스트 |
| 추가 로직      | 없음                                     | 사용자 인증, 메시지 유효성 검사, 방 존재 확인 등 처리           |
| 실시간성       | 빠름                                     | Redis와 WebSocket을 조합하여 빠른 응답 제공            |
| 확장성        | 단일 목적에 적합<br>(Pub/Sub 외 다른 기능 지원하지 않음) | 다른 시스템과 통합 가능<br> (예 : 채팅 알림, 통계 시스템)      |
| 구독자가 없는 경우 | 메시지가 유실됨                               | 메시지는 DB에 저장되므로 추후 클라이언트가 수신 가능             |

기존 Redis Pub/Sub만으로 보다 간단하게 구현한 실시간 채팅에서는

1. 메세지 보존 : 구독자가 없을 경우 메세지가 증발해 데이터 보존이 어려움
2. 확장성 : 추가적인 비즈니스 로직 처리/관리에 어려움
3. 조회 분석 : 저장된 메세지 내역을 통해 조회나 분석하는 관리의 어려움

등의 한계가 있어 보다 고도화를 진행 하였습니다.

</details>

### 3️⃣ 쿠폰 발급 시스템

**기능 설명**  
사용자 요청에 따라 쿠폰을 발급하고, 재고를 실시간으로 관리하며, 발급 상태를 사용자별로 저장하는 시스템입니다.
특징:

- 대량의 쿠폰 발급 요청을 처리할 수 있는 병렬 처리 방식.
- Redis를 활용한 빠른 데이터 접근과 TTL(Time-to-Live) 설정으로 데이터 만료 관리.
- 실패한 요청을 별도 큐에 저장하여 안정적으로 재처리 가능.

**구현 방법**

- Redis를 활용한 대기열 처리:

  대기열 생성: Redis의 List 구조(LPUSH, RPUSH)를 사용하여 사용자 요청을 큐에 저장.
  대기열 처리: @Scheduled를 활용해 Redis에서 데이터를 일정 간격으로 가져와(RPOP) 병렬로 처리.

`redisTemplate.opsForList().leftPush("couponQueue", jsonRequest);
List<Object> batch = redisTemplate.opsForList().rightPop("couponQueue", BATCH_SIZE);
`

- Redisson 락을 통한 중복 처리 방지:

  분산 환경에서 여러 인스턴스가 동시에 작업하지 않도록 Redisson의 분산 락을 사용.

  락이 없을 경우 작업을 건너뛰도록 구현.

`RLock lock = redissonClient.getLock("couponQueueLock");
if (!lock.tryLock(10, TimeUnit.SECONDS)) {
log.info("다른 인스턴스가 락을 보유 중입니다. 이번 작업은 건너뜁니다.");
return;
}
`

- 실시간 재고 관리:

Redis의 DECR 명령어를 사용해 쿠폰 재고를 관리.

재고 부족 시 요청을 실패 처리하고 대기열에서 제거.

`Long remainingStock = redisTemplate.opsForValue().decrement("couponStock");
if (remainingStock == null || remainingStock < 0) {
redisTemplate.opsForValue().increment("couponStock"); // 재고 복구
log.warn("쿠폰 발급 실패: 재고 부족");
}
`

- 발급 상태 저장 및 TTL 설정:

발급된 쿠폰의 상태(SUCCESS 또는 FAILED)를 사용자별로 Redis에 저장.

TTL(Time-to-Live) 설정을 통해 발급 상태 데이터를 24시간 유지.

`redisTemplate.opsForValue().set("couponIssued:" + userId, status, 86400, TimeUnit.SECONDS);
`

- 실패 데이터 재처리:

처리 중 실패한 요청은 별도의 Redis 대기열(failureQueue)에 저장.

주기적으로 실패 데이터를 재처리하는 스케줄러를 실행

`redisTemplate.opsForList().leftPush("failureQueue", failedRequest);
`

- 병렬 처리:

ExecutorService를 활용해 병렬 처리로 대량 요청을 효율적으로 처리.

작업 큐가 초과되면 호출자 스레드에서 작업을 실행하도록 정책 설정.

`private final ExecutorService executorService = new ThreadPoolExecutor(
10, 100, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(500),
new ThreadPoolExecutor.CallerRunsPolicy()
);
`

**주요 사용 기술**

Redis: List 구조, DECR 명령어, TTL 설정.
Redisson: 분산 락을 통한 데이터 충돌 방지.
Java: Spring Scheduler, ExecutorService를 활용한 병렬 처리.
JSON: 요청 데이터 직렬화/역직렬화(ObjectMapper).

**시스템 동작 흐름**

사용자 요청이 들어오면 Redis 대기열(couponQueue)에 저장.
@Scheduled를 통해 주기적으로 대기열 데이터를 가져옴.
Redisson 락으로 작업 충돌을 방지하며, 병렬로 요청 처리.
쿠폰 재고를 실시간으로 관리하며, 성공/실패 상태를 Redis에 저장.
실패한 요청은 failureQueue로 이동하고, 별도의 스케줄러로 재처리.

**장점**

고속 처리: Redis의 메모리 기반 처리로 대량의 요청도 빠르게 처리 가능.
확장성: Redisson 락과 병렬 처리를 통해 다중 인스턴스 환경에서도 안정적으로 동작.
안정성: 실패 데이터를 관리하고 재처리하여 데이터 손실 방지.
유연성: TTL 설정으로 불필요한 데이터 자동 삭제.

![image](https://github.com/user-attachments/assets/58f3dcf1-e248-4843-95e9-9fc6020f0e2b)

---

### 💥 트러블 슈팅

### 1️⃣ 검색 속도 개선

`문제점`

1. Gather table에는 참조하는 테이블이 많습니다.
2. map과 hashTag은 Eager Loding을 활용하였습니다.
3. 검색속도 저하와 함께 N+1의 문제도 함께 발생하였습니다.

`해결방안`

1. application-dev.yml파일에 batch size = 100으로 설정였습니다.
2. map과 hashTag를 leftJoin().fetchJoin()으로 설정하였습니다.
3. Gather table에 title로 새로운 인덱스를 생성하였습니다.
4. Elastic Search를 도입하였습니다.

`결과`

1. map과 hashTag를 leftJoin().fetchJoin()으로 설정하자 N+1 문제가 해결되었습니다.
2. Elastic Search를 적용하고 검색하자 카테고리 검색 82.37%, 해시태그 검색 78.65%, 타이틀 검색 71.29% 개선 되었습니다.
3. 일반검색 대비 Throughput 개선률이 **약 4배**이상 개선되었습니다.<br>
   ![image](https://github.com/user-attachments/assets/3155c741-80f3-45b4-8a5f-60dfc2582fb2)
   ![image](https://github.com/user-attachments/assets/d1983900-f032-476c-a278-1e395b092e3e)


| 검색 타입      | **Batch 적용 개선율** | **Elastic Search적용 개선율** | **전체 개선율 (일반 → DB Index)** |
|------------|------------------|---------------------|----------------------------|
| 카테고리 검색    | 313.85%          | 8.37%              | 350.00%                    |
| HashTag 검색 | 318.46%          | 9.19%              | 356.92%                     

### 2️⃣ 위치기반 모임 추천속도 개선

`문제점`

1. 클라이언트에서 위도,경도,nkm의 값을 받고 모임을 추천하려면 map의 모든 데이터를 하나하나 대조하는 구조였습니다..
2. map의 데이터가 많아질 수록 데이터 검색속도가 많이 걸리는 것을 확인하였습니다.
3. 느린 로딩 속도로 유저가 느끼는 불편함을 개선하고 운영 측면에서도 서버 비용을 감축할 필요가 있었습다.

`해결방안`
MySQL에 저장된 값을 Redis로 캐싱하여 GeoOperation 자료구조를 활용하여 주변 모임을 추천하였습니다.

`결과`
개선전 **164ms**에서 개선후 **16ms**로 **약 90%** 검색속도가 개선되었습니다.
![image](https://github.com/user-attachments/assets/c8f1f622-bc9c-4f30-a267-30b94a61693c)

### 3️⃣ Redis 자료구조를 활용한 서버 부하 최적화

### 기존 구조의 첫번째 문제점

기존 시스템에서는 **MySQL**에서 시/군/구 단위로 모임 데이터를 가져와 정렬하고, 이를 스케줄링 작업(`@Scheduled`)으로 클라이언트에 제공하였습니다. 이 방식은
다음과 같은 문제가 발생했습니다:

1. **서버 부하 증가**: 스케줄링 작업이 많아질수록 데이터베이스 부하가 증가하여 성능이 저하될 수 있었습니다.
2. **비효율적인 데이터 정렬**: 시/군/구 데이터를 직접 집계 및 정렬하는 작업이 반복되면서 **처리 속도가 저하**되었습니다.

### 개선 방안: Redis ZSET Operation 사용

**Redis ZSET(정렬된 집합) 구조**를 활용하여 효율적으로 데이터를 관리하고 클라이언트에 제공하도록 개선하였습니다.

- **ZSET 특징**: ZSET은 각 데이터에 고유한 `score` 값을 할당하여 자동으로 정렬된 순서를 유지합니다. 이를 통해 추가적인 정렬 작업 없이도 **순위를 쉽게 확인
  **할 수 있습니다.

### 새로운 데이터 흐름

1. **데이터 집계 및 스케줄링 작업 간소화**:
   Redis ZSET을 사용하여 별도의 스케줄링 작업 없이도 **정렬된 데이터**를 실시간으로 관리할 수 있습니다.

2. **트래픽 분산**:
   Redis의 고속 데이터 처리 특성을 이용해 MySQL 대신 Redis에서 순위를 관리함으로써 **서버 부하를 효과적으로 분산**시킬 수 있었습니다.

3. **Spring batch**:
   Spring batch를 사용하여 대용량의 데이터를 효율적이고 안정적으로 반복할 수 있을것으로 기대하하고 있습니다.
   <img width="761" alt="스크린샷 2024-12-16 오전 9 48 29" src="https://github.com/user-attachments/assets/88990449-88eb-40fc-98fd-5d7c2b2556d7" />


### 3️⃣ 쿠폰 요청 시 불필요한 쿼리 발생

`문제점`
![image](https://github.com/user-attachments/assets/f328f544-dbce-4e79-99d1-5abb5ef39f1c)

쿠폰 요청에서 member의 권한을 확인하는 로직 부분을 실행 시 쿼리가 추가적으로 연결되어있는 맵,모임,유저의 쿼리까지 같이 표시 되어 불필요한 쿼리가 조회되고 있었고
동시에 100건이 넘는 경우를 처리할때 너무 길어지는 점이 거슬렸다

`해결방안`

1. 그 불편함을 해소하기 위하여 캐싱을 이용해보고자 하였다

`@Cacheable(value = "memberPermissions", key = "#userId.toString()", unless = "#result == null")
public Member getCachedMemberByUserId(UUID userId) {
log.info("Fetching member from DB for userId: {}", userId);
return memberRepository.findByUserId(userId)
.orElseThrow(() -> new BaseException(ExceptionEnum.MANAGER_NOT_FOUND));
}`

하지만 그래도 캐싱이 저장되지않아서 다른 방법을 생각해보았다

2. 멤버 entity에 연관관계인 @ManyToOne(fetch = FetchType.LAZY) 으로 해서 지연로딩으로 필요한 쿼리만 조회되게 하기
   ![image](https://github.com/user-attachments/assets/1d167848-a665-42bf-bda2-48a1f0fdfd4d)

-> 이건 다른 팀원의 로직이라 다른 곳에 씌일 수 있으므로 보류

3. yml파일의 show_sql,format_Sql을 false로 하여 표시되지 않게 하기
   ![image](https://github.com/user-attachments/assets/f597068c-dc60-466f-be65-ea2fec6777fd)

-> 다른 모든 로직들의 sql도 보이지 않으므로 반영하지않음

4. MemberRepository에 query문으로 지정하여 원하는 부분만 표시 되게 반영하기
   ![image](https://github.com/user-attachments/assets/a01c19f6-c530-4d1a-b086-e8e059c73521)
   -> 다른사람의 로직은 안건드리면서 필요한 부분만 표시 할 수 있다는 점으로 반영하여 표시하였다

`결과`

결국 멤버의 권한을 조회할 때 memberRepository와 userReporitory를 둘 다 조회하지 않고 memberRepository에서 User와 member를 함께
조회하여
쿼리 단순화를 하게되었다

![image](https://github.com/user-attachments/assets/c3fcc063-f467-4742-8901-bcdd49fc4a27)

엄청난 양의 쿼리에서 필요한 부분만 조회될 수 있게 변경하였다!

---

### [API 명세서](https://www.notion.so/teamsparta/monolog-API-1262dc3ef51481bf83d9d18c9cf78a3b)

---

### ERD![image](https://github.com/user-attachments/assets/70b6cb53-8cfd-428f-a43a-e303bbe92e8e)

---

### [인프라 설계도]

![인프라 설계도](https://github.com/user-attachments/assets/28078e8e-7924-44cb-b02f-3167808881f1)

- **CI/CD 파이프라인**:  
  GitHub Actions를 통해 Docker 이미지를 **Amazon ECR**에 업로드한 후, **Amazon EC2**를 사용해 수동으로 배포를 진행합니다.  
  배포 시 **ACM**(AWS Certificate Manager)를 이용해 SSL 인증서를 적용하였으며, **Amazon Route 53**과 연결하여 도메인을 설정했습니다.

- **Database**:  
  메인 데이터베이스로 **MySQL**(Amazon RDS)를 사용하며, **실시간 채팅, 랭킹, 이메일 인증 서비스**는 **Redis Cloud**를 활용해 처리하고 있습니다.

### 🚀 향후 개선 사항
- 프로젝트 기간(1개월)이 짧아 CI/CD 자동화 작업을 완료하지 못했습니다. 
- 향후 **AWS CodeDeploy**나 **Elastic Beanstalk**를 활용하여 배포 자동화 프로세스를 구현할 계획입니다.
- 배포 자동화를 통해 운영의 효율성을 높이고, 더 빠르게 새로운 기능을 배포할 수 있도록 개선하고자 합니다.

---

### [초기 와이어프레임 V1](https://www.notion.so/teamsparta/21-5f3e6a5d16e84de48916ea9904b4fc91)

![final_project](https://github.com/user-attachments/assets/22b8469f-c568-4686-99fc-e5fa25bf5c65)

### 구현된 페이지

![398877121-69498f5d-7761-402e-89d2-013ba2d5fb15](https://github.com/user-attachments/assets/5951292c-5388-48b3-b7ef-ad7b05b23679)

![398877117-4ac91722-8ce3-45f6-a7ed-3b9fbd7757b8](https://github.com/user-attachments/assets/71ebe466-7641-4134-8668-1eda5ab6c367)

![398877114-0c1cf5ce-e158-4c63-99e1-aec1648e63af](https://github.com/user-attachments/assets/b3983261-b6fe-4336-846a-0640c35d08fb)

![398877104-a3b56e1c-a1bf-433f-9115-146d687e6e34](https://github.com/user-attachments/assets/41392aa5-567b-4c63-af1d-91468c0b8dda)

---
