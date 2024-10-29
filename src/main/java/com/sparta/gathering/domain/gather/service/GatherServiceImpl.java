package com.sparta.gathering.domain.gather.service;

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

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GatherServiceImpl implements GatherService {

    private final CategoryRepository categoryRepository;
    private final GatherRepository gatherRepository;
    private final MemberRepository memberRepository;

    // 모임생성
    @Transactional
    public void createGather(GatherRequest request, User user, UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.NOT_FOUNT_CATEGORY));
        Gather gather = new Gather(request.getTitle(), request.getDescription(), category);
        gatherRepository.save(gather);
        Member member = new Member(user, gather, Permission.MANAGER);
        memberRepository.save(member);
    }

    //모임 수정 gather
    @Transactional
    public void modifyGather(GatherRequest request, Long id, User user) {
        validateManager(id, user);
        Gather gather = gatherRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        gather.updateGatherTitle(request.getTitle());
        gatherRepository.save(gather);
    }

    //모임 삭제
    @Transactional
    public void deleteGather(Long id, User user) {
        validateManager(id, user);
        Gather gather = gatherRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        gather.delete();
        gatherRepository.save(gather);
    }

    //모임 불러오기
    public Page<Gather> Gathers(Pageable pageable, UUID categoryId) {
        return gatherRepository.findGathersByCategoryId(pageable, categoryId);
    }

    private void validateManager(Long id, User user) {
        UUID managerId = memberRepository.findManagerIdByGatherId(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MANAGER_NOT_FOUND));

        if (!managerId.equals(user.getId()) && user.getUserRole() != UserRole.ROLE_ADMIN) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }
    }
}
