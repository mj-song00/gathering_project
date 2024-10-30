package com.sparta.gathering.domain.member.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.user.dto.response.UserDTO;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final GatherRepository gatherRepository;

    @Transactional
    public void createMember(UUID userId, long gatherId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        Member member = new Member(user, gather, Permission.PENDDING);
        memberRepository.save(member);
    }

    public Page<Member> getMembers(Pageable pageable, long gatherId) {
        return memberRepository.findByGatherIdAndDeletedAtIsNull(pageable, gatherId);
    }

    public void approval(long memberId, long gatherId, UserDTO userDto) {
        validateManager(gatherId, userDto);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MEMBER_NOT_FOUND));
        member.updatePermission(Permission.GUEST);
        memberRepository.save(member);
    }

    @Transactional
    public void refusal(long memberId, long gatherId, UserDTO userDto) {
        validateManager(gatherId, userDto);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MEMBER_NOT_FOUND));
        member.updatePermission(Permission.REFUSAL);
        member.delete();
        memberRepository.save(member);
    }

    @Transactional
    public void withdrawal(long memberId, UserDTO userDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MEMBER_NOT_FOUND));
        if (!member.getUser().getId().equals(userDto.getUserId())) {
            throw new BaseException(ExceptionEnum.USER_NOT_FOUND);
        }
        if (member.getDeletedAt() != null) {
            throw new BaseException(ExceptionEnum.ALREADY_DELETED_MEMBER);
        }

        member.delete();
        memberRepository.save(member);
    }

    private void validateManager(long gatherId, UserDTO userDto) {
        UUID managerId = memberRepository.findManagerIdByGatherId(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MANAGER_NOT_FOUND));

        if (!managerId.equals(userDto.getUserId()) && userDto.getUserRole() != UserRole.ROLE_ADMIN) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }
    }
}
