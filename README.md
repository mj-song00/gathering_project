
![제목을 입력해주세요_-001 (1)](https://github.com/user-attachments/assets/5804c49b-d50a-47a7-9564-05ccd4762812)
# 🎨 [취미 모임 관리 웹사이트](https://www.notion.so/teamsparta/21-5f3e6a5d16e84de48916ea9904b4fc91)

같은 취미를 가진 사람들과 모임을 쉽게 만들고 관리할 수 있는 웹사이트입니다. 사용자는 모임을 생성하고, 멤버를 관리하며, 실시간 소통과 일정 관리를 할 수 있습니다.


---
### 🔍 개요
​
이 웹사이트는 취미를 중심으로 모임을 쉽게 생성하고 관리할 수 있는 플랫폼입니다. 사용자는 다음과 같은 기능을 이용할 수 있습니다:
​
-   **모임 정보 입력**
-   **회원 모집**
-   **실시간 소통**
-   **일정 및 모임 관리**
    ​
### 🌟 배경

사람들의 관심사가 다양해지면서 비슷한 관심을 가진 사람들과 경험을 공유하려는 욕구가 커지고 있습니다. 하지만 취미 모임을 직접 조직하고 관리하는 일은 홍보, 소통, 일정 관리 등 여러 면에서 어려움을 겪을 수 있습니다. 이 웹사이트은 이러한 과정을 간소화하는 데 목적이 있습니다.


---
### 🤗 프로젝트 멤버 및 기간

2024-10-21 ~ 2024-11-22

| 이름     | 역할  |   
|----------|---------|
| [송민지 ](https://github.com/mj-song00)   |  리더      |
|  [조은형](https://github.com/eunhyeong99)    |  부리더   |        
| [ 고  결 ](https://github.com/gyeol9012)   |   팀원     |        
| [번영덕](github.com/zerodeok)     |   팀원     |        
|  [이정현](https://github.com/LJH4987)      |   팀원     |      

---
### 💨 SWAGGER 사용

#### [Swagger URL](http://localhost:8080/swagger-ui/index.html)

---
### ⚙️ 핵심 기능
1.  **🔐 회원가입 및 로그인**
    -   **이메일 회원가입**: 이메일을 통해 회원가입
    -   **카카오 회원가입**: 카카오를 통한 회원가입
    -   **로그인**: 이메일 또는 카카오를 통한 로그인
    -   **회원 탈퇴**: 비밀번호 입력 후 소프트 딜리트
2.  **👤 프로필 관리 (CUD)**
    -   프로필 정보 조회 (이미지, 닉네임)
    -   프로필 이미지 등록
    -   프로필 이미지 및 닉네임 변경
3.  **🎉 모임 관리 (CRUD)**
    -   **생성, 수정, 삭제**: 소규모 취미 모임 생성 및 수정, 삭제
    -   **카테고리별 조회**: 선택한 카테고리별로 모임 조회
    -   **해시태그 조회**: 해시태그를 통해 모임 검색
    -   **title 조회**: 제목을 통한 모임 검색
4.  **📝 공지(게시판) 관리 (CUD)**
    -   게시판에서 공지 작성, 수정, 삭제
5.  **💬 댓글 관리 (CUD)**
    -   게시글에 댓글 작성, 수정, 삭제
6.  **🏷️ 해시태그 (CD)**
    -   콘텐츠를 분류할 수 있는 태그 생성 및 삭제
7.  **👍 좋아요 (CD)**
    - 소속된 모임에 좋아요/취소      ​
---
### 🚀 추가 기능

### 1️⃣ 위치 기반 모임 추천 서비스
- **추천 알고리즘**:  
  사용자의 위치를 기준으로 **반경 10km 이내**의 모임을 추천합니다.  
  프론트에서 사용자의 위치를 서버로 전송하면, 서버는 **Redis의 GeoOperation**을 통해 거리를 계산해 반경 내 모임을 리스트로 제공합니다.

- **모임 랭킹**:
  zSetOperation자료구조를 이용하여 생성이 많이 된 지역 순으로 랭킹 확인을 할 수 있습니다.

### 2️⃣ 실시간 채팅 서비스
- **데이터 흐름**:  
  동일한 채팅방에 입장한 유저들의 대화 내용은 ChatController → ChatService 경로를 거쳐 **MySQL**에 저장됩니다. 이후 **Redis Publisher** 기능을 통해 대화 내용이 Redis 메모리에 저장되며, **Subscriber**들에게 실시간으로 전달됩니다.

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

`결과`
1.  map과 hashTag를 leftJoin().fetchJoin()으로 설정하자 N+1 문제가 해결되었습니다.
2.  Gather table에 인덱스를 적용하고 검색하자 카테고리 검색 84.3%, 해시태그 검색 85%, 타이틀 검색 77.7% 개선 되었습니다.
3.  일반검색 대비 Throughput 개선률이 **약 4배**이상 개선되었습니다.<br>
![image](https://github.com/user-attachments/assets/7f8634eb-66a5-4d84-8756-3260f97e44f2)
![image](https://github.com/user-attachments/assets/836bdcf6-9cda-4f38-8cb4-7d4cf62af00f)

| 검색 타입      | **Batch 적용 개선율** | **DB Index 적용 개선율** | **전체 개선율 (일반 → DB Index)** |
|----------------|-----------------------|--------------------------|-----------------------------------|
| 카테고리 검색  | 313.85%              | 22.59%                   | 407.69%                          |
| HashTag 검색   | 318.46%              | 21.32%                   | 408.46

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

기존 시스템에서는 **MySQL**에서 시/군/구 단위로 모임 데이터를 가져와 정렬하고, 이를 스케줄링 작업(`@Scheduled`)으로 클라이언트에 제공하였습니다. 이 방식은 다음과 같은 문제가 발생했습니다:

1. **서버 부하 증가**: 스케줄링 작업이 많아질수록 데이터베이스 부하가 증가하여 성능이 저하될 수 있었습니다.
2. **비효율적인 데이터 정렬**: 시/군/구 데이터를 직접 집계 및 정렬하는 작업이 반복되면서 **처리 속도가 저하**되었습니다.

### 개선 방안: Redis ZSET Operation 사용

**Redis ZSET(정렬된 집합) 구조**를 활용하여 효율적으로 데이터를 관리하고 클라이언트에 제공하도록 개선하였습니다.

- **ZSET 특징**: ZSET은 각 데이터에 고유한 `score` 값을 할당하여 자동으로 정렬된 순서를 유지합니다. 이를 통해 추가적인 정렬 작업 없이도 **순위를 쉽게 확인**할 수 있습니다.

### 새로운 데이터 흐름

1. **데이터 집계 및 스케줄링 작업 간소화**:
   Redis ZSET을 사용하여 별도의 스케줄링 작업 없이도 **정렬된 데이터**를 실시간으로 관리할 수 있습니다.

2. **트래픽 분산**:
   Redis의 고속 데이터 처리 특성을 이용해 MySQL 대신 Redis에서 순위를 관리함으로써 **서버 부하를 효과적으로 분산**시킬 수 있었습니다.

3. **구조도**:
   아래의 구조도는 새로운 Redis ZSET Operation을 통해 서버 부하를 최적화한 구조를 보여줍니다.

<img width="818" alt="스크린샷 2024-11-06 오후 1 09 55" src="https://github.com/user-attachments/assets/d034e32b-2d2f-4c30-b081-0a74a1f1b33c">

- **상단**: 기존 MySQL 기반 데이터 흐름 (비효율적인 집계 방식)
- **하단**: Redis ZSET Operation 적용 후 데이터 흐름 (최적화된 집계 방식)

  **설명**: MySQL에서 직접 데이터 정렬 및 집계하던 작업을 Redis ZSET으로 옮겨, 서버 부하를 줄이고 실시간 데이터 접근이 가능하도록 최적화하였습니다.

---
### [API 명세서](https://www.notion.so/teamsparta/monolog-API-1262dc3ef51481bf83d9d18c9cf78a3b)


---
### ERD![image](https://github.com/user-attachments/assets/70b6cb53-8cfd-428f-a43a-e303bbe92e8e)

---
### [인프라 설계도]
![image](https://github.com/user-attachments/assets/98d0138c-8f44-4677-9ca4-b2e8939900e3)



- **CI/CD 파이프라인**:  
  GitHub Actions를 이용해 GitHub에 코드가 병합되면 **Amazon ECR**에 Docker 이미지가 자동으로 업로드됩니다. 해당 이미지를 **Amazon ECS** 인스턴스에 배포하여 서버를 구동합니다.

- **Database**:  
  메인 데이터베이스로 **MySQL**을 사용하며, **실시간 채팅, 랭킹, 이메일 인증 서비스**는 Redis로 처리합니다.

---
### [와이어프레임](https://www.notion.so/teamsparta/21-5f3e6a5d16e84de48916ea9904b4fc91)
![final_project](https://github.com/user-attachments/assets/22b8469f-c568-4686-99fc-e5fa25bf5c65)


---

### 🛠️ 기술 스택
![](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white) 
![](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) 
![](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![](https://img.shields.io/badge/HTML-239120?style=for-the-badge&logo=html5&logoColor=white)
![](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![](https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)
![](https://img.shields.io/badge/Kakao-FFCD00?style=for-the-badge&logo=Kakao&logoColor=white)
![](https://img.shields.io/badge/Amazon_AWS-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)
![](https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white)
![](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white) 
![](https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white)
![](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
​
---
### 🔔 핵심 기능

### 1️⃣ 위치 기반 모임 추천 서비스

- **기능 설명**  
사용자의 위치를 기준으로 **이용자들이 원하는 거리에 맞게** 모임을 추천합니다.

- **구현 방법**:  
  사용자의 **위도와 경도** 정보를 받아 서버에서 **Redis에서 지원하는 GeoOperation**을 사용해 거리 계산을 수행합니다.

<img width="389" alt="스크린샷 2024-11-06 오후 1 18 13" src="https://github.com/user-attachments/assets/fe573d87-3599-4a1e-8a9e-b3d005e4b9b9"> <br>
<img width="389" alt="프론트 구현" src="https://github.com/user-attachments/assets/4139faf4-bd86-481d-a9ba-9410aa12f8c5">


###  2️⃣ 실시간 채팅 서비스

- **기능 설명**  
동일한 채팅방에 입장한 유저들이 실시간으로 대화를 주고받을 수 있습니다.

- **구현 방법**
사용자가 채팅 메시지를 전송하면 ChatController에서 메세지를 수신합니다.<br>
ChatService에서 메시지를 MySQL에 저장합니다.<br>
Redis Publisher로 메시지를 Redis 메모리에 저장합니다.<br>

<img width="371" alt="스크린샷 2024-11-06 오후 1 18 26" src="https://github.com/user-attachments/assets/19feae42-8697-432b-bfeb-821fd5392610">

- Redis는 빠른 인메모리 데이터 처리와 Pub/Sub 모델을 통해 실시간 메시지 전송을 지원하며, 대규모 트래픽을 효과적으로 분산할 수 있습니다.

---


