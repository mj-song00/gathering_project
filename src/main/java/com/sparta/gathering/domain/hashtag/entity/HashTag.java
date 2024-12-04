package com.sparta.gathering.domain.hashtag.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gatherhashtag.entity.GatherHashtag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "hashtag")
@SQLDelete(sql = "UPDATE `hashtag` SET deleted_at = NOW() WHERE id = ?")
public class HashTag extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hashTagName;

    @ColumnDefault("NULL")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "hashTag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GatherHashtag> gatherHashTags = new ArrayList<>();

    public HashTag(String hashTagName) {
        this.hashTagName = hashTagName;
    }

    public static HashTag of(String hashTagName, Gather gather) {
        HashTag hashTag = new HashTag();
        hashTag.hashTagName = hashTagName;
        return hashTag;
    }

    public void updateDeleteAt() {
        this.deletedAt = LocalDateTime.now();
    }

}