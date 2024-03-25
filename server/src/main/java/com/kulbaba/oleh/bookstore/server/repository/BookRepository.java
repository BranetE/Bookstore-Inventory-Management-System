package com.kulbaba.oleh.bookstore.server.repository;

import com.kulbaba.oleh.bookstore.server.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
}
