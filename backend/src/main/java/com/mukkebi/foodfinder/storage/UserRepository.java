package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByGithubId(String githubId);
}

