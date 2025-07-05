package com.HabeshaTreasure.HabeshaTreasure.Service;

import com.HabeshaTreasure.HabeshaTreasure.DTO.AdminDashboard.DashboardSummaryResponse;
import com.HabeshaTreasure.HabeshaTreasure.DTO.AdminDashboard.SalesTrendData;
import com.HabeshaTreasure.HabeshaTreasure.DTO.AdminDashboard.TopProductResponse;
import com.HabeshaTreasure.HabeshaTreasure.DTO.AdminDashboard.TrendValue;
import com.HabeshaTreasure.HabeshaTreasure.DTO.AdminOrderResponseDTO;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Orders.Order;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Orders.OrderItem;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import com.HabeshaTreasure.HabeshaTreasure.Repository.OrderRepo;
import com.HabeshaTreasure.HabeshaTreasure.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private OrderRepo orderRepository;
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private OrderService orderService;

    private static final double USD_TO_ETB_RATE = 150.0;

    public DashboardSummaryResponse getDashboardSummary(String currency, String endDateStr, int rangeDays) {
        LocalDate endDate = endDateStr != null
                ? LocalDate.parse(endDateStr, DateTimeFormatter.ISO_DATE)
                : LocalDate.now();
        LocalDate startDate = endDate.minusDays(rangeDays);

        List<Order> allOrders = orderRepository.findAll();
        List<Order> recentOrders = allOrders.stream()
                .filter(o -> {
                    LocalDate orderDate = o.getOrderedAt().toLocalDate();
                    return !orderDate.isBefore(startDate);
                })
                .toList();

        List<User> allUsers = userRepository.findAll();
        List<User> recentUsers = allUsers.stream()
                .filter(u -> {
                    LocalDate joinDate = u.getUsersInfo().getJoined().toLocalDate();
                    return !joinDate.isBefore(startDate);
                })
                .toList();

        double totalRevenue = recentOrders.stream().mapToDouble(Order::getTotalPrice).sum();
        long totalOrders = recentOrders.size();
        long newCustomers = recentUsers.size();
        long pendingOrders = recentOrders.stream().filter(o -> o.getStatus().name().equals("PENDING_PAYMENT")).count();

        double revenueLastPeriod = allOrders.stream()
                .filter(o -> {
                    LocalDate orderDate = o.getOrderedAt().toLocalDate();
                    return orderDate.isAfter(startDate.minusDays(rangeDays)) && orderDate.isBefore(startDate);
                })
                .mapToDouble(Order::getTotalPrice).sum();

        long ordersLastPeriod = allOrders.stream()
                .filter(o -> {
                    LocalDate orderDate = o.getOrderedAt().toLocalDate();
                    return orderDate.isAfter(startDate.minusDays(rangeDays)) && orderDate.isBefore(startDate);
                })
                .count();

        long usersLastPeriod = allUsers.stream()
                .filter(u -> {
                    LocalDate joinDate = u.getUsersInfo().getJoined().toLocalDate();
                    return joinDate.isAfter(startDate.minusDays(rangeDays)) && joinDate.isBefore(startDate);
                })
                .count();

        long pendingLastPeriod = allOrders.stream()
                .filter(o -> {
                    LocalDate orderDate = o.getOrderedAt().toLocalDate();
                    return orderDate.isAfter(startDate.minusDays(rangeDays)) && orderDate.isBefore(startDate);
                })
                .filter(o -> o.getStatus().name().equals("PENDING_PAYMENT"))
                .count();

        double revenueTrend = calculateTrend(revenueLastPeriod, totalRevenue);
        double ordersTrend = calculateTrend(ordersLastPeriod, totalOrders);
        double usersTrend = calculateTrend(usersLastPeriod, newCustomers);
        double pendingTrend = calculateTrend(pendingLastPeriod, pendingOrders);

        if (currency.equalsIgnoreCase("ETB")) {
            totalRevenue *= USD_TO_ETB_RATE;
        }

        SalesTrendData trendData = generateSalesTrendData(recentOrders, startDate, endDate);
        List<TopProductResponse> topProducts = getTopProducts(5);

        return new DashboardSummaryResponse(
                new TrendValue(totalRevenue, revenueTrend),
                new TrendValue(totalOrders, ordersTrend),
                new TrendValue(newCustomers, usersTrend),
                new TrendValue(pendingOrders, pendingTrend),
                trendData,
                topProducts
        );
    }

    private double calculateTrend(double previous, double current) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((current - previous) / previous) * 100.0;
    }

    private SalesTrendData generateSalesTrendData(List<Order> orders, LocalDate startDate, LocalDate endDate) {
        List<String> labels = new ArrayList<>();
        List<Double> sales = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            labels.add(currentDate.format(DateTimeFormatter.ofPattern("MMM dd")));
            double total = orders.stream()
                    .filter(o -> o.getOrderedAt().toLocalDate().isEqual(currentDate))
                    .mapToDouble(Order::getTotalPrice)
                    .sum();
            sales.add(total);
        }

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "Sales");
        dataset.put("data", sales);
        dataset.put("borderColor", "#3b82f6");
        dataset.put("backgroundColor", "rgba(59, 130, 246, 0.2)");

        return new SalesTrendData(labels, List.of(dataset));
    }

    public List<TopProductResponse> getTopProducts(int limit) {
        List<Order> orders = orderRepository.findAll();
        Map<String, TopProductResponse> productSalesMap = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                String key = item.getProductName();
                TopProductResponse existing = productSalesMap.getOrDefault(
                        key,
                        new TopProductResponse(item.getProductName(), item.getProductImage(), 0)
                );
                existing.setSales(existing.getSales() + item.getQuantity());
                productSalesMap.put(key, existing);
            }
        }

        return productSalesMap.values().stream()
                .sorted((a, b) -> Integer.compare(b.getSales(), a.getSales()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<AdminOrderResponseDTO> getRecentOrders(int count) {
        return orderService.getRecentOrders(count);
    }
}