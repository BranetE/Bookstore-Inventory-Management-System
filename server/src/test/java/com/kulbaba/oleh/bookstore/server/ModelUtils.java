package com.kulbaba.oleh.bookstore.server;

import kulbaba.oleh.bookstore.BookServiceOuterClass.BookInfoResponse;
import kulbaba.oleh.bookstore.BookServiceOuterClass.UpdateBookRequest;
import kulbaba.oleh.bookstore.BookServiceOuterClass.CreateBookRequest;

public class ModelUtils {

    public static CreateBookRequest createBookRequest() {
        return CreateBookRequest.newBuilder()
                .setTitle("NewBook")
                .setAuthor("John Doe")
                .setIsbn("978-123-456-7890-X")
                .build();
    }

    public static UpdateBookRequest updateBookRequest() {
        return UpdateBookRequest.newBuilder()
                .setTitle("ChangedTitle")
                .build();
    }

    public static BookInfoResponse bookInfoResponse() {
        return BookInfoResponse.newBuilder()
                .setTitle("NewBook")
                .setAuthor("John Doe")
                .setIsbn("978-123-456-7890-X")
                .setQuantity(20L)
                .build();
    }
}
