package com.HabeshaTreasure.HabeshaTreasure.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChapaPaymentService {

    @Value("${chapa.secretKey}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String initiatePayment(String email, String firstName, String lastName, double amount, String phoneNumber) {
        String txRef = UUID.randomUUID().toString();
        String returnUrl = "http://localhost:3000/payment/verify?tx_ref=" + txRef;

        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", amount);
        payload.put("currency", "ETB");
        payload.put("email", email);
        payload.put("first_name", firstName);
        payload.put("last_name", lastName);
        payload.put("tx_ref", txRef);
        payload.put("callback_url", returnUrl);
        payload.put("return_url", returnUrl);
        if (phoneNumber != null) payload.put("phone_number", phoneNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        String url = "https://api.chapa.co/v1/transaction/initialize";
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        System.out.println("Chapa Response: " + response.getBody());


        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            return data.get("checkout_url").toString();
        }

        throw new RuntimeException("Chapa initiation failed");
    }

    public ResponseEntity<?> verifyPayment(String txRef) {
        String url = "https://api.chapa.co/v1/transaction/verify/" + txRef;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            String status = (String) responseBody.get("status");

            if ("success".equalsIgnoreCase(status)) {
                // ✅ Optional: Save payment/order here
                return ResponseEntity.ok(responseBody);
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", "error", "message", "Verification failed"));
    }

}

