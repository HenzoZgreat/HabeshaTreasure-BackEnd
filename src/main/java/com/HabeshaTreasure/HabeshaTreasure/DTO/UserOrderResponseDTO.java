package com.HabeshaTreasure.HabeshaTreasure.DTO;

import com.HabeshaTreasure.HabeshaTreasure.Entity.Orders.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderResponseDTO {
    private Long orderId;
    private Double totalPrice;
    private LocalDateTime orderedAt;
    private OrderStatus status;
    private List<OrderItemDTO> items;
}


