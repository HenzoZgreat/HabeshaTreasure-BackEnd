package com.HabeshaTreasure.HabeshaTreasure.Entity.AdminSettings;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class PaymentInfo {
    private String accountName;
    private String accountNumber;
    private String bankName;
}
