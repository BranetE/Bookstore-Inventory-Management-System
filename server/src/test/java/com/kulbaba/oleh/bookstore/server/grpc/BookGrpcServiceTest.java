package com.kulbaba.oleh.bookstore.server.grpc;

import com.google.protobuf.Empty;
import com.kulbaba.oleh.bookstore.server.ModelUtils;
import com.kulbaba.oleh.bookstore.server.ServerApplication;
import com.kulbaba.oleh.bookstore.server.service.BookService;
import io.grpc.StatusRuntimeException;
import jakarta.persistence.EntityNotFoundException;
import kulbaba.oleh.bookstore.BookServiceOuterClass.UpdateBookRequest;
import kulbaba.oleh.bookstore.BookServiceOuterClass.BookIdRequest;
import kulbaba.oleh.bookstore.BookServiceOuterClass.BookInfoResponse;
import kulbaba.oleh.bookstore.BookServiceOuterClass.CreateBookRequest;
import kulbaba.oleh.bookstore.ReactorBookServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static java.lang.String.format;

@SpringBootTest(properties = {
        "grpc.server.in-process-name=test",
        "grpc.server.port=-1",
        "grpc.client.bookService.address=in-process:test"})
@Testcontainers
@SpringJUnitConfig(classes = {ServerApplication.class})
@EnableAutoConfiguration
@DirtiesContext
class BookGrpcServiceTest {

    @Autowired
    private BookService bookService;

    @GrpcClient("bookService")
    private ReactorBookServiceGrpc.ReactorBookServiceStub bookGrpcService;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("book_store")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("db/changelog/db.changelog-create-schema.sql");

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    private static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                format("r2dbc:pool:postgresql://%s:%d/%s",
                        postgreSQLContainer.getHost(),
                        postgreSQLContainer.getFirstMappedPort(),
                        postgreSQLContainer.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);
    }

    static{
        postgreSQLContainer.start();
    }
    @Test
    @DirtiesContext
    void testCreate() {
        CreateBookRequest request = CreateBookRequest.newBuilder()
                .setTitle("NewBook")
                .setAuthor("John Doe")
                .setIsbn("978-123-456-7890-X")
                .build();
        Mono<BookInfoResponse> result = bookGrpcService.create(Mono.just(request));

        StepVerifier
                .create(result)
                .expectNextMatches(res -> !res.getId().equals(null))
                .verifyComplete();
    }

    @Test
    @DirtiesContext
    void testGetByIdThrowsEntityNotFoundException() {
        BookIdRequest request = BookIdRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();

        Mono<BookInfoResponse> result = bookGrpcService.getById(Mono.just(request));

        StepVerifier
                .create(result)
                .expectErrorMatches(e -> e instanceof StatusRuntimeException && e.getMessage().equals("NOT_FOUND: No books with given Id was found"))
                .verify();
    }

    @Test
    @DirtiesContext
    void testGetAllThrowsEntityNotFoundException() {
        Flux<BookInfoResponse> result = bookGrpcService.getAll(Mono.just(Empty.newBuilder().build()));

        StepVerifier
                .create(result)
                .expectErrorMatches(e -> e instanceof StatusRuntimeException && e.getMessage().equals("NOT_FOUND: No books found"))
                .verify();
    }

    @Test
    @DirtiesContext
    void testUpdate() {
        String createdBookId = bookGrpcService
                .create(Mono.just(ModelUtils.createBookRequest()))
                .block()
                .getId();

        UpdateBookRequest request = UpdateBookRequest.newBuilder()
                .setId(createdBookId)
                .setTitle("ChangedTitle")
                .build();

        Mono<BookInfoResponse> result = bookGrpcService.updateById(request);

        StepVerifier
                .create(result)
                .expectNextMatches(res -> res.getTitle().equals(request.getTitle()))
                .verifyComplete();
    }

    @Test
    @DirtiesContext
    void testDelete() {
        String createdBookId = bookGrpcService
                .create(Mono.just(ModelUtils.createBookRequest()))
                .block()
                .getId();

        BookIdRequest request = BookIdRequest.newBuilder()
                .setId(createdBookId)
                .build();

        Mono<Empty> result = bookGrpcService.deleteById(Mono.just(request));

        StepVerifier
                .create(result)
                .expectComplete();

    }
}
