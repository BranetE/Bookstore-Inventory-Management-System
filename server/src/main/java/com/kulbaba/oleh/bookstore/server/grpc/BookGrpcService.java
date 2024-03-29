package com.kulbaba.oleh.bookstore.server.grpc;

import com.google.protobuf.Empty;
import com.kulbaba.oleh.bookstore.server.service.BookService;
import kulbaba.oleh.bookstore.BookServiceOuterClass.*;
import kulbaba.oleh.bookstore.ReactorBookServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@GrpcService
@RequiredArgsConstructor
public class BookGrpcService extends ReactorBookServiceGrpc.BookServiceImplBase {

    private final BookService bookService;

    @Override
    public Mono<BookInfoResponse> create(Mono<CreateBookRequest> request) {
        return request.flatMap(bookService::create);
    }

    @Override
    public Mono<BookInfoResponse> getById(Mono<BookIdRequest> request) {
        return request.flatMap(bookService::getById);
    }

    @Override
    public Flux<BookInfoResponse> getAll(Mono<Empty> request) {
        return request.thenMany(bookService.getAll());
    }

    @Override
    public Mono<BookInfoResponse> updateById(Mono<UpdateBookRequest> request) {
        return request.flatMap(bookService::updateById);
    }

    @Override
    public Mono<Empty> deleteById(Mono<BookIdRequest> request) {
        return request.flatMap(bookService::deleteById);
    }
}
