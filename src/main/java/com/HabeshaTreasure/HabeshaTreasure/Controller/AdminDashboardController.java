package com.HabeshaTreasure.HabeshaTreasure.Controller;

import com.HabeshaTreasure.HabeshaTreasure.DTO.AdminDashboard.DashboardSummaryResponse;
import com.HabeshaTreasure.HabeshaTreasure.DTO.AdminDashboard.TopProductResponse;
import com.HabeshaTreasure.HabeshaTreasure.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    @Autowired
    private final DashboardService dashboardService;

    @PostMapping("/overview")
    public ResponseEntity<?> getDashboardOverview(@RequestBody Map<String, Object> request) {
        try {
            String currency = (String) request.getOrDefault("currency", "USD");
            String endDate = (String) request.getOrDefault("endDate", null);
            int rangeDays = (int) request.getOrDefault("rangeDays", 7);

            DashboardSummaryResponse summary = dashboardService.getDashboardSummary(currency, endDate, rangeDays);
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/products/top")
    public ResponseEntity<?> getTopProducts(@RequestBody Map<String, Object> request) {
        try {
            int limit = (int) request.getOrDefault("limit", 3);
            List<TopProductResponse> topProducts = dashboardService.getTopProducts(limit);
            return ResponseEntity.ok(topProducts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch top products"));
        }
    }

    @GetMapping("/recent-orders")
    public ResponseEntity<?> getRecentOrders() {
        try {
            return ResponseEntity.ok(dashboardService.getRecentOrders(3));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to load recent orders");
        }
    }
}
