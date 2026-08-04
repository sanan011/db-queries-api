package com.example.dbqueries.service;

import com.example.dbqueries.dto.OrderItemRequest;
import com.example.dbqueries.dto.PlaceOrderRequest;
import com.example.dbqueries.entity.Book;
import com.example.dbqueries.entity.Category;
import com.example.dbqueries.exception.BookNotFoundException;
import com.example.dbqueries.repository.BookRepository;
import com.example.dbqueries.repository.CategoryRepository;
import com.example.dbqueries.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
// Using an in-memory H2 database for tests to isolate them from the MySQL database and avoid connection issues.
@org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase(replace = org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY)
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@Transactional // Ensures the test runs in a transaction that gets rolled back at the end, leaving DB clean
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void placeOrder_shouldRollbackAllChanges_whenOneBookDoesNotExist() {
        // 1. Setup test data (using the configured test database, e.g., H2 or embedded MySQL)
        Category category = new Category();
        category.setName("Test Category");
        categoryRepository.save(category);

        Book validBook = new Book();
        validBook.setTitle("Valid Book");
        validBook.setAuthor("Test Author");
        validBook.setPrice(new BigDecimal("29.99"));
        validBook.setPublishedDate(LocalDate.now());
        validBook.addCategory(category);
        bookRepository.save(validBook);

        Long validBookId = validBook.getId();
        Long invalidBookId = 9999L; // Non-existent ID

        // 2. Prepare the request
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setCustomerName("Test Customer");

        OrderItemRequest validItem = new OrderItemRequest();
        validItem.setBookId(validBookId);
        validItem.setQuantity(2);

        OrderItemRequest invalidItem = new OrderItemRequest();
        invalidItem.setBookId(invalidBookId);
        invalidItem.setQuantity(1);

        request.setItems(Arrays.asList(validItem, invalidItem));

        // 3. Act & Assert: Exception should be thrown
        assertThrows(BookNotFoundException.class, () -> {
            orderService.placeOrder(request);
        });

        // 4. Assert: Verify transaction rollback
        // Because the transaction rolls back (or the insert is never flushed due to the exception),
        // no order or order items should be persisted.
        assertThat(orderRepository.findAll()).isEmpty();
    }
}
