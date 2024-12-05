package com.sparta.gathering.domain.gather.dto.response;

import com.sparta.gathering.domain.gather.document.GatherDocument;
import com.sparta.gathering.domain.gather.entity.Gather;
import lombok.Getter;

import java.util.List;

@Getter
public class ElasticRespones {
    private final List<ElasticResponseItem> list; // Gather 아이템 리스트
    private final int currentPage; // 현재 페이지 번호
    private final int totalPages; // 총 페이지 수
    private final long totalElements; // 총 요소 수

    public ElasticRespones(List<GatherDocument> gathers, int currentPage, int totalPages, long totalElements) {
        this.list = gathers.stream()
                .map(ElasticResponseItem::new) // Gather를 GatherListResponseItem으로 변환
                .toList();
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
