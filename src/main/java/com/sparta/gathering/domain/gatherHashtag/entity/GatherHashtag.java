package com.sparta.gathering.domain.gatherHashtag.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.hashtag.entity.HashTag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "gatherHashtag")
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

   public GatherHashtag(Gather gather, HashTag hashTag){
       this.gather = gather;
       this.hashTag = hashTag;
       this.createdAt = LocalDateTime.now();
   }

    public static GatherHashtag of(Gather gather, HashTag hashTag) {
        return new GatherHashtag(gather, hashTag);
    }
}
