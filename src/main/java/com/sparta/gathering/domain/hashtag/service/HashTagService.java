package com.sparta.gathering.domain.hashtag.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.hashtag.dto.request.HashTagReq;
import com.sparta.gathering.domain.hashtag.dto.response.HashTagRes;
import com.sparta.gathering.domain.hashtag.entity.HashTag;
import com.sparta.gathering.domain.hashtag.repository.HashTagRepository;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashTagService {
    private final HashTagRepository hashTagRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final GatherRepository gatherRepository;

    // 해시태그 생성
    @Transactional
    public HashTagRes createHashTag(User user, Gather gather, HashTagReq hashTagReq) {
        isValidGather(gather);
        // 멤버 권한 확인
        isValidMember(user);
        if (hashTagRepository.findByHashTagName(hashTagReq.getHashTagName()).isPresent()) {
            throw new BaseException(ExceptionEnum.ALREADY_HAVE_HASHTAG);
        }

        HashTag hashTag = HashTag.from(hashTagReq, gather);
        HashTag savedHashTag = hashTagRepository.save(hashTag);
        return HashTagRes.from(savedHashTag);

    }

    // 해시태그 삭제
    @Transactional
    public void deleteHashTag(User user, Gather gather, UUID hashtagId) {
        isValidGather(gather);
        // 멤버 권한 확인
        isValidMember(user);
        HashTag hashTag = hashTagRepository.findById(hashtagId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.NOT_FOUNT_HASHTAG));
        hashTag.updateDeleteAt();
    }


    // 해시태그 조회
    public List<HashTagRes> getHashTagList(User user, Gather gather) {
        Gather newgather = isValidGather(gather);
        return hashTagRepository.findByGatherId(newgather.getId())
                .stream()
                .map(HashTagRes::from)
                .toList();
    }

    // 모임 확인
    public Gather isValidGather(Gather gather) {
        return gatherRepository.findById(gather.getId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
    }

    // Manager 권한 확인
    public Member isValidMember(User user) throws BaseException {
        Member member = memberRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (!member.getPermission().equals(Permission.MANAGER)) {
            throw new BaseException(ExceptionEnum.NOT_ADMIN_ROLE);
        }
        return member;
    }


}