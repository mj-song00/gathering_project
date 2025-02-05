package com.sparta.gathering.domain.member.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.member.dto.eventpayload.EventPayload;
import com.sparta.gathering.domain.member.dto.eventpayload.EventType;
import com.sparta.gathering.domain.member.dto.response.MemberInfoResponse;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.notification.service.NotificationService;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final GatherRepository gatherRepository;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public void createMember(UUID userId, long gatherId, AuthenticatedUser authenticatedUser) {
        if (!userId.equals(authenticatedUser.getUserId())) {
            throw new BaseException(ExceptionEnum.USER_NOT_FOUND);
        }
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

        //중복신청 방지
        Optional<Member> existingMember = memberRepository.findByUserAndGather(user, gather);
        if (existingMember.isEmpty()) {
            Member member = new Member(user, gather, Permission.PENDDING);
            memberRepository.save(member);
        } else {
            throw new BaseException(ExceptionEnum.DUPLICATE_MEMBER);
        }

        //신규 가입 신청 정보
        notificationService.broadcast(manager.getUser().getId(),
                EventPayload.builder()
                        .nickname(user.getNickName() + " 님이 멤버신청을 하였습니다.")
                        .eventType(EventType.PANDING)
                        .gatherId(gather.getId())
                        .title(gather.getTitle())
                        .build()

        );
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

        //가입 승인 정보
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        notificationService.broadcast(member.getUser().getId(),
                EventPayload.builder()
                        .nickname(member.getUser().getNickName() + " 님의 신청을 승인하였습니다.")
                        .eventType(EventType.APPROVE)
                        .title(gather.getTitle())
                        .gatherId(gatherId)
                        .build()
        );
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

        //가입 거절 정보
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        notificationService.broadcast(member.getUser().getId(),
                EventPayload.builder()
                        .nickname(member.getUser().getNickName() + " 님의 신청을 거절하였습니다.")
                        .eventType(EventType.REJECT)
                        .title(gather.getTitle())
                        .gatherId(gatherId)
                        .build()
        );
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

    @Override
    public List<MemberInfoResponse> getMyId(AuthenticatedUser authenticatedUser) {
        List<Member> infos = memberRepository.findAllByUserId(authenticatedUser.getUserId());
        if (infos.isEmpty()){
            throw  new BaseException(ExceptionEnum.MEMBER_NOT_FOUND);
        }

        return infos.stream()
                .map(info -> new MemberInfoResponse(info))
                .collect(Collectors.toList());
    }

    private void validateManager(long gatherId, AuthenticatedUser authenticatedUser) {
        Member member = memberRepository.findByGatherIdAndUserId(gatherId, authenticatedUser.getUserId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.MANAGER_NOT_FOUND));
        if (!member.getPermission().equals(Permission.MANAGER)) {
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
