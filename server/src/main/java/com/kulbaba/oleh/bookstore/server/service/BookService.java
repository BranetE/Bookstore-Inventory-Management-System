package com.kulbaba.oleh.bookstore.server.service;

import com.google.protobuf.Empty;
import com.kulbaba.oleh.bookstore.server.entity.Book;
import com.kulbaba.oleh.bookstore.server.mapper.BookMapper;
import com.kulbaba.oleh.bookstore.server.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import kulbaba.oleh.bookstore.BookServiceOuterClass.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public Mono<BookInfoResponse> create(CreateBookRequest request){
        Book book = bookMapper.createBookRequestToBook(request);

        return bookRepository.save(book).map(bookMapper::bookToBookInfoResponse);

    }

    public Mono<BookInfoResponse> getById(BookIdRequest request) {
        return bookRepository.findById(UUID.fromString(request.getId()))
                .map(bookMapper::bookToBookInfoResponse)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("No books with given Id was found")));

    }

    public Flux<BookInfoResponse> getAll() {
        return bookRepository.findAll()
                .map(bookMapper::bookToBookInfoResponse)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("No books found")));
    }

    public Mono<BookInfoResponse> updateById(UpdateBookRequest request){
        return bookRepository.findById(UUID.fromString(request.getId()))
                .flatMap(e -> {
                    bookMapper.updateBookFromUpdateBookRequest(e, request);
                    return bookRepository.save(e);
                })
                .map(bookMapper::bookToBookInfoResponse)
                .switchIfEmpty(Mono.error(new EntityNotFoundException()));

    }

    public Mono<Empty> deleteById(BookIdRequest request) {
        return bookRepository.deleteById(UUID.fromString(request.getId())).then(Mono.just(Empty.newBuilder().build()));
    }

}
