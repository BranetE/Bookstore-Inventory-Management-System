package com.kulbaba.oleh.bookstore.server.service;

import com.google.protobuf.Empty;
import com.kulbaba.oleh.bookstore.server.mapper.BookMapper;
import com.kulbaba.oleh.bookstore.server.repository.BookRepository;
import jakarta.transaction.Transactional;
import kulbaba.oleh.bookstore.BookServiceOuterClass.*;
import kulbaba.oleh.bookstore.ReactorBookServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@GrpcService
@Transactional
@RequiredArgsConstructor
public class BookGrpcService extends ReactorBookServiceGrpc.BookServiceImplBase {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

//    @Override
//    public void create(CreateBookRequest request, StreamObserver<BookInfoResponse> responseObserver) {
//        Book book = bookMapper.createBookRequestToBook(request);
//        BookInfoResponse response = bookMapper.bookToBookInfoResponse(bookRepository.save(book));
//
//        responseObserver.onNext(response);
//        responseObserver.onCompleted();
//    }
//
//    @Override
//    public void getById(BookIdRequest request, StreamObserver<BookInfoResponse> responseObserver) {
//        BookInfoResponse response = bookRepository.findById(UUID.fromString(request.getId()))
//                .map(bookMapper::bookToBookInfoResponse)
//                .orElseThrow(EntityNotFoundException::new);
//
//        responseObserver.onNext(response);
//        responseObserver.onCompleted();
//    }
//
//    @Override
//    public void getAll(Empty request, StreamObserver<BookInfoListResponse> responseObserver) {
//        List<Book> bookList = bookRepository.findAll();
//        if (bookList.isEmpty()) throw new EntityNotFoundException();
//        BookInfoListResponse response = bookMapper.bookListToBookInfoListResponse(bookList);
//
//        responseObserver.onNext(response);
//        responseObserver.onCompleted();
//
//    }
//
//    @Override
//    public void updateById(UpdateBookRequest request, StreamObserver<BookInfoResponse> responseObserver) {
//        Book book = bookRepository.findById(UUID.fromString(request.getId())).orElseThrow(EntityNotFoundException::new);
//        bookMapper.updateBookFromUpdateBookRequest(book, request);
//        BookInfoResponse response = bookMapper.bookToBookInfoResponse(bookRepository.save(book));
//
//        responseObserver.onNext(response);
//        responseObserver.onCompleted();
//    }
//
//    @Override
//    public void deleteById(BookIdRequest request, StreamObserver<Empty> responseObserver) {
//        bookRepository.deleteById(UUID.fromString(request.getId()));
//
//        responseObserver.onNext(Empty
//                .newBuilder()
//                .build());
//        responseObserver.onCompleted();
//    }


    @Override
    public Mono<BookInfoResponse> create(Mono<CreateBookRequest> request) {
        return super.create(request);
    }

    @Override
    public Mono<BookInfoResponse> getById(Mono<BookIdRequest> request) {
        return super.getById(request);
    }

    @Override
    public Flux<BookInfoResponse> getAll(Mono<Empty> request) {
        return super.getAll(request);
    }

    @Override
    public Mono<BookInfoResponse> updateById(Mono<UpdateBookRequest> request) {
        return super.updateById(request);
    }

    @Override
    public Mono<Empty> deleteById(Mono<BookIdRequest> request) {
        return super.deleteById(request);
    }
}
