package com.HabeshaTreasure.HabeshaTreasure.Entity.Favorites;

import com.HabeshaTreasure.HabeshaTreasure.Entity.Products;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteProduct {

    @EmbeddedId
    private FavoriteProductId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    private Products product;

    @Column(nullable = false)
    private LocalDateTime favoritedAt;
}
