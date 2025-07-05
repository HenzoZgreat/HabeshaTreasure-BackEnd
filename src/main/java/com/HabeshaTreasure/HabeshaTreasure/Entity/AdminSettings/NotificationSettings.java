package com.HabeshaTreasure.HabeshaTreasure.Entity.AdminSettings;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class NotificationSettings {
    private String adminOrderEmail;
}
