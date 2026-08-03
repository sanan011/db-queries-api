package com.example.dbqueries.specification;

import com.example.dbqueries.entity.Book;
import com.example.dbqueries.entity.Category;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class BookSpecification {

    public static Specification<Book> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> hasAuthor(String author) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Book> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) -> {
            if (min != null && max != null) {
                return criteriaBuilder.between(root.get("price"), min, max);
            } else if (min != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), min);
            } else if (max != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), max);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Book> hasCategory(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            Join<Book, Category> categories = root.join("categories");
            return criteriaBuilder.equal(categories.get("name"), categoryName);
        };
    }
}
