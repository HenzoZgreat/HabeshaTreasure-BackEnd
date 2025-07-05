package com.HabeshaTreasure.HabeshaTreasure.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemRequestDTO {
    private Integer productId;
    private Integer quantity;
}