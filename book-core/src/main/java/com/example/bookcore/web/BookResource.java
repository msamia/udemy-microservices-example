package com.example.bookcore.web;

import com.example.bookcore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookResource {

    private final BookService service;

    @PostMapping
    public ResponseEntity<Long> save(@RequestBody BookDTO dto) {
        return ResponseEntity.ok().body(this.service.save(dto));
    }

}
