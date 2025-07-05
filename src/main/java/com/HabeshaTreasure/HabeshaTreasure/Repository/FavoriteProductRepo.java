package com.HabeshaTreasure.HabeshaTreasure.Repository;

import com.HabeshaTreasure.HabeshaTreasure.Entity.Favorites.FavoriteProduct;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Favorites.FavoriteProductId;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Products;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteProductRepo extends JpaRepository<FavoriteProduct, FavoriteProductId> {
    List<FavoriteProduct> findByUser(User user);

    @Query("SELECT f.product FROM FavoriteProduct f WHERE f.user = :user")
    List<Products> findFavoriteProductsByUser(@Param("user") User user);

    boolean existsByUserAndProduct(User user, Products product);
    void deleteByUserAndProduct(User user, Products product);
    void deleteByUser(User user);
    void deleteByProduct(Products product);

}

