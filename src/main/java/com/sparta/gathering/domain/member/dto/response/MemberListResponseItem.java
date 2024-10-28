package com.sparta.gathering.domain.member.dto.response;

import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import lombok.Getter;

@Getter
public class MemberListResponseItem {
    private Long id;
    private String Nickname;
    private Permission permission;
    private String profileImage;

    public MemberListResponseItem(Member member){
        this.id= member.getId();
        this.Nickname = member.getUser().getNickName();
        this.permission = member.getPermission();
        this.profileImage = member.getUser().getProfileImage();
    }
}
