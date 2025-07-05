package com.HabeshaTreasure.HabeshaTreasure.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDTO {
    private Integer productId;
    private String productName;
    private Double price;
    private String image;
    private Integer quantity;
    private LocalDateTime addedAt;
}