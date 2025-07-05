package com.HabeshaTreasure.HabeshaTreasure.Repository;

import com.HabeshaTreasure.HabeshaTreasure.Entity.Products;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Review;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {
    Optional<Review> findByUserAndProduct(User user, Products product);
    List<Review> findByProduct(Products product);
    void deleteByUserAndProduct(User user, Products product);
    void deleteByUser(User user);
    void deleteByProduct(Products product);

}