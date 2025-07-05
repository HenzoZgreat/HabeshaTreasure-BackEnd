package com.HabeshaTreasure.HabeshaTreasure.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private String name;
    private Double price;
    private String image;
    private Integer quantity;
}
