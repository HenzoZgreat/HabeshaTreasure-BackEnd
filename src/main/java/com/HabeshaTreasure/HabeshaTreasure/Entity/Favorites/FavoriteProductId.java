package com.HabeshaTreasure.HabeshaTreasure.Entity.Favorites;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteProductId implements Serializable {
    private Long userId;
    private Integer productId;
}
