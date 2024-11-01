package com.sparta.gathering.domain.gather.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.category.repository.CategoryRepository;
import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class GatherServiceImpl implements GatherService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final GatherRepository gatherRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    // 모임생성
    @Transactional
    public void createGather(GatherRequest request, AuthenticatedUser authenticatedUser, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.NOT_FOUNT_CATEGORY));
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        Gather gather = new Gather(request.getTitle(), request.getDescription(), category, request.getHashtags(), request.getMap());
        Member member = new Member(user, gather, Permission.MANAGER);

        //레디스에서 값 찾아오기
        Object result = redisTemplate.opsForZSet().score("city", gather.getMap().getAddressName());
        if (result != null) {
            redisTemplate.opsForZSet().incrementScore("city", gather.getMap().getAddressName(), 1);
        } else {
            redisTemplate.opsForZSet().add("city", gather.getMap().getAddressName(), 1);
        }

        gatherRepository.save(gather);
        memberRepository.save(member);
    }

    //모임 수정 gather
    @Transactional
    public void modifyGather(GatherRequest request, Long id, AuthenticatedUser authenticatedUser) {
        validateManager(id, authenticatedUser);

        Gather gather = gatherRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        // redis 기존 score -1
        redisTemplate.opsForZSet().incrementScore("city", gather.getMap().getAddressName(), -1);
        gather.updateGather(request.getTitle(), request.getDescription(), request.getHashtags(), request.getMap());
        // redis 수정된 주소 score +1
        redisTemplate.opsForZSet().incrementScore("city", gather.getMap().getAddressName(), 1);

        gatherRepository.save(gather);
    }

    //모임 삭제
    @Transactional
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
    public Page<Gather> gathers(Pageable pageable, Long categoryId) {
        return gatherRepository.findByCategoryWithHashTags(pageable, categoryId);
    }

    @Transactional(readOnly = true)
    public Page<Gather> findTitle(Pageable pageable, String keyword) {
        return gatherRepository.findByKeywordContaining(pageable, keyword);
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
}
