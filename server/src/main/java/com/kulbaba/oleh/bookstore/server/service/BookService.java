package com.kulbaba.oleh.bookstore.server.service;

import com.google.protobuf.Empty;
import com.kulbaba.oleh.bookstore.server.entity.Book;
import com.kulbaba.oleh.bookstore.server.mapper.BookMapper;
import com.kulbaba.oleh.bookstore.server.repository.BookRepository;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kulbaba.oleh.bookstore.BookServiceGrpc.BookServiceImplBase;
import kulbaba.oleh.bookstore.BookServiceOuterClass.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;

@GrpcService
@Transactional
@RequiredArgsConstructor
public class BookService extends BookServiceImplBase {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public void create(CreateBookRequest request, StreamObserver<BookInfoResponse> responseObserver) {
        Book book = bookMapper.createBookRequestToBook(request);
        BookInfoResponse response = bookMapper.bookToBookInfoResponse(bookRepository.save(book));

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getBtId(BookIdRequest request, StreamObserver<BookInfoResponse> responseObserver) {
        BookInfoResponse response = bookRepository.findById(UUID.fromString(request.getId()))
                .map(bookMapper::bookToBookInfoResponse)
                .orElseThrow(EntityNotFoundException::new);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAll(Empty request, StreamObserver<BookInfoListResponse> responseObserver) {
        List<Book> bookList = bookRepository.findAll();
        if (bookList.isEmpty()) throw new EntityNotFoundException();
        BookInfoListResponse response = bookMapper.bookListToBookInfoListResponse(bookList);

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void updateById(UpdateBookRequest request, StreamObserver<BookInfoResponse> responseObserver) {
        Book book = bookRepository.findById(UUID.fromString(request.getId())).orElseThrow(EntityNotFoundException::new);
        bookMapper.updateBookFromUpdateBookRequest(book, request);
        BookInfoResponse response = bookMapper.bookToBookInfoResponse(bookRepository.save(book));

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteById(BookIdRequest request, StreamObserver<Empty> responseObserver) {
        bookRepository.deleteById(UUID.fromString(request.getId()));

        responseObserver.onNext(Empty
                .newBuilder()
                .build());
        responseObserver.onCompleted();
    }
}
