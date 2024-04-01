package com.kulbaba.oleh.bookstore.server.grpc;

import com.google.protobuf.Empty;
import com.kulbaba.oleh.bookstore.server.ModelUtils;
import com.kulbaba.oleh.bookstore.server.service.BookService;
import io.grpc.StatusRuntimeException;
import kulbaba.oleh.bookstore.BookServiceOuterClass.UpdateBookRequest;
import kulbaba.oleh.bookstore.BookServiceOuterClass.BookIdRequest;
import kulbaba.oleh.bookstore.BookServiceOuterClass.BookInfoResponse;
import kulbaba.oleh.bookstore.BookServiceOuterClass.CreateBookRequest;
import kulbaba.oleh.bookstore.ReactorBookServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.citrusframework.GherkinTestActionRunner;
import org.citrusframework.annotations.CitrusResource;
import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.junit.jupiter.CitrusExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.sql.DataSource;

import static java.lang.String.format;
import static org.citrusframework.actions.ExecuteSQLAction.Builder.sql;

@SpringBootTest(properties = {
        "grpc.server.in-process-name=test",
        "grpc.server.port=-1",
        "grpc.client.bookService.address=in-process:test"})
@Testcontainers
@ActiveProfiles("test")
@ExtendWith(CitrusExtension.class)
class BookGrpcServiceTest {

    @Autowired
    private BookService bookService;

    @GrpcClient("bookService")
    private ReactorBookServiceGrpc.ReactorBookServiceStub bookGrpcService;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("book_store")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("create-schema.sql");

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

    private DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(postgreSQLContainer.getJdbcUrl());
        dataSource.setUsername("test");
        dataSource.setPassword("test");
        return dataSource;
    }

    @AfterEach
    void tearDown(@CitrusResource GherkinTestActionRunner runner) {
        runner.$(sql()
                    .dataSource(getDataSource())
                        .sqlResource("delete-all-books.sql"));

    }

    @Test
    @CitrusTest
    @DirtiesContext
    void testCreate(@CitrusResource GherkinTestActionRunner runner) {
        CreateBookRequest request = CreateBookRequest.newBuilder()
                .setTitle("NewBook")
                .setAuthor("John Doe")
                .setQuantity(20)
                .setIsbn("978-123-456-7890-X")
                .build();

        Mono<BookInfoResponse> result = bookGrpcService.create(Mono.just(request));

        StepVerifier
                .create(result)
                .expectNextMatches(res -> !res.getId().equals(null))
                .verifyComplete();

        runner.$(sql()
                .dataSource(getDataSource())
                .query()
                .statement("SELECT title, author, isbn FROM books")
                .validate("title", request.getTitle())
                .validate("author", request.getAuthor())
                .validate("isbn", request.getIsbn()));
    }

    @Test
    @CitrusTest
    @DirtiesContext
    void testGetByIdSuccess(@CitrusResource GherkinTestActionRunner runner) {
        runner.$(sql()
                .dataSource(getDataSource())
                    .sqlResource("insert-default-book.sql"));

        BookIdRequest request = ModelUtils.defaultBookIdRequest();

        Mono<BookInfoResponse> result = bookGrpcService.getById(Mono.just(request));

        StepVerifier
                .create(result)
                .expectNextMatches(res ->
                        res.getTitle().equals(ModelUtils.DEFAULT_TITLE) &&
                        res.getAuthor().equals(ModelUtils.DEFAULT_AUTHOR))
                .verifyComplete();

        runner.$(sql()
                .dataSource(getDataSource())
                    .query()
                        .statement(String.format("SELECT COUNT(*) as book_count FROM books WHERE id='%s'", ModelUtils.DEFAULT_ID))
                .validate("book_count", "1"));
    }

    @Test
    @CitrusTest
    @DirtiesContext
    void testGetAllSuccess(@CitrusResource GherkinTestActionRunner runner) {
        runner.$(sql()
                .dataSource(getDataSource())
                    .sqlResource("insert-default-book.sql"));

        Flux<BookInfoResponse> result = bookGrpcService.getAll(Mono.just(Empty.newBuilder().build()));

        StepVerifier
                .create(result)
                .expectNextMatches(res ->
                        res.getTitle().equals(ModelUtils.DEFAULT_TITLE) &&
                        res.getAuthor().equals(ModelUtils.DEFAULT_AUTHOR))
                .verifyComplete();

        runner.$(sql()
                .dataSource(getDataSource())
                .query()
                .statement("SELECT COUNT(*) as book_count FROM books")
                .validate("book_count", "1"));
    }

    @Test
    @CitrusTest
    @DirtiesContext
    void testGetByIdThrowsEntityNotFoundException(@CitrusResource GherkinTestActionRunner runner) {
        BookIdRequest request = ModelUtils.defaultBookIdRequest();

        Mono<BookInfoResponse> result = bookGrpcService.getById(Mono.just(request));

        StepVerifier
                .create(result)
                .expectErrorMatches(e -> e instanceof StatusRuntimeException && e.getMessage().equals("NOT_FOUND: No books with given Id was found"))
                .verify();

        runner.$(sql()
                .dataSource(getDataSource())
                    .query()
                        .statement("SELECT COUNT(*) as book_count FROM books")
                .validate("book_count", "0"));
    }

    @Test
    @CitrusTest
    @DirtiesContext
    void testGetAllThrowsEntityNotFoundException(@CitrusResource GherkinTestActionRunner runner) {
        Flux<BookInfoResponse> result = bookGrpcService.getAll(Mono.just(Empty.newBuilder().build()));

        StepVerifier
                .create(result)
                .expectErrorMatches(e -> e instanceof StatusRuntimeException && e.getMessage().equals("NOT_FOUND: No books found"))
                .verify();

        runner.$(sql()
                .dataSource(getDataSource())
                .query()
                .statement("SELECT COUNT(*) as book_count FROM books")
                .validate("book_count", "0"));
    }

    @Test
    @CitrusTest
    @DirtiesContext
    void testUpdate(@CitrusResource GherkinTestActionRunner runner) {
        runner.$(sql()
                .dataSource(getDataSource())
                    .sqlResource("insert-default-book.sql"));

        UpdateBookRequest request = ModelUtils.defaultUpdateBookRequest();

        Mono<BookInfoResponse> result = bookGrpcService.updateById(request);

        StepVerifier
                .create(result)
                .expectNextMatches(res -> res.getTitle().equals(request.getTitle()))
                .verifyComplete();

        runner.$(sql()
                .dataSource(getDataSource())
                    .query()
                        .statement("SELECT COUNT(*) as book_count FROM books")
                .validate("book_count", "1"));
    }

    @Test
    @CitrusTest
    @DirtiesContext
    void testDelete(@CitrusResource GherkinTestActionRunner runner) {
        runner.$(sql()
                .dataSource(getDataSource())
                    .sqlResource("insert-default-book.sql"));

        BookIdRequest request = ModelUtils.defaultBookIdRequest();

        Mono<Empty> result = bookGrpcService.deleteById(Mono.just(request));

        StepVerifier
                .create(result)
                .expectNextMatches(res -> res instanceof Empty)
                .verifyComplete();

        runner.$(sql()
                .dataSource(getDataSource())
                    .query()
                        .statement("SELECT COUNT(*) as book_count FROM books")
                .validate("book_count", "0"));
    }
}
