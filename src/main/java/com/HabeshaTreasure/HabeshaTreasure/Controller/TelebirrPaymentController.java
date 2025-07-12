package com.HabeshaTreasure.HabeshaTreasure.Controller;

import com.HabeshaTreasure.HabeshaTreasure.DTO.TelebirrTransactionDetails;
import com.HabeshaTreasure.HabeshaTreasure.Service.TelebirrVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class TelebirrPaymentController {

    private final TelebirrVerificationService telebirrService;

    @PostMapping("/verify-telebirr")
    public ResponseEntity<?> verifyTelebirr(@RequestBody Map<String, String> body) {
        String trxId = body.get("transactionId");

        if (trxId == null || trxId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Transaction ID is required"));
        }

        try {
            TelebirrTransactionDetails details = telebirrService.verify(trxId);
            return ResponseEntity.ok(details);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Could not fetch receipt"));
        }
    }
}
