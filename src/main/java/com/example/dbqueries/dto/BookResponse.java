package com.example.dbqueries.dto;

import com.example.dbqueries.entity.Book;
import com.example.dbqueries.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private LocalDate publishedDate;
    private List<String> categoryNames;

    public static BookResponse from(Book book) {
        if (book == null) {
            return null;
        }
        List<String> categoryNames = book.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getPublishedDate(),
                categoryNames
        );
    }
}
