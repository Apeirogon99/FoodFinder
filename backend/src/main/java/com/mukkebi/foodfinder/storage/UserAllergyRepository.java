package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.domain.UserAllergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAllergyRepository extends JpaRepository<UserAllergy, Long> {

    List<UserAllergy> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
