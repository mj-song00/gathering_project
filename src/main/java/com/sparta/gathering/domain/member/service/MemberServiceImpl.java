package com.sparta.gathering.domain.member.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final GatherRepository gatherRepository;
//    private final NotificationService notificationService;

    @Transactional
    @Override
    public void createMember(UUID userId, long gatherId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        //매니저 확인
        Member manager = memberRepository.findByGatherIdAndPermission(gatherId, Permission.MANAGER)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MANAGER_NOT_FOUND));

        //본인 초대 금지
        if (userId == manager.getUser().getId()) {
            throw new BaseException(ExceptionEnum.MEMBER_NOT_ALLOWED);
        }

        Member member = new Member(user, gather, Permission.PENDDING);
        memberRepository.save(member);

        //신규 가입 신청 정보
//        notificationService.broadcast(manager.getId(),
//                EventPayload.builder()
//                        .memberId(member.getId().toString())
//                        .build()
//        );
    }

    public Page<Member> getMembers(Pageable pageable, long gatherId) {
        return memberRepository.findByGatherIdAndDeletedAtIsNull(pageable, gatherId);
    }

    public void approval(long memberId, long gatherId, AuthenticatedUser authenticatedUser) {
        validateManager(gatherId, authenticatedUser);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MEMBER_NOT_FOUND));
        member.updatePermission(Permission.GUEST);
        memberRepository.save(member);
    }

    @Transactional
    @Override
    public void refusal(long memberId, long gatherId, AuthenticatedUser authenticatedUser) {
        validateManager(gatherId, authenticatedUser);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MEMBER_NOT_FOUND));
        member.updatePermission(Permission.REFUSAL);
        member.delete();
        memberRepository.save(member);
    }

    @Transactional
    @Override
    public void withdrawal(long memberId, AuthenticatedUser authenticatedUser) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MEMBER_NOT_FOUND));
        if (!member.getUser().getId().equals(authenticatedUser.getUserId())) {
            throw new BaseException(ExceptionEnum.USER_NOT_FOUND);
        }
        if (member.getDeletedAt() != null) {
            throw new BaseException(ExceptionEnum.ALREADY_DELETED_MEMBER);
        }

        member.delete();
        memberRepository.save(member);
    }

    private void validateManager(long gatherId, AuthenticatedUser authenticatedUser) {
        // gatherId에 대한 매니저 ID를 찾고, 없으면 예외를 던짐
        UUID managerId = memberRepository.findManagerIdByGatherId(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MANAGER_NOT_FOUND));  // Optional.empty()인 경우 예외 발생

        // 매니저 권한 검증
        if (!managerId.equals(authenticatedUser.getUserId()) && authenticatedUser.getAuthorities().stream()
                .noneMatch(authority -> authority.getAuthority().equals(UserRole.ROLE_ADMIN.toString()))) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }
    }

    // 사용자가 특정 모임에 속해 있는지 확인하는 메서드
    public boolean isUserInGathering(Long gatheringId, AuthenticatedUser authenticatedUser) {
        Member member = memberRepository.findByUserId(authenticatedUser.getUserId()).orElse(null);

        // 사용자가 멤버가 아니거나 모임 ID가 일치하지 않는 경우 false 반환
        if (member == null || !member.getGather().getId().equals(gatheringId)) {
            return false;
        }

        return true;
    }
}
