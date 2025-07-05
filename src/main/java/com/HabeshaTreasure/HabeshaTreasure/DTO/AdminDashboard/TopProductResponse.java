package com.HabeshaTreasure.HabeshaTreasure.DTO.AdminDashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductResponse {
    private String name;
    private String image;
    private int sales;
}
