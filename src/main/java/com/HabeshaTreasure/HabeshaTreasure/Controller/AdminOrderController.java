package com.HabeshaTreasure.HabeshaTreasure.Controller;

import com.HabeshaTreasure.HabeshaTreasure.Entity.Orders.OrderStatus;
import com.HabeshaTreasure.HabeshaTreasure.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;


    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            return ResponseEntity.ok(orderService.getAllOrders());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to fetch orders");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to fetch order");
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approvePayment(@PathVariable Long id) {
        try {
            orderService.setStatus(id, OrderStatus.PAID);
            return ResponseEntity.ok("Payment approved");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Approval failed");
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectPayment(@PathVariable Long id) {
        try {
            orderService.rejectOrder(id);
            return ResponseEntity.ok("Payment rejected");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Rejection failed");
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody Map<String, String> body) {
        try {
            String statusStr = body.get("status");
            if (statusStr == null) {
                return ResponseEntity.badRequest().body("Missing 'status' field");
            }

            OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());

            if (EnumSet.of(OrderStatus.PENDING_PAYMENT, OrderStatus.CANCELLED, OrderStatus.REJECTED)
                    .contains(status)) {
                return ResponseEntity.badRequest().body("Invalid status for admin update");
            }

            orderService.setStatus(id, status);
            return ResponseEntity.ok("Status updated to " + status);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid order status");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update status");
        }
    }
    @GetMapping("/{id}/proof")
    public ResponseEntity<?> downloadProof(@PathVariable Long id) {
        try {
            byte[] image = orderService.getPaymentProof(id);
            if (image == null || image.length == 0) {
                return ResponseEntity.noContent().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(image, headers, HttpStatus.OK);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to load image");
        }
    }


}

