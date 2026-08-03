package com.example.dbqueries.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class BookRequest {
    private String title;
    private String author;
    private BigDecimal price;
    private LocalDate publishedDate;
    private List<Long> categoryIds;
}
