package com.project.library_management_system.repository;

import com.project.library_management_system.model.entity.Book;
import com.project.library_management_system.model.BookType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByTitle(String title);

    List<Book> findByTitleContaining(String title);


    List<Book> findByBookType(BookType bookType);

    List<Book> findByBookNo(String bookNo);


}
