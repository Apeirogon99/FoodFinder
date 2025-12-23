package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "github_id", nullable = false, unique = true)
    private String githubId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Builder
    public User(String githubId, String nickname) {
        this.githubId = githubId;
        this.nickname = nickname;
    }
}

