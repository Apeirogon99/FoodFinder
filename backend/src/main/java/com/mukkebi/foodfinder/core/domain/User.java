package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.enums.UserStatus;
import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "github_id", nullable = false, unique = true)
    private String githubId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPreference> preferences = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAllergy> allergies = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Builder
    public User(String githubId, String nickname, List<UserPreference> preferences, List<UserAllergy> allergies, UserStatus userStatus) {
        this.githubId = githubId;
        this.nickname = nickname;
        this.preferences = preferences;
        this.allergies = allergies;
        this.userStatus = userStatus;
    }

    public void completeSignup() {
        this.userStatus = UserStatus.ACTIVE;
    }
}

