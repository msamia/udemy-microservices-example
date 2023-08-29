package com.example.bookcore.service;

import com.example.bookcore.dispatcher.KafkaDispatcher;
import com.example.bookcore.model.Book;
import com.example.bookcore.repository.BookRepository;
import com.example.bookcore.web.BookDTO;
import com.example.common.BookCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "transactionManager")
public class BookService {

    private final BookRepository repository;
    private final KafkaDispatcher dispatcher;

    public Long save(BookDTO dto) {
        log.debug("Book saving process has been starting...");
        Book savedBook = this.repository.save(
                Book.builder()
                        .title(dto.title())
                        .author(dto.author())
                        .price(dto.price())
                        .type(dto.type())
                        .build());
        this.dispatcher.send(new BookCreated(savedBook.getId().toString(), savedBook.getTitle(), savedBook.getAuthor(),
                savedBook.getPrice(), savedBook.getType()));
        log.debug("Book saving process had been finished. {}", savedBook.getId());
        return savedBook.getId();
    }
}
