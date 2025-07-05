package com.HabeshaTreasure.HabeshaTreasure.Service;

import com.HabeshaTreasure.HabeshaTreasure.DTO.CartItemResponseDTO;
import com.HabeshaTreasure.HabeshaTreasure.Entity.CartItem;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Products;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import com.HabeshaTreasure.HabeshaTreasure.Repository.CartItemRepo;
import com.HabeshaTreasure.HabeshaTreasure.Repository.ProductsRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepo cartRepo;
    @Autowired
    private ProductsRepo productsRepo;

    public List<CartItemResponseDTO> getCartItems(User user) {
        return cartRepo.findByUser(user).stream()
                .map(item -> new CartItemResponseDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getProduct().getImage(),
                        item.getQuantity(),
                        item.getAddedAt()
                ))
                .toList();
    }

    public void addOrUpdateItem(User user, Integer productId, int quantity) {
        Products product = productsRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        CartItem item = cartRepo.findByUserAndProduct(user, product)
                .orElse(new CartItem(null, user, product, 0, LocalDateTime.now()));

        item.setQuantity(item.getQuantity() + quantity);
        item.setAddedAt(LocalDateTime.now());
        cartRepo.save(item);
    }

    public void removeItem(User user, Integer productId) {
        Products product = productsRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
        cartRepo.deleteByUserAndProduct(user, product);
    }

    public void clearCart(User user) {
        cartRepo.deleteByUser(user);
    }

    public void deleteItemsByProduct(Products product) {
        cartRepo.deleteByProduct(product);
    }
}

