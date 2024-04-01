package com.kulbaba.oleh.bookstore.server.service;

import com.google.protobuf.Empty;
import com.kulbaba.oleh.bookstore.server.ModelUtils;
import com.kulbaba.oleh.bookstore.server.entity.Book;
import com.kulbaba.oleh.bookstore.server.mapper.BookMapper;
import com.kulbaba.oleh.bookstore.server.repository.BookRepository;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    BookRepository bookRepository;
    @Spy
    BookMapper bookMapper = Mappers.getMapper(BookMapper.class);
    @InjectMocks
    BookService bookService;

    private Book getRandomBook() {
        Book book = new Book();
        book.setTitle("NewBook");
        book.setAuthor("John Doe");
        book.setIsbn("978-123-456-7890-X");
        book.setQuantity(20);

        return book;
    }

    @Test
    void createTest() {
        CreateBookRequest request = ModelUtils.defaultCreateBookRequest();

        Book book = getRandomBook();

        doReturn(Mono.just(book)).when(bookRepository).save(any(Book.class));

        Mono<BookInfoResponse> result = bookService.create(request);

        BookInfoResponse expected = ModelUtils.defaultBookInfoResponse();


        verify(bookMapper).createBookRequestToBook(request);
        verify(bookRepository).save(any(Book.class));

        StepVerifier
                .create(result)
                .expectNext(Mono.just(expected).block())
                .verifyComplete();
    }

    @Test
    void getByIdTest() {
        BookIdRequest request = ModelUtils.defaultBookIdRequest();

        Book book = getRandomBook();

        doReturn(Mono.just(book)).when(bookRepository).findById(UUID.fromString(request.getId()));

        BookInfoResponse expected = ModelUtils.defaultBookInfoResponse();

        Mono<BookInfoResponse> result = bookService.getById(request);

        verify(bookRepository).findById(UUID.fromString(request.getId()));
         StepVerifier
                 .create(result)
                 .expectNext(Mono.just(expected).block())
                 .verifyComplete();

    }

    @Test
    void getAllTest() {
        Empty request = Empty.getDefaultInstance();

        Book book = getRandomBook();

        doReturn(Flux.just(book)).when(bookRepository).findAll();

        Flux<BookInfoResponse> result = bookService.getAll();

        verify(bookRepository).findAll();

        StepVerifier
                .create(result)
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    void updateTest() {
        UpdateBookRequest request = ModelUtils.defaultUpdateBookRequest();

        Book book = getRandomBook();

        when(bookRepository.findById(UUID.fromString(request.getId()))).thenReturn(Mono.just(book));
        when(bookRepository.save(book)).thenReturn(Mono.just(book));

        Mono<BookInfoResponse> result = bookService.updateById(request);

        StepVerifier
                .create(result)
                .expectNextMatches(res -> res.getTitle().equals(request.getTitle()))
                .expectComplete()
                .verify();

    }

    @Test
    void deleteByIdTest() {
        BookIdRequest request = ModelUtils.defaultBookIdRequest();

        doReturn(Mono.empty()).when(bookRepository).deleteById(UUID.fromString(request.getId()));

        Mono<Empty> result = bookService.deleteById(request);

        verify(bookRepository).deleteById(UUID.fromString(request.getId()));

        StepVerifier
                .create(result)
                .expectNext(Mono.just(Empty.newBuilder().build()).block())
                .expectComplete()
                .verify();
    }
}
