package com.example.dbqueries.service;

import com.example.dbqueries.dto.BookRequest;
import com.example.dbqueries.entity.Book;
import com.example.dbqueries.entity.Category;
import com.example.dbqueries.repository.BookRepository;
import com.example.dbqueries.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Book createBook(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPrice(request.getPrice());
        book.setPublishedDate(request.getPublishedDate());

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            for (Category category : categories) {
                book.addCategory(category);
            }
        }

        return bookRepository.save(book);
    }
}
