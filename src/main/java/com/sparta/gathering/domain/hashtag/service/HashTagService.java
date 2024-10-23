package com.sparta.gathering.domain.hashtag.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.hashtag.dto.request.HashTagReq;
import com.sparta.gathering.domain.hashtag.dto.response.HashTagRes;
import com.sparta.gathering.domain.hashtag.entity.HashTag;
import com.sparta.gathering.domain.hashtag.repository.HashTagRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashTagService {
    private final HashTagRepository hashTagRepository;
    private final UserRepository userRepository;

    // 해시태그 생성
    @Transactional
    public HashTagRes createHashTag(User user, HashTagReq hashTagReq) {
        // 유저 권한 확인
//        isValidMember(user);
        if (hashTagRepository.findByHashTagName(hashTagReq.getHashTagName()).isPresent()) {
            throw new BaseException(ExceptionEnum.ALREADY_HAVE_HASHTAG);
        }

        HashTag hashTag = HashTag.from(hashTagReq);
        HashTag savedHashTag = hashTagRepository.save(hashTag);
        return HashTagRes.from(savedHashTag);

    }

    // 해시태그 삭제
    @Transactional
    public void deleteHashTag(User user, UUID hashtagId) {
        HashTag hashTag = hashTagRepository.findById(hashtagId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.NOT_FOUNT_HASHTAG));
        hashTag.updateDeleteAt();
    }


    // 해시태그 조회
//    public List<HashTagRes> getHashTagList(User user) {
//        return hashTagRepository.findByGatheringId(gathertingId)
//                .stream()
//                .map(HashTagRes::from)
//                .toList();
//    }


    // Manager 권한 확인
//    public Member isValidMember(User user) throws BaseException {
//        User newUser = userRepository.findById(user.getId())
//                .orElseThrow(()-> new BaseException(ExceptionEnum.USER_NOT_FOUND));
//       = memberRepository.findById(newUser.getId())
//                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
//
//        if (!member.getMemberRole.equals(MemebrRole.ROLE_MANAGER)) {
//            throw new BaseException(ExceptionEnum.NOT_ADMIN_ROLE);
//        }
//        return member;
//    }


}