package com.HabeshaTreasure.HabeshaTreasure.Service;

import com.HabeshaTreasure.HabeshaTreasure.DTO.ProductRequestDTO;
import com.HabeshaTreasure.HabeshaTreasure.Entity.NotificationType;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Products;
import com.HabeshaTreasure.HabeshaTreasure.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductsService {

    @Autowired
    private ProductsRepo productsRepo;
    @Autowired
    private CartService cartService;
    @Autowired
    private FavoriteProductRepo favoriteRepo;
    @Autowired
    private ReviewRepo reviewRepo;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CartItemRepo cartItemRepository;
    @Autowired
    private OrderRepo orderRepository;
    @Autowired
    private OrderItemRepo orderItemRepository;
    @Autowired
    private NotificationRepository notificationRepository;


    public void updateProductStatusByStock(Products product) {
        int stock = product.getStock();

        String status = switch (stock) {
            case 0 -> "Out of Stock";
            default -> (stock <= 10) ? "Low Stock" : "Active";
        };

        product.setStatus(status);

        if (status.equals("Low Stock")) {
            notificationService.createNotification("Low stock alert: " + product.getName(), NotificationType.STOCK, null);
        }

    }

    public List<Products> getAllProducts() {
        return productsRepo.findAll();
    }

    public Products getProductById(Integer id) {
        return productsRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
    }

    public void createProduct(ProductRequestDTO dto) {
        Products product = mapToProduct(dto);
        product.setDateAdded(LocalDate.now());
        product.setFavorites(0);
        productsRepo.save(product);

        notificationService.createNotification("New product added: " + product.getName(), NotificationType.PRODUCT, null);


    }

    public void updateProduct(Integer id, ProductRequestDTO dto) {
        Products product = getProductById(id);
        product.setName(dto.getName());
        product.setImage(dto.getImage());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setStatus(dto.getStatus());
        product.setDescriptionEn(dto.getDescriptionEn());
        product.setDescriptionAm(dto.getDescriptionAm());
        product.setIsFeatured(dto.getIsFeatured());
        productsRepo.save(product);

        notificationService.createNotification("Product updated: " + product.getName(), NotificationType.PRODUCT, null);
    }

    @Transactional
    public void deleteProduct(Integer productId) {
        Products product = productsRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        // 🧹 1. Delete order items by productId (primitive)
        orderItemRepository.deleteByProductId(productId);

        // 🧹 2. Delete reviews for this product
        reviewRepo.deleteByProduct(product);

        // 🧹 3. Delete favorite_products entries for this product
        favoriteRepo.deleteByProduct(product);

        // 🧹 4. Delete the product itself
        productsRepo.delete(product);

        // 🔔 Notify
        notificationService.createNotification(
                "Product deleted: " + product.getName(),
                NotificationType.PRODUCT,
                null
        );
    }


    @Transactional
    public void deleteMultipleProducts(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Invalid product IDs");
        }

        List<Products> productsToDelete = productsRepo.findAllById(ids);
        for (Products product : productsToDelete) {
            cartService.deleteItemsByProduct(product);
            favoriteRepo.deleteByProduct(product);
            reviewRepo.deleteByProduct(product);
        }

        productsRepo.deleteAll(productsToDelete);

        for (Products product : productsToDelete) {
            notificationService.createNotification("Product deleted: " + product.getName(), NotificationType.PRODUCT, null);
        }
    }

    public int getFavoritesCount(Integer id) {
        return getProductById(id).getFavorites();
    }

    public void incrementFavorites(Integer id) {
        Products product = getProductById(id);
        product.setFavorites(product.getFavorites() + 1);
        productsRepo.save(product);
    }
    
    public void decrementFavorites(Integer id) {
        Products product = getProductById(id);
        if (product.getFavorites() > 0) {
            product.setFavorites(product.getFavorites() - 1);
            productsRepo.save(product);
        }
    }

    public void rateProduct(Integer productId, int newRating) {
        Products product = getProductById(productId);

        int count = product.getCount();
        double currentAvg = product.getRate();

        double newAvg = ((currentAvg * count) + newRating) / (count + 1);

        product.setCount(count + 1);
        product.setRate(newAvg);

        productsRepo.save(product);
    }

    public List<Products> getFeaturedProducts() {
        return productsRepo.findByIsFeaturedTrue();
    }

    public List<Products> getProductsByStatus(String status) {
        return productsRepo.findByStatusIgnoreCase(status);
    }

    public List<String> getDistinctCategories() {
        return productsRepo.findDistinctCategories();
    }

    public List<String> getDistinctStatuses() {
        return productsRepo.findDistinctStatuses();
    }


    private Products mapToProduct(ProductRequestDTO dto) {
        Products p = new Products();
        p.setName(dto.getName());
        p.setImage(dto.getImage());
        p.setCategory(dto.getCategory());
        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        p.setStatus(dto.getStatus());
        p.setDescriptionEn(dto.getDescriptionEn());
        p.setDescriptionAm(dto.getDescriptionAm());
        p.setIsFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false);
        return p;
    }

}


