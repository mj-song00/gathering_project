package com.sparta.gathering.domain.gather.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.category.repository.CategoryRepository;
import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.hashtag.repository.HashTagRepository;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.user.dto.response.UserDTO;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    // 모임생성
    @Transactional
    public void createGather(GatherRequest request, UserDTO userDto, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.NOT_FOUNT_CATEGORY));
        User user = userRepository.findById(userDto.getUserId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        Gather gather = new Gather(request.getTitle(), request.getDescription(), category, request.getHashtags());
        Member member = new Member(user, gather, Permission.MANAGER);
        gatherRepository.save(gather);
        memberRepository.save(member);

        //redisClient.zadd('',1)
    }

    //모임 수정 gather
    public void modifyGather(GatherRequest request, Long id, UserDTO userDto) {
        validateManager(id, userDto);
        Gather gather = gatherRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        gather.updateGatherTitle(request.getTitle());
        gatherRepository.save(gather);

//        redis 기존꺼 삭제
  //              수정되는거 추가
    }

    //모임 삭제
    @Transactional
    public void deleteGather(Long id, UserDTO userDto) {
        validateManager(id, userDto);
        Gather gather = gatherRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        gather.delete();
        gatherRepository.save(gather);

    //    기존꺼에서 하나 빼기
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

    private void validateManager(Long id, UserDTO userDto) {
        UUID managerId = memberRepository.findManagerIdByGatherId(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MANAGER_NOT_FOUND));

        if (!managerId.equals(userDto.getUserId()) && userDto.getUserRole() != UserRole.ROLE_ADMIN) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }
    }
}
