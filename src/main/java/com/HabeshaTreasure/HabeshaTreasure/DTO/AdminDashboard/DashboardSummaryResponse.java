package com.HabeshaTreasure.HabeshaTreasure.DTO.AdminDashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DashboardSummaryResponse {
    private TrendValue totalRevenue;
    private TrendValue totalOrders;
    private TrendValue newCustomers;
    private TrendValue pendingOrders;
    private SalesTrendData salesTrendData;
    private List<TopProductResponse> topProducts;
}
