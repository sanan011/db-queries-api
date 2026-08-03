package com.example.dbqueries.controller;

import com.example.dbqueries.dto.BookRequest;
import com.example.dbqueries.dto.BookResponse;
import com.example.dbqueries.repository.BookRepository;
import com.example.dbqueries.service.BookService;
import com.example.dbqueries.specification.BookSpecification;
import com.example.dbqueries.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookRepository bookRepository;

    @PostMapping
    public BookResponse createBook(@RequestBody BookRequest request) {
        return BookResponse.from(bookService.createBook(request));
    }

    @GetMapping("/search")
    public List<BookResponse> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String category) {

        Specification<Book> spec = Specification.where(null);

        if (title != null && !title.trim().isEmpty()) {
            spec = spec.and(BookSpecification.hasTitle(title));
        }
        if (author != null && !author.trim().isEmpty()) {
            spec = spec.and(BookSpecification.hasAuthor(author));
        }
        if (minPrice != null || maxPrice != null) {
            spec = spec.and(BookSpecification.priceBetween(minPrice, maxPrice));
        }
        if (category != null && !category.trim().isEmpty()) {
            spec = spec.and(BookSpecification.hasCategory(category));
        }

        return bookRepository.findAll(spec).stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }
}
