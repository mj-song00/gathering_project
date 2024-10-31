package com.sparta.gathering.domain.category.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.category.dto.request.CategoryReq;
import com.sparta.gathering.domain.user.entity.User;
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
@Table(name = "category")
@Filter(name = "deleted", condition = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE category SET deleted_at = NOW() WHERE id = ?")
public class Category extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;

    @ColumnDefault("NULL")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JsonIgnore  // 직렬화 방지를 위해 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Category(String categoryName, User user) {
        this.categoryName = categoryName;
        this.user = user;
    }

    public static Category from(CategoryReq categoryReq, User user) {
        return new Category(categoryReq.getCategoryName(), user);
    }

    public void updateCategory(String categoryName, User user) {
        this.categoryName = categoryName;
        this.user = user;
    }

    // 논리 삭제
    public void updateDeleteAt() {
        this.deletedAt = LocalDateTime.now();
    }
}
