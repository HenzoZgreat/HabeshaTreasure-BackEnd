package com.HabeshaTreasure.HabeshaTreasure.Service;

import com.HabeshaTreasure.HabeshaTreasure.Entity.Favorites.FavoriteProduct;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Favorites.FavoriteProductId;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Products;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import com.HabeshaTreasure.HabeshaTreasure.Repository.FavoriteProductRepo;
import com.HabeshaTreasure.HabeshaTreasure.Repository.ProductsRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FavoriteProductService {

    private final FavoriteProductRepo favoriteRepo;
    private final ProductsRepo productsRepo;

    public void addToFavorites(User user, Integer productId) {
        Products product = productsRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        if (!favoriteRepo.existsByUserAndProduct(user, product)) {
            FavoriteProduct fav = new FavoriteProduct(
                    new FavoriteProductId(user.getId(), product.getId()),
                    user,
                    product,
                    LocalDateTime.now()
            );
            favoriteRepo.save(fav);

            // 🔁 Update count
            product.setFavorites(product.getFavorites() + 1);
            productsRepo.save(product);
        }
    }

    @Transactional
    public void removeFromFavorites(User user, Integer productId) {
        Products product = productsRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        if (favoriteRepo.existsByUserAndProduct(user, product)) {
            favoriteRepo.deleteByUserAndProduct(user, product);
            product.setFavorites(Math.max(0, product.getFavorites() - 1));
            productsRepo.save(product);
        }
    }

    public boolean isFavoritedBy(User user, Integer productId) {
        Products product = productsRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
        return favoriteRepo.existsByUserAndProduct(user, product);
    }


    public List<Products> getFavorites(User user) {
        return favoriteRepo.findFavoriteProductsByUser(user);
    }

}
