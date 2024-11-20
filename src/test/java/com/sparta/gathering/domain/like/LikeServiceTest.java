//package com.sparta.gathering.domain.like;
//
//import com.sparta.gathering.domain.gather.entity.Gather;
//import com.sparta.gathering.domain.gather.repository.GatherRepository;
//import com.sparta.gathering.domain.like.service.LikeService;
//import com.sparta.gathering.domain.member.entity.Member;
//import com.sparta.gathering.domain.member.enums.Permission;
//import com.sparta.gathering.domain.member.repository.MemberRepository;
//import com.sparta.gathering.domain.user.entity.User;
//import com.sparta.gathering.domain.user.repository.UserRepository;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.UUID;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//public class LikeServiceTest {
//
//    @Autowired
//    private LikeService likeService;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private GatherRepository gatherRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    private Gather gather;
//
//    User user1;
//    User user2;
//
//    @BeforeEach
//    void setUp() {
//        gather = gatherRepository.save(new Gather("살려줘"));
//        user1 = userRepository.findById(UUID.fromString("04a89669-0f04-46e7-a5e3-5a451fcf4938")).get();
//        user2 = userRepository.findById(UUID.fromString("bd4a2b14-1cc5-4e62-b75a-cd2d3a1c6238")).get();
//    }
//
//    @Nested
//    class like_test {
//        @Test
//        public void testAddLikeConcurrency() throws InterruptedException {
//            Member member1 = memberRepository.save(new Member(user1, gather, Permission.GUEST));
//            Member member2 = memberRepository.save(new Member(user2, gather, Permission.GUEST));
//            // 쓰레드 수와 작업 정의
//            int threadCount = 10;
//            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//            CountDownLatch latch = new CountDownLatch(threadCount);
//
//            try {
//                // 쓰레드 작업: 동일한 Member와 Gather로 Like 생성/삭제 시도
//                for (int i = 0; i < threadCount; i++) {
//                    executorService.execute(() -> {
//                        likeService.addLike(member1.getId(), gather.getId());
//                        latch.countDown();
//
//                    });
//                    executorService.execute(() -> {
//                        likeService.addLike(member2.getId(), gather.getId());
//                        latch.countDown();
//
//                    });
//                }
//            } finally {
//                // 모든 쓰레드가 작업을 마칠 때까지 대기
//                latch.await();
//            }
//
//            // 결과 확인
//            int likeCount = likeService.countLikeByGather(gather);
//            System.out.println("Final Like Count: " + likeCount);
//
//            // 동시성 문제를 검출: Like가 0 또는 1이어야 정상
//            assertEquals(0, likeCount);
//            assertEquals(0,  gatherRepository.findById(gather.getId()).get().getLikeCount());
//        }
//    }
//}
