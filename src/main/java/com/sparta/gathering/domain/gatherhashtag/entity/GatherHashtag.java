package com.sparta.gathering.domain.gatherhashtag.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.hashtag.entity.HashTag;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "gather_hashtag")
@Getter
public class GatherHashtag extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gather_id")
    private Gather gather;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private HashTag hashTag;

    private LocalDateTime createdAt;

    public GatherHashtag(Gather gather, HashTag hashTag) {
        this.gather = gather;
        this.hashTag = hashTag;
        this.createdAt = LocalDateTime.now();
    }

    public static GatherHashtag of(Gather gather, HashTag hashTag) {
        return new GatherHashtag(gather, hashTag);
    }
}
