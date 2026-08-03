package com.example.dbqueries.service;

import com.example.dbqueries.dto.OrderItemRequest;
import com.example.dbqueries.dto.PlaceOrderRequest;
import com.example.dbqueries.entity.Book;
import com.example.dbqueries.entity.Order;
import com.example.dbqueries.entity.OrderItem;
import com.example.dbqueries.entity.OrderStatus;
import com.example.dbqueries.exception.BookNotFoundException;
import com.example.dbqueries.repository.BookRepository;
import com.example.dbqueries.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;

    /*
     * This method needs to be transactional so that the entire order placement
     * (creating the order and all its items) succeeds or fails as a single atomic unit.
     * Without it, if book #3 doesn't exist, the order and book #1/#2's order items
     * would already be committed to the database, leaving inconsistent data.
     * The @Transactional annotation ensures that any runtime exception (like BookNotFoundException)
     * will roll back all inserts, keeping the database in a consistent state.
     */
    @Transactional
    public Order placeOrder(PlaceOrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        if (request.getItems() != null) {
            for (OrderItemRequest itemRequest : request.getItems()) {
                Book book = bookRepository.findById(itemRequest.getBookId())
                        .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + itemRequest.getBookId()));
                
                OrderItem orderItem = new OrderItem();
                orderItem.setBook(book);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setPriceAtPurchase(book.getPrice());
                
                order.addItem(orderItem);
            }
        }

        return orderRepository.save(order);
    }
}
