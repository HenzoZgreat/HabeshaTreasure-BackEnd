package com.HabeshaTreasure.HabeshaTreasure.Service;


import com.HabeshaTreasure.HabeshaTreasure.Entity.AdminSettings.*;
import com.HabeshaTreasure.HabeshaTreasure.Repository.SettingRepository;
import com.HabeshaTreasure.HabeshaTreasure.Entity.NotificationType;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class SettingService {

    @Autowired
    private final SettingRepository settingRepository;
    @Autowired
    private final NotificationService notificationService;


    public Setting getSettings() {
        return settingRepository.findAll().stream().findFirst().orElse(null);
    }

    public Setting updateSettings(Setting incoming) {
        Setting existing = getSettings();
        if (existing == null) {
            Setting created = settingRepository.save(incoming);

            // 🔔 Notify new settings created
            notificationService.createNotification(
                    "Initial settings were created by admin.",
                    NotificationType.SETTINGS,
                    null
            );
            return created;
        }

        List<String> changedFields = new ArrayList<>();

        // ===== STORE INFO =====
        if (incoming.getStoreInfo() != null) {
            if (existing.getStoreInfo() == null) existing.setStoreInfo(new StoreInfo());

            if (incoming.getStoreInfo().getName() != null &&
                    !incoming.getStoreInfo().getName().equals(existing.getStoreInfo().getName())) {
                existing.getStoreInfo().setName(incoming.getStoreInfo().getName());
                changedFields.add("Store Name");
            }
            if (incoming.getStoreInfo().getExchangeRate() != null &&
                    !incoming.getStoreInfo().getExchangeRate().equals(existing.getStoreInfo().getExchangeRate())) {
                existing.getStoreInfo().setExchangeRate(incoming.getStoreInfo().getExchangeRate());
                changedFields.add("Exchange Rate");
            }
            if (incoming.getStoreInfo().getPhone() != null &&
                    !incoming.getStoreInfo().getPhone().equals(existing.getStoreInfo().getPhone())) {
                existing.getStoreInfo().setPhone(incoming.getStoreInfo().getPhone());
                changedFields.add("Store Phone");
            }
            if (incoming.getStoreInfo().getEmail() != null &&
                    !incoming.getStoreInfo().getEmail().equals(existing.getStoreInfo().getEmail())) {
                existing.getStoreInfo().setEmail(incoming.getStoreInfo().getEmail());
                changedFields.add("Store Email");
            }
            if (incoming.getStoreInfo().getCurrency() != null &&
                    !incoming.getStoreInfo().getCurrency().equals(existing.getStoreInfo().getCurrency())) {
                existing.getStoreInfo().setCurrency(incoming.getStoreInfo().getCurrency());
                changedFields.add("Currency");
            }
        }

        // ===== PAYMENT =====
        if (incoming.getPayment() != null) {
            if (existing.getPayment() == null) existing.setPayment(new PaymentInfo());

            if (incoming.getPayment().getAccountName() != null &&
                    !incoming.getPayment().getAccountName().equals(existing.getPayment().getAccountName())) {
                existing.getPayment().setAccountName(incoming.getPayment().getAccountName());
                changedFields.add("Bank Account Name");
            }
            if (incoming.getPayment().getAccountNumber() != null &&
                    !incoming.getPayment().getAccountNumber().equals(existing.getPayment().getAccountNumber())) {
                existing.getPayment().setAccountNumber(incoming.getPayment().getAccountNumber());
                changedFields.add("Bank Account Number");
            }
            if (incoming.getPayment().getBankName() != null &&
                    !incoming.getPayment().getBankName().equals(existing.getPayment().getBankName())) {
                existing.getPayment().setBankName(incoming.getPayment().getBankName());
                changedFields.add("Bank Name");
            }
        }

        // ===== SHIPPING =====
        if (incoming.getShipping() != null) {
            if (existing.getShipping() == null) existing.setShipping(new ShippingInfo());

            if (incoming.getShipping().getFreeShippingThreshold() != null &&
                    !incoming.getShipping().getFreeShippingThreshold().equals(existing.getShipping().getFreeShippingThreshold())) {
                existing.getShipping().setFreeShippingThreshold(incoming.getShipping().getFreeShippingThreshold());
                changedFields.add("Free Shipping Threshold");
            }
        }

        // ===== NOTIFICATIONS =====
        if (incoming.getNotifications() != null) {
            if (existing.getNotifications() == null) existing.setNotifications(new NotificationSettings());

            if (incoming.getNotifications().getAdminOrderEmail() != null &&
                    !incoming.getNotifications().getAdminOrderEmail().equals(existing.getNotifications().getAdminOrderEmail())) {
                existing.getNotifications().setAdminOrderEmail(incoming.getNotifications().getAdminOrderEmail());
                changedFields.add("Admin Order Email");
            }
        }

        Setting updated = settingRepository.save(existing);

        // 🔔 Only notify if changes were made
        if (!changedFields.isEmpty()) {
            String message = "Settings updated: " + String.join(", ", changedFields);
            notificationService.createNotification(message, NotificationType.SETTINGS, null);
        }

        return updated;
    }



}

