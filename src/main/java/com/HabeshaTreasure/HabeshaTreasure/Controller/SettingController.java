package com.HabeshaTreasure.HabeshaTreasure.Controller;


import com.HabeshaTreasure.HabeshaTreasure.Entity.AdminSettings.Setting;
import com.HabeshaTreasure.HabeshaTreasure.Service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    public ResponseEntity<Setting> getSettings() {
        Setting setting = settingService.getSettings();
        return ResponseEntity.ok(setting);
    }

    @PutMapping
    public ResponseEntity<Setting> updateSettings(@RequestBody Setting updated) {
        return ResponseEntity.ok(settingService.updateSettings(updated));
    }
}

