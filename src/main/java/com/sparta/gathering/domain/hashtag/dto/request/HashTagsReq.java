package com.sparta.gathering.domain.hashtag.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class HashTagsReq {
    private List<String> hashTagName;
}
