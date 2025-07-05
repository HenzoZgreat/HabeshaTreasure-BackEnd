package com.HabeshaTreasure.HabeshaTreasure.Entity.AdminSettings;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class StoreInfo {
    private String name;
    private Double exchangeRate;
    private String phone;
    private String email;
    private String currency;
}
