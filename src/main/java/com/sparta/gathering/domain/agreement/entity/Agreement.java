package com.sparta.gathering.domain.agreement.entity;

import com.sparta.gathering.common.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Agreement")
@NoArgsConstructor
public class Agreement extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String content;        // 약관 내용

    @Column(nullable = false)
    private String version;        // 약관 버전 정보


    public Agreement(String content, String version) {
        this.content = content;
        this.version = version;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
        this.setUpdatedAt(LocalDateTime.now());
    }
}
