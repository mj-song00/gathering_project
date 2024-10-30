package com.sparta.gathering.domain.hashtag.dto.response;

import com.sparta.gathering.domain.hashtag.entity.HashTag;
import lombok.Getter;

import java.util.List;

@Getter
public class HashTagRes {

    private final Long hashtagId;
    private final String hashTagName;

    public HashTagRes(Long hashtagId, String hashTagName) {
        this.hashtagId = hashtagId;
        this.hashTagName = hashTagName;
    }

    public static HashTagRes from(HashTag hashTag) {
        return new HashTagRes(hashTag.getId(), hashTag.getHashTagName());
    }

    public static List<HashTagRes> from(List<HashTag> savedHashTag) {
        return savedHashTag.stream().map(HashTagRes::from).toList();
    }
}