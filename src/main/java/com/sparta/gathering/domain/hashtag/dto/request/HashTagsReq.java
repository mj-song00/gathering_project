package com.sparta.gathering.domain.hashtag.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HashTagsReq {

    private List<String> hashTagName;
}
