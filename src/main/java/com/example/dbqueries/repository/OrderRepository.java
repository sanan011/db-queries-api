package com.example.dbqueries.repository;

import com.example.dbqueries.entity.Order;
import com.example.dbqueries.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // JPQL query with JOIN: Useful for querying across relationships.
    // We use SELECT DISTINCT to avoid returning duplicate Orders if multiple items match.
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE o.status = :status AND i.book.id = :bookId")
    List<Order> findOrdersByStatusAndBookId(@Param("status") OrderStatus status, @Param("bookId") Long bookId);

    // Naive method: This causes an N+1 query problem. When we access the items (which are lazily loaded by default),
    // Hibernate will execute an additional query for each Order to fetch its items.
    List<Order> findByStatus(OrderStatus status);

    // Fixed method: Uses a JOIN FETCH to eagerly load the items and their associated books in a single query.
    // This avoids the N+1 problem because all required data is retrieved upfront without secondary queries.
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.book WHERE o.status = :status")
    List<Order> findByStatusWithItems(@Param("status") OrderStatus status);
}

