package com.sparta.gathering.domain.member.dto.response;

import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import lombok.Getter;

@Getter
public class MemberInfoResponse {
    private final Long id;
    private final Permission permission;
    private final Long gatherId;
    private final String title;

    public MemberInfoResponse(Member member) {
        this.id = member.getId();
        this.permission = member.getPermission();
        this.gatherId = member.getGatherId();
        this.title = member.getGather().getTitle();
    }
}
