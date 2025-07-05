package com.HabeshaTreasure.HabeshaTreasure.DTO;

import lombok.Data;

@Data
public class ProductRequestDTO {
    private Integer id;
    private String name;
    private String image;
    private String category;
    private Double price;
    private Integer stock;
    private String status;
    private String descriptionEn;
    private String descriptionAm;
    private Boolean isFeatured;
    private Double rate;
    private Integer count;
}


