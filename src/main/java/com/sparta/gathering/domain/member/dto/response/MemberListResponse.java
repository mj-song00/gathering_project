package com.sparta.gathering.domain.member.dto.response;

import com.sparta.gathering.domain.member.entity.Member;
import lombok.Getter;

import java.util.List;

@Getter
public class MemberListResponse {
    private List<MemberListResponseItem> members;
    private int currentPage;
    private int totalPages;
    private long totalElements;

    public MemberListResponse(List<Member> members, int currentPage, int totalPages, long totalElements) {
        this.members = members.stream()
                .map(MemberListResponseItem::new)
                .toList();
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
