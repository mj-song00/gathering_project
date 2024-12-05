package com.sparta.gathering.domain.gather.document;

import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gatherhashtag.entity.GatherHashtag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Document(indexName = "gathers")
//@Setting(settingPath = "/resources/elastic/gather-setting.json")
public class GatherDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String title;

    @Field(type = FieldType.Keyword)
    private Category category;

    private String description;
    private String address;
    private List<GatherHashtag> gatherHashtags = new ArrayList<>();

    @Builder
    public GatherDocument(Long id, String title, Category category, String description, String address, List<GatherHashtag> gatherhashtags){
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.address = address;
        this.gatherHashtags=  gatherhashtags;

    }
    public static GatherDocument from (Gather gather){
        return GatherDocument.builder()
                .id(gather.getId())
                .title(gather.getTitle())
                .category(gather.getCategory())
                .description(gather.getDescription())
                .address(gather.getMap().getAddressName())
                .gatherhashtags(gather.getGatherHashtags())
                .build();
    }
}
