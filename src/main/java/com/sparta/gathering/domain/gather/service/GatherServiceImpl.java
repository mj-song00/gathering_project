package com.sparta.gathering.domain.gather.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.category.repository.CategoryRepository;
import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.dto.response.GatherResponse;
import com.sparta.gathering.domain.gather.dto.response.NewGatherResponse;
import com.sparta.gathering.domain.gather.dto.response.RankResponse;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.map.entity.Map;
import com.sparta.gathering.domain.map.repository.MapRepository;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@EnableScheduling
public class GatherServiceImpl implements GatherService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final GatherRepository gatherRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> rediusTemplate;
    private final ZSetOperations<String, Object> zsetOperations;
    private final MapRepository mapRepository;


    // 모임생성
    @Transactional
    @Override
    public void createGather(GatherRequest request, AuthenticatedUser authenticatedUser, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.NOT_FOUNT_CATEGORY));
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        addRedisMap(request); //래디스 맵저장 로직 메서드
        Map newMap = new Map(request.getAddressName(), request.getLatitude(), request.getLongitude()); // Map 객체 생성
        Gather gather = new Gather(request.getTitle(), request.getDescription(), category, request.getHashtags());
        Member member = new Member(user, gather, Permission.MANAGER);
        gather.saveMap(newMap);
        gatherRepository.save(gather);
        //레디스에서 값 찾아오기
        Object result = redisTemplate.opsForZSet().score("city", gather.getMap().getAddressName());
        if (result != null) {
            redisTemplate.opsForZSet().incrementScore("city", gather.getMap().getAddressName(), 1);
        } else {
            redisTemplate.opsForZSet().add("city", gather.getMap().getAddressName(), 1);
        }

        memberRepository.save(member);
    }

    //모임 수정 gather
    @Transactional
    @Override
    public void modifyGather(GatherRequest request, Long id, AuthenticatedUser authenticatedUser) {
        validateManager(id, authenticatedUser);

        Gather gather = gatherRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        Map map = mapRepository.findByGatherId(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        // redis 기존 score -1
        redisTemplate.opsForZSet().incrementScore("city", gather.getMap().getAddressName(), -1);
        gather.updateGather(request.getTitle(), request.getDescription(), request.getHashtags(), map);
        // redis 수정된 주소 score +1
        redisTemplate.opsForZSet().incrementScore("city", gather.getMap().getAddressName(), 1);

        gatherRepository.save(gather);
    }

    //모임 삭제
    @Transactional
    @Override
    public void deleteGather(Long id, AuthenticatedUser authenticatedUser) {
        validateManager(id, authenticatedUser);
        Gather gather = gatherRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        //  redis 기존 score -1
        redisTemplate.opsForZSet().incrementScore("city", gather.getMap().getAddressName(), -1);
        gather.delete();
        gatherRepository.save(gather);
    }

    //모임 불러오기
    @Transactional(readOnly = true)
    @Override
    public Page<Gather> gathers(Pageable pageable, Long categoryId) {
        return gatherRepository.findByCategoryWithHashTags(pageable, categoryId);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Gather> findByHashTags(Pageable pageable, List<String> hashTagName) {
        return gatherRepository.findByKeywords(pageable, hashTagName);
    }

    /**
     * 매일 자정마다 실행되는 스케쥴러 입니다.
     * 상위 5개의 데이터를 top5Ranking에 별도 저장후 기존 저장된 city는 삭제됩니다.
     * 이후 다시 모임이 생성되면 city역시 다시 저장됩니다.
     */
    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 0 * * *")
    public void ranks() {
        // 1. 상위 5개 데이터 조회
        Set<ZSetOperations.TypedTuple<Object>> top5 = redisTemplate.opsForZSet().reverseRangeWithScores("city", 0, 4);

        // 2. 상위 5개 데이터를 별도의 키에 저장 (top5Ranking)
        if (top5 != null && !top5.isEmpty()) {
            // 기존 top5Ranking에서 데이터가 있다면 6번째 이후 항목 삭제
            redisTemplate.opsForZSet().removeRange("top5Ranking", 5, -1);

            // 새 데이터를 top5Ranking에 추가
            top5.forEach(tuple -> redisTemplate.opsForZSet().add("top5Ranking", tuple.getValue().toString(), tuple.getScore()));
        } else {
            // top5가 null이거나 비어있으면 그냥 새로운 데이터만 저장
            if (top5 != null) {
                top5.forEach(tuple -> redisTemplate.opsForZSet().add("top5Ranking", tuple.getValue().toString(), tuple.getScore()));
            }
        }

        // 3. 기존 랭킹 데이터 삭제
        redisTemplate.opsForZSet().removeRange("city", 0, -1);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RankResponse> getTop5Ranking() {
        // top5Ranking 키에서 상위 5개 데이터 조회
        Set<ZSetOperations.TypedTuple<Object>> top5 = redisTemplate.opsForZSet().reverseRangeWithScores("top5Ranking", 0, 4);

        // 조회된 데이터를 RankResponse 리스트로 변환
        return (top5 == null || top5.isEmpty()) ? Collections.emptyList()
                : top5.stream().map(tuple -> new RankResponse(tuple.getScore(), tuple.getValue().toString()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public GatherResponse getDetails(Long gatherId) {
        Gather gather = gatherRepository.findByIdWithBoardAndSchedule(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        return new GatherResponse(gather);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Gather> findByTitles(Pageable pageable, String title) {
        return gatherRepository.findByTitle(pageable, title);
    }


    // 새로운 모임 5개 조회
    @Transactional(readOnly = true)
    public List<NewGatherResponse> newCreatedGatherList() {
        List<Gather> gatherList = gatherRepository.findTop5ByOrderByCreatedAtDesc();

        return gatherList.stream()
                .map(NewGatherResponse::from)
                .collect(Collectors.toList());
    }

    private void validateManager(Long id, AuthenticatedUser authenticatedUser) {
        UUID managerId = memberRepository.findManagerIdByGatherId(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MANAGER_NOT_FOUND));

        if (!managerId.equals(authenticatedUser.getUserId()) && authenticatedUser.getAuthorities().stream()
                .noneMatch(authority ->
                        authority.getAuthority().equals(UserRole.ROLE_ADMIN.toString()))) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }
    }

    //래디스 모임 위치 저장 로직
    private void addRedisMap(GatherRequest request) {
        GeoOperations<String, String> geoOperations = rediusTemplate.opsForGeo();
        Point point = new Point(request.getLongitude(), request.getLatitude());
        geoOperations.add("map", point, request.getTitle());
    }
}
