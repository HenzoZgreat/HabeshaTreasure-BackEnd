package com.HabeshaTreasure.HabeshaTreasure.Controller;

import com.HabeshaTreasure.HabeshaTreasure.Service.ChapaPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ChapaPaymentService chapaPaymentService;

    @PostMapping("/initiate-chapa")
    public ResponseEntity<?> initiatePayment(
            @RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam double amount,
            @RequestParam(required = false) String phoneNumber
    ) {
        try {
            String checkoutUrl = chapaPaymentService.initiatePayment(email, firstName, lastName, amount, phoneNumber);
            return ResponseEntity.ok(Map.of("checkout_url", checkoutUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> requestBody) {
        String txRef = requestBody.get("tx_ref");

        if (txRef == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "tx_ref is required"));
        }

        return chapaPaymentService.verifyPayment(txRef);
    }
}
