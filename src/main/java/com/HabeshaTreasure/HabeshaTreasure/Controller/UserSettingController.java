package com.HabeshaTreasure.HabeshaTreasure.Controller;

import com.HabeshaTreasure.HabeshaTreasure.Entity.AdminSettings.Setting;
import com.HabeshaTreasure.HabeshaTreasure.Service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/settings")
@RequiredArgsConstructor
public class UserSettingController {
    private final SettingService settingService;

    @GetMapping
    public ResponseEntity<Setting> getSettings() {
        Setting setting = settingService.getSettings();
        return ResponseEntity.ok(setting);
    }
}
