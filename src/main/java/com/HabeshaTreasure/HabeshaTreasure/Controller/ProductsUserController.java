package com.HabeshaTreasure.HabeshaTreasure.Controller;

import com.HabeshaTreasure.HabeshaTreasure.DTO.ReviewResponseDTO;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Products;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import com.HabeshaTreasure.HabeshaTreasure.Service.FavoriteProductService;
import com.HabeshaTreasure.HabeshaTreasure.Service.ProductsService;
import com.HabeshaTreasure.HabeshaTreasure.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/user/products")
public class ProductsUserController {

    @Autowired
    private ProductsService productsService;
    @Autowired
    private FavoriteProductService favoriteService;
    @Autowired
    private ReviewService reviewService;


    @GetMapping
    public ResponseEntity<List<Products>> getAllProducts() {
        return ResponseEntity.ok(productsService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(productsService.getProductById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }


//===================================================================================

    // Favorites
    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in.");
        }
        return ResponseEntity.ok(favoriteService.getFavorites(user));
    }

    @GetMapping("/{id}/is-favorited")
    public ResponseEntity<?> isFavorited(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        boolean isFav = favoriteService.isFavoritedBy(user, id);
        return ResponseEntity.ok(Map.of("favorited", isFav));
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> favorite(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        favoriteService.addToFavorites(user, id);
        return ResponseEntity.ok("Favorited");
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<?> unfavorite(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        favoriteService.removeFromFavorites(user, id);
        return ResponseEntity.ok("Unfavorited");
    }



    // Reviews
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getReviews(@PathVariable Integer id) {
        return ResponseEntity.ok(reviewService.getReviewsForProduct(id));
    }


    @PostMapping("/{id}/review")
    public ResponseEntity<?> review(@PathVariable Integer id,
                                    @RequestBody Map<String, Object> body,
                                    @AuthenticationPrincipal User user) {
        int rating = (int) body.get("rating");
        String comment = (String) body.get("comment");
        reviewService.addOrUpdateReview(user, id, rating, comment);
        return ResponseEntity.ok("Review submitted");
    }



    @DeleteMapping("/{id}/review")
    public ResponseEntity<?> deleteReview(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        reviewService.deleteReview(user, id);
        return ResponseEntity.ok("Review deleted");
    }

    @GetMapping("/distinct/categories")
    public ResponseEntity<List<String>> getDistinctCategories() {
        return ResponseEntity.ok(productsService.getDistinctCategories());
    }


}
