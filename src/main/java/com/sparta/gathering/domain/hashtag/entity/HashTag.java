package com.sparta.gathering.domain.hashtag.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.gather.entity.Gather;
import jakarta.persistence.Column;
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
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "hashtag")
@Filter(name = "deleted", condition = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE `hashtag` SET deleted_at = NOW() WHERE id = ?")
public class HashTag extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hashTagName;

    @ColumnDefault("NULL")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gather_id")
    private Gather gather;

    private HashTag(String hashTagName, Gather gather) {
        this.hashTagName = hashTagName;
        this.gather = gather;
    }

    public static HashTag from(String hashTagName, Gather gather) {
        return new HashTag(hashTagName, gather);
    }

    public static HashTag of(String hashTagName, Gather gather) {
        return new HashTag(hashTagName, gather);
    }

    public void updateDeleteAt() {
        this.deletedAt = LocalDateTime.now();
    }

}