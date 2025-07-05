package com.HabeshaTreasure.HabeshaTreasure.Controller;

import com.HabeshaTreasure.HabeshaTreasure.DTO.CartItemRequestDTO;
import com.HabeshaTreasure.HabeshaTreasure.DTO.CartItemResponseDTO;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import com.HabeshaTreasure.HabeshaTreasure.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/user/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCart(@AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(cartService.getCartItems(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch cart: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@AuthenticationPrincipal User user,
                                       @RequestBody CartItemRequestDTO request) {
        try {
            cartService.addOrUpdateItem(user, request.getProductId(), request.getQuantity());
            return ResponseEntity.ok("Item added/updated in cart");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add/update item: " + e.getMessage());
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@AuthenticationPrincipal User user,
                                            @RequestBody Map<String, Integer> body) {
        try {
            Integer productId = body.get("productId");
            cartService.removeItem(user, productId);
            return ResponseEntity.ok("Item removed from cart");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to remove item: " + e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal User user) {
        try {
            cartService.clearCart(user);
            return ResponseEntity.ok("Cart cleared");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to clear cart: " + e.getMessage());
        }
    }
}

