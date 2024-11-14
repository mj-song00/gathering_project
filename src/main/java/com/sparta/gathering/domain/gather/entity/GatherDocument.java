package com.sparta.gathering.domain.gather.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

@Getter
@Document(indexName="gather")
@NoArgsConstructor
@Setting(settingPath = "/resources/elastic/gather-setting.json")
public class GatherDocument {
    @Id
    private Long id;

    @Field(type= FieldType.Text)
    private String title;

    @Field(type= FieldType.Text)
    private String description;

    @Field(type= FieldType.Date)
    private LocalDateTime deletedAt;


    @Builder
    public GatherDocument(Long id, String title, String description, LocalDateTime deletedAt){
        this.id = id;
        this.title = title;
        this.description = description;
        this.deletedAt = deletedAt;
    }

    public static GatherDocument from (Gather gather){
        return GatherDocument.builder()
                .id(gather.getId())
                .title(gather.getTitle())
                .description(gather.getDescription())
                .deletedAt(gather.getDeletedAt())
                .build();
    }
}
