package com.sparta.gathering.domain.hashtag.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.hashtag.dto.request.HashTagsReq;
import com.sparta.gathering.domain.hashtag.dto.response.HashTagRes;
import com.sparta.gathering.domain.hashtag.entity.HashTag;
import com.sparta.gathering.domain.hashtag.repository.HashTagRepository;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashTagService {

    private final HashTagRepository hashTagRepository;
    private final MemberRepository memberRepository;
    private final GatherRepository gatherRepository;

    // 해시태그 생성
    @Transactional
    public List<HashTagRes> createHashTag(AuthenticatedUser authenticatedUser, Gather gather, HashTagsReq hashTagReq) {
        isValidGather(gather);
        // 멤버 권한 확인
        isValidMember(authenticatedUser);

        // 모임별 해시태그 확인
        if (!hashTagRepository.findByGatherIdAndHashTagNameIn(gather.getId(), hashTagReq.getHashTagName()).isEmpty()) {
            throw new BaseException(ExceptionEnum.ALREADY_HAVE_HASHTAG);
        }

        List<HashTag> hashTag = new ArrayList<>();
        for (String hashTagName : hashTagReq.getHashTagName()) {
            hashTag.add(HashTag.of(hashTagName, gather));
        }
        List<HashTag> savedHashTag = hashTagRepository.saveAll(hashTag);
        return HashTagRes.from(savedHashTag);

    }


    // 해시태그 조회
    public List<HashTagRes> getHashTagList(Gather gather) {
        Gather newgather = isValidGather(gather);
        return hashTagRepository.findByGatherIdAndDeletedAtIsNull(newgather.getId())
                .stream()
                .map(HashTagRes::from)
                .toList();
    }


    // 해시태그 삭제
    @Transactional
    public void deleteHashTag(AuthenticatedUser authenticatedUser, Gather gather, Long hashtagId) {
        isValidGather(gather);
        // 멤버 권한 확인
        isValidMember(authenticatedUser);
        HashTag hashTag = hashTagRepository.findById(hashtagId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.NOT_FOUNT_HASHTAG));
        hashTag.updateDeleteAt();
    }


    // 모임 확인
    public Gather isValidGather(Gather gather) {
        return gatherRepository.findById(gather.getId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
    }

    // Manager 권한 확인
    public void isValidMember(AuthenticatedUser authenticatedUser) throws BaseException {
        Member member = memberRepository.findByUserId(authenticatedUser.getUserId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (!member.getPermission().equals(Permission.MANAGER)) {
            throw new BaseException(ExceptionEnum.NOT_ADMIN_ROLE);
        }
    }


}