package com.HabeshaTreasure.HabeshaTreasure.Controller;

import com.HabeshaTreasure.HabeshaTreasure.DTO.UserOrderResponseDTO;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import com.HabeshaTreasure.HabeshaTreasure.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderService orderService;

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            orderService.cancelOrder(id, user);
            return ResponseEntity.ok("Order cancelled");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found or not yours");
        }
    }


    @PostMapping
    public ResponseEntity<?> placeOrder(@AuthenticationPrincipal User user) {
        try {
            Long orderId = orderService.placeOrder(user);
            return ResponseEntity.ok(Map.of("orderId", orderId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }
    }

    @PostMapping("/{id}/upload-proof")
    public ResponseEntity<?> uploadProof(@PathVariable Long id,
                                         @RequestParam("file") MultipartFile file,
                                         @AuthenticationPrincipal User user) {
        try {
            orderService.uploadPaymentProof(id, user, file);
            return ResponseEntity.ok("Proof uploaded successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found or not yours");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    @GetMapping
    public ResponseEntity<List<UserOrderResponseDTO>> getMyOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getOrdersForUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserOrderResponseDTO> getOrderDetail(@PathVariable Long id,
                                                               @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(orderService.getOrderByIdForUser(id, user));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/proof")
    public ResponseEntity<byte[]> getProof(@PathVariable Long id,
                                           @AuthenticationPrincipal User user) {
        byte[] image = orderService.getPaymentProof(id, user);
        if (image == null) return ResponseEntity.noContent().build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // or IMAGE_PNG
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
