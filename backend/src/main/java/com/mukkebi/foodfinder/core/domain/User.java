package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.enums.UserStatus;
import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.*;
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Builder
    public User(String githubId, String nickname, UserStatus userStatus) {
        this.githubId = githubId;
        this.nickname = nickname;
        this.userStatus = userStatus;
    }

    public void completeSignup() {
        this.userStatus = UserStatus.ACTIVE;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
}

