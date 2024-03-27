package com.kulbaba.oleh.bookstore.server.service;

import com.google.protobuf.Empty;
import com.kulbaba.oleh.bookstore.server.entity.Book;
import com.kulbaba.oleh.bookstore.server.mapper.BookMapper;
import com.kulbaba.oleh.bookstore.server.repository.BookRepository;
import io.grpc.stub.StreamObserver;
import kulbaba.oleh.bookstore.BookServiceOuterClass.BookInfoListResponse;
import kulbaba.oleh.bookstore.BookServiceOuterClass.UpdateBookRequest;
import kulbaba.oleh.bookstore.BookServiceOuterClass.BookInfoResponse;
import kulbaba.oleh.bookstore.BookServiceOuterClass.BookIdRequest;
import kulbaba.oleh.bookstore.BookServiceOuterClass.CreateBookRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookGrpcServiceTest {
    @Mock
    BookRepository bookRepository;
    @Spy
    BookMapper bookMapper = Mappers.getMapper(BookMapper.class);
    @Mock
    StreamObserver streamObserver;
    @InjectMocks
    BookGrpcService bookGrpcService;

    private Book getRandomBook() {
        Book book = new Book();
        book.setTitle("NewBook");
        book.setAuthor("John Doe");
        book.setIsbn("978-123-456-7890-X");
        book.setQuantity(20);

        return book;
    }

    @Test
    @SuppressWarnings("unchecked")
    void createTest() {
        CreateBookRequest request = CreateBookRequest.newBuilder()
                .setTitle("NewBook")
                .setAuthor("John Doe")
                .setIsbn("978-123-456-7890-X")
                .setQuantity(20L)
                .build();

        Book book = getRandomBook();

        doReturn(book).when(bookMapper).createBookRequestToBook(request);
        doReturn(book).when(bookRepository).save(book);

        bookGrpcService.create(request, streamObserver);

        verify(bookMapper).createBookRequestToBook(request);
        verify(bookRepository).save(book);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getByIdTest() {
        BookIdRequest request = BookIdRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();

        Book book = getRandomBook();

        BookInfoResponse bookInfoResponse = BookInfoResponse.getDefaultInstance();

        doReturn(Optional.of(book)).when(bookRepository).findById(UUID.fromString(request.getId()));
        doReturn(bookInfoResponse).when(bookMapper).bookToBookInfoResponse(book);

        bookGrpcService.getById(request, streamObserver);

        verify(bookRepository).findById(UUID.fromString(request.getId()));
        verify(bookMapper).bookToBookInfoResponse(book);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAllTest() {
        Empty request = Empty.getDefaultInstance();

        List<Book> randomBookList = List.of(getRandomBook());

        doReturn(randomBookList).when(bookRepository).findAll();
        doReturn(BookInfoListResponse.getDefaultInstance()).when(bookMapper).bookListToBookInfoListResponse(randomBookList);

        bookGrpcService.getAll(request, streamObserver);

        verify(bookRepository).findAll();
        verify(bookMapper).bookListToBookInfoListResponse(randomBookList);
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateTest() {
        UpdateBookRequest request = UpdateBookRequest.newBuilder()
                .setTitle("NewBook")
                .setId(UUID.randomUUID().toString())
                .setAuthor("John Doe")
                .setIsbn("978-123-456-7890-X")
                .build();

        Book book = getRandomBook();

        when(bookRepository.findById(UUID.fromString(request.getId()))).thenReturn(Optional.of(book));
        doNothing().when(bookMapper).updateBookFromUpdateBookRequest(book, request);
        when(bookRepository.save(book)).thenReturn(book);

        bookGrpcService.updateById(request, streamObserver);

        verify(bookRepository).findById(UUID.fromString(request.getId()));
        verify(bookMapper).updateBookFromUpdateBookRequest(book, request);
        verify(bookRepository).save(book);
    }

    @Test
    @SuppressWarnings("unchecked")
    void deleteByIdTest() {
        BookIdRequest request = BookIdRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();

        doNothing().when(bookRepository).deleteById(UUID.fromString(request.getId()));

        bookGrpcService.deleteById(request, streamObserver);

        verify(bookRepository).deleteById(UUID.fromString(request.getId()));
    }
}
