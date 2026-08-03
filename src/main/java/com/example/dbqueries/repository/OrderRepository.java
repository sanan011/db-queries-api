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
}
