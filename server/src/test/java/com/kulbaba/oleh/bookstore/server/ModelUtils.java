package com.kulbaba.oleh.bookstore.server;

import kulbaba.oleh.bookstore.BookServiceOuterClass.BookIdRequest;
import kulbaba.oleh.bookstore.BookServiceOuterClass.BookInfoResponse;
import kulbaba.oleh.bookstore.BookServiceOuterClass.UpdateBookRequest;
import kulbaba.oleh.bookstore.BookServiceOuterClass.CreateBookRequest;

import java.util.UUID;

public class ModelUtils {

    public static final UUID DEFAULT_ID = UUID.fromString("b63db9ad-1314-4d58-8ba6-269780e4232c");
    public static final String DEFAULT_AUTHOR = "John Doe";
    public static final String DEFAULT_TITLE = "NewBook";
    public static final String DEFAULT_ISBN = "978-123-456-7890-X";

    public static CreateBookRequest defaultCreateBookRequest() {
        return CreateBookRequest.newBuilder()
                .setTitle(DEFAULT_TITLE)
                .setAuthor(DEFAULT_AUTHOR)
                .setIsbn(DEFAULT_ISBN)
                .build();
    }

    public static UpdateBookRequest defaultUpdateBookRequest() {
        return UpdateBookRequest.newBuilder()
                .setId(DEFAULT_ID.toString())
                .setTitle("ChangedTitle")
                .setAuthor("Different Author")
                .setQuantity(10L)
                .build();
    }

    public static BookIdRequest defaultBookIdRequest() {
        return BookIdRequest.newBuilder()
                .setId(DEFAULT_ID.toString())
                .build();
    }

    public static BookInfoResponse defaultBookInfoResponse() {
        return BookInfoResponse.newBuilder()
                .setTitle(DEFAULT_TITLE)
                .setAuthor(DEFAULT_AUTHOR)
                .setIsbn(DEFAULT_ISBN)
                .setQuantity(20L)
                .build();
    }
}
