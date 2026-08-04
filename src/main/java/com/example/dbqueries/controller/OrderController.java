package com.example.dbqueries.controller;

import com.example.dbqueries.dto.OrderItemResponse;
import com.example.dbqueries.dto.OrderResponse;
import com.example.dbqueries.dto.PlaceOrderRequest;
import com.example.dbqueries.entity.Order;
import com.example.dbqueries.entity.OrderStatus;
import com.example.dbqueries.repository.OrderRepository;
import com.example.dbqueries.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement and lookup endpoints")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping
    @Operation(summary = "Place an order", description = "Creates a new order with the requested items")
    public OrderResponse placeOrder(@RequestBody PlaceOrderRequest request) {
        Order order = orderService.placeOrder(request);
        return mapToResponse(order);
    }

    @GetMapping
    @Operation(summary = "List orders by status", description = "Returns all orders matching the given status")
    public List<OrderResponse> getOrdersByStatus(@RequestParam OrderStatus status) {
        // Use the fixed method to avoid N+1 queries when mapping to OrderResponse (which accesses items and books)
        return orderRepository.findByStatusWithItems(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getBook().getTitle(),
                        item.getQuantity(),
                        item.getPriceAtPurchase()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getStatus().name(),
                itemResponses
        );
    }
}

