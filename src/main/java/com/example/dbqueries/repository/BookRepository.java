package com.example.dbqueries.repository;

import com.example.dbqueries.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    // Derived query method: Good for simple, straightforward queries. 
    // Spring Data JPA parses the method name to construct the query.
    List<Book> findByPriceBetweenAndCategories_Name(BigDecimal min, BigDecimal max, String categoryName);

    // JPQL query: Good for readable object-oriented queries that are too complex for method names,
    // or when you want more control over the exact query generated without relying on method name parsing.
    @Query("SELECT b FROM Book b WHERE b.publishedDate > :date ORDER BY b.price DESC")
    List<Book> findBooksPublishedAfterOrderByPriceDesc(@Param("date") LocalDate date);

    // Native SQL query: Essential for complex aggregations, database-specific functions,
    // or highly optimized queries that JPQL cannot express well.
    @Query(nativeQuery = true, value = 
           "SELECT c.name, AVG(b.price) " +
           "FROM books b " +
           "JOIN book_category bc ON b.id = bc.book_id " +
           "JOIN categories c ON bc.category_id = c.id " +
           "GROUP BY c.name")
    List<Object[]> findAveragePriceByCategory();
}
