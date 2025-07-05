package com.HabeshaTreasure.HabeshaTreasure.Repository;


import com.HabeshaTreasure.HabeshaTreasure.Entity.AdminSettings.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, Long> {
}
