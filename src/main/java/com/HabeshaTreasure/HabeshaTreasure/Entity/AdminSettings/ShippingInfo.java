package com.HabeshaTreasure.HabeshaTreasure.Entity.AdminSettings;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ShippingInfo {
    private Double freeShippingThreshold;
}
