package com.example.dbqueries.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlaceOrderRequest {
    private String customerName;
    private List<OrderItemRequest> items;
}
