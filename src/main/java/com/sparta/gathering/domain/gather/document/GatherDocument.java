package com.sparta.gathering.domain.gather.document;

import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gatherhashtag.entity.GatherHashtag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Document(indexName = "gathers")
@Builder
public class GatherDocument {
    @Id
    @Field(type = FieldType.Keyword)
    private Long gatherId;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String category;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String address;

    @Field(type = FieldType.Nested)
    private List<GatherHashtag> gatherHashtags = new ArrayList<>();


    public GatherDocument(Long gatherId, String title, String category, String description, String address, List<GatherHashtag> gatherHashtags) {
        this.gatherId = gatherId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.address = address;
        this.gatherHashtags = gatherHashtags;
    }

    public static GatherDocument from(Gather gather) {
        return GatherDocument.builder()
                .gatherId(gather.getId())
                .title(gather.getTitle())
                .category(gather.getCategory().getCategoryName())
                .description(gather.getDescription())
                .address(gather.getMap().getAddressName())
                .gatherHashtags(gather.getGatherHashtags()) // 해시태그 이름 리스트로 변환
                .build();
    }
}
