package com.HabeshaTreasure.HabeshaTreasure.Entity.AdminSettings;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "settings")
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private StoreInfo storeInfo;

    @Embedded
    private PaymentInfo payment;

    @Embedded
    private ShippingInfo shipping;

    @Embedded
    private NotificationSettings notifications;
}
