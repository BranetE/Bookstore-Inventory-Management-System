package com.kulbaba.oleh.bookstore.server.mapper;

import com.kulbaba.oleh.bookstore.server.entity.Book;
import kulbaba.oleh.bookstore.BookServiceOuterClass.BookInfoListResponse;
import kulbaba.oleh.bookstore.BookServiceOuterClass.UpdateBookRequest;
import kulbaba.oleh.bookstore.BookServiceOuterClass.BookInfoResponse;
import kulbaba.oleh.bookstore.BookServiceOuterClass.CreateBookRequest;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface BookMapper {
    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    Book createBookRequestToBook(CreateBookRequest request);
    BookInfoResponse bookToBookInfoResponse(Book book);

    void updateBookFromUpdateBookRequest(@MappingTarget Book book, UpdateBookRequest request);

    default BookInfoListResponse bookListToBookInfoListResponse(List<Book> bookList){
        return BookInfoListResponse
                .newBuilder()
                .addAllBooks(bookList.stream().map(this::bookToBookInfoResponse).toList())
                .build();
    }
}
