package com.sparta.gathering.domain.hashtag.dto.response;

import com.sparta.gathering.domain.hashtag.entity.HashTag;
import lombok.Getter;

import java.util.UUID;

@Getter
public class HashTagRes {
//    private final UUID gatheringId;
    private final UUID hashTagId;
    private final String hashTagName;

    public HashTagRes(UUID hashTagId, String hashTagName) {
//        this.gatheringId = gatheringId;
        this.hashTagId = hashTagId;
        this.hashTagName = hashTagName;
    }

    public static HashTagRes from(HashTag hashTag) {
        return new HashTagRes(hashTag.getId(),hashTag.getHashTagName());
    }
}