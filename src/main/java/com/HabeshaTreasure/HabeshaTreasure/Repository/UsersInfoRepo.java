package com.HabeshaTreasure.HabeshaTreasure.Repository;

import com.HabeshaTreasure.HabeshaTreasure.Entity.UsersInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersInfoRepo extends JpaRepository<UsersInfo, Integer> {
    UsersInfo findByUserId(Long userId);
    UsersInfo findByPhoneNumber(String phoneNumber);
}

