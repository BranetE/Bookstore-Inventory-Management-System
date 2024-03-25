package com.kulbaba.oleh.bookstore.server.mapper;

import com.kulbaba.oleh.bookstore.server.entity.Book;
import kulbaba.oleh.bookstore.BookServiceOuterClass.BookInfoListResponse;
import kulbaba.oleh.bookstore.BookServiceOuterClass.UpdateBookRequest;
import kulbaba.oleh.bookstore.BookServiceOuterClass.BookInfoResponse;
import kulbaba.oleh.bookstore.BookServiceOuterClass.CreateBookRequest;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface BookMapper {
    Book createBookRequestToBook(CreateBookRequest request);
    BookInfoResponse bookToBookInfoResponse(Book book);
    BookInfoListResponse bookListToBookInfoListResponse(List<Book> bookList);
    void updateBookFromUpdateBookRequest(@MappingTarget Book book, UpdateBookRequest request);
}
