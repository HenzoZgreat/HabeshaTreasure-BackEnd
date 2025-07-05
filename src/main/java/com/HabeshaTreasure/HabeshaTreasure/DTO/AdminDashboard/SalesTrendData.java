package com.HabeshaTreasure.HabeshaTreasure.DTO.AdminDashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class SalesTrendData {
    private List<String> labels;
    private List<Map<String, Object>> datasets;
}