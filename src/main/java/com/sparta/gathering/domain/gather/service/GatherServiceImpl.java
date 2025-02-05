package com.sparta.gathering.domain.gather.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.common.service.SlackNotifierService;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.category.repository.CategoryRepository;
import com.sparta.gathering.domain.gather.document.GatherDocument;
import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.dto.response.GatherListResponse;
import com.sparta.gathering.domain.gather.dto.response.GatherResponse;
import com.sparta.gathering.domain.gather.dto.response.NewGatherResponse;
import com.sparta.gathering.domain.gather.dto.response.RankResponse;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.gather.repository.elastic.GatherElasticRepository;
import com.sparta.gathering.domain.hashtag.entity.HashTag;
import com.sparta.gathering.domain.hashtag.repository.HashTagRepository;
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
    private final HashTagRepository hashTagRepository;
    private final GatherElasticRepository elasticRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> rediusTemplate;
    private final ZSetOperations<String, Object> zsetOperations;
    private final MapRepository mapRepository;
    private final SlackNotifierService slackNotifierService;
    private static final String REDIS_CITY_KEY = "city";

    // 모임생성
    @Transactional
    @Override
    public void createGather(GatherRequest request, AuthenticatedUser authenticatedUser, Long categoryId) {
        // 카테고리와 사용자 조회
        Category category = getCategory(categoryId);
        User user = getUser(authenticatedUser.getUserId());

        // Redis에 map 저장
        addRedisMap(request);

        // Map 객체 생성 및 Gather, Member, ElasticSearch 생성
        Map newMap = createMap(request);
        Gather gather = createGather(request, category, newMap);
        gather = gatherRepository.save(gather);

        Member member = createMember(user, gather);
        GatherDocument document = GatherDocument.from(gather);

        // Gather, Member 저장
        saveData(member, document);

        // 해시태그 연결
        List<String> hashTagNames = request.getHashtags(); // 요청에서 해시태그 목록 가져오기
        connectHashTagsToGather(gather, hashTagNames);

        // Redis ZSet 값 갱신
        updateRedisZSet(gather);

        // Slack 알림 전송
        sendSlackNotification(category, request);
    }

    //모임 수정 gather
    @Transactional
    @Override
    public void modifyGather(GatherRequest request, Long id, AuthenticatedUser authenticatedUser) {
        Gather gather = findGatherById(id);
        validateManager(id, authenticatedUser);

        Map map = findMapByGatherId(id);

        updateRedisScores(gather, -1); // 기존 주소 score -1
        gather.updateGather(request.getTitle(), request.getDescription(), request.getHashtags(), map);
        updateRedisScores(gather, 1);  // 수정된 주소 score +1

        GatherDocument document = GatherDocument.from(gather);

        gatherRepository.save(gather);
        elasticRepository.save(document);
    }

    private Gather findGatherById(Long id) {
        return gatherRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
    }

    private Map findMapByGatherId(Long id) {
        return mapRepository.findByGatherId(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
    }

    private void updateRedisScores(Gather gather, int scoreDelta) {
        String addressName = gather.getMap().getAddressName();
        redisTemplate.opsForZSet().incrementScore(REDIS_CITY_KEY, addressName, scoreDelta);
    }

    //모임 삭제
    @Transactional
    @Override
    public void deleteGather(Long id, AuthenticatedUser authenticatedUser) {
        //매니저 검증
        Gather gather = findGatherById(id);
        validateManager(id, authenticatedUser);
        //  redis 기존 score -1
        redisTemplate.opsForZSet().incrementScore(REDIS_CITY_KEY, gather.getMap().getAddressName(), -1);
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

    @Transactional(readOnly = true)
    @Override
    public List<RankResponse> getTop5Ranking() {
        // top5Ranking 키에서 상위 5개 데이터 조회
        Set<ZSetOperations.TypedTuple<Object>> top5 = redisTemplate.opsForZSet()
                .reverseRangeWithScores("top5Ranking", 0, 4);

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
    public Page<GatherDocument> findByTitle(Pageable pageable,String title) {
        return elasticRepository.findByTitleContaining(pageable, title);
    }


    // 새로운 모임 5개 조회
    @Transactional(readOnly = true)
    public List<NewGatherResponse> newCreatedGatherList() {
        List<Gather> gatherList = gatherRepository.findTop5ByOrderByCreatedAtDesc();

        return gatherList.stream()
                .map(NewGatherResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GatherListResponse> getGatherList(AuthenticatedUser authenticatedUser) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        List<Gather> gathers = gatherRepository.findAllByUserId(user.getId());

        return gathers.stream().map(GatherListResponse::from).collect(Collectors.toList());
    }

    private void validateManager(Long id, AuthenticatedUser authenticatedUser) {

        UUID managerId = memberRepository.findManagerIdByGatherId(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MANAGER_NOT_FOUND));

        if (!managerId.equals(authenticatedUser.getUserId()) && authenticatedUser.getAuthorities().stream()
                .noneMatch(authority -> authority.getAuthority().equals(UserRole.ROLE_ADMIN.toString()))) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }
    }

    //래디스 모임 위치 저장 로직
    void addRedisMap(GatherRequest request) {
        GeoOperations<String, String> geoOperations = rediusTemplate.opsForGeo();
        Point point = new Point(request.getLongitude(), request.getLatitude());
        geoOperations.add("map", point, request.getTitle());
    }

    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.NOT_FOUNT_CATEGORY));
    }

    public User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    public Map createMap(GatherRequest request) {
        return new Map(request.getAddressName(), request.getLatitude(), request.getLongitude());
    }

    public Gather createGather(GatherRequest request, Category category, Map newMap) {
        Gather gather = new Gather(request.getTitle(), request.getDescription(), category, request.getHashtags());
        gather.saveMap(newMap);
        return gather;
    }

    public Member createMember(User user, Gather gather) {
        return new Member(user, gather, Permission.MANAGER);
    }

    public void saveData(Member member, GatherDocument document) {
        memberRepository.save(member);
        elasticRepository.save(document);
    }

    public void updateRedisZSet(Gather gather) {
        Object result = redisTemplate.opsForZSet().score("city", gather.getMap().getAddressName());
        if (result != null) {
            redisTemplate.opsForZSet().incrementScore("city", gather.getMap().getAddressName(), 1);
        } else {
            redisTemplate.opsForZSet().add("city", gather.getMap().getAddressName(), 1);
        }
    }

    public void sendSlackNotification(Category category, GatherRequest request) {
        slackNotifierService.sendNotification("[모임이 생성되었습니다] \n 카테고리명 : " + category.getCategoryName() +
                "\n 모임명 : " + request.getTitle());
    }

    private void connectHashTagsToGather(Gather gather, List<String> hashTagNames) {
        if (hashTagNames == null || hashTagNames.isEmpty()) {
            return; // 해시태그가 없으면 처리 생략
        }

        for (String hashTagName : hashTagNames) {
            // 해시태그 조회 및 저장
            HashTag hashTag = hashTagRepository.findByHashTagName(hashTagName)
                    .orElseGet(() -> hashTagRepository.save(new HashTag(hashTagName)));

            // Gather와 HashTag 연결
            gather.addHashTag(hashTag);
        }
    }
}
