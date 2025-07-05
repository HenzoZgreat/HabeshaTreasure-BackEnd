package com.HabeshaTreasure.HabeshaTreasure.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String image;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "date_added", nullable = false)
    private LocalDate dateAdded;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_am", columnDefinition = "TEXT")
    private String descriptionAm;

    @Column(nullable = false)
    private Integer favorites = 0;

    @Column(nullable = false)
    private Double rate = 0.0;

    @Column(nullable = false)
    private Integer count = 0;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

}
