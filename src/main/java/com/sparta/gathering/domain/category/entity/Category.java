package com.sparta.gathering.domain.category.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.category.dto.request.CategoryReq;
import com.sparta.gathering.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "category")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE category SET deleted_at = NOW() WHERE id = ?")
public class Category extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    private String title;

    @ColumnDefault("NULL")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    private Category(String title, User user) {
        this.title = title;
        this.user = user;
    }

    public static Category from(CategoryReq categoryReq, User user) {
        return new Category(categoryReq.getTitle(), user);
    }


    public void updateDeleteAt() {
        this.deletedAt = LocalDateTime.now();
    }

}
