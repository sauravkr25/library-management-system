package com.project.library_management_system.service;

import com.project.library_management_system.dto.BookRequest;
import com.project.library_management_system.model.*;
import com.project.library_management_system.model.entity.Author;
import com.project.library_management_system.model.entity.Book;
import com.project.library_management_system.repository.AuthorRepository;
import com.project.library_management_system.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;


    public Book addBook(BookRequest bookRequest) {

        Author authorFromDB = authorRepository.getAuthorByEmail(bookRequest.getAuthorEmail());
        if(authorFromDB == null){
            // make object of author table
            // save the data in author table
            authorFromDB = authorRepository.save(bookRequest.toAuthor());
        }
        Book book = bookRequest.toBook();
        book.setAuthor(authorFromDB);
        return bookRepository.save(book);
    }

    public List<Book> filter(BookFilterType filterType, Operator operator, String value) {
        switch (filterType){
            case BOOK_TITLE :
                switch (operator){
                    case EQUALS :
                        return bookRepository.findByTitle(value);
                    case LIKE :
                        return bookRepository.findByTitleContaining(value);
                    default :
                        return new ArrayList<>();
                }
            case BOOK_TYPE :
                switch (operator){
                    case EQUALS :
                        return bookRepository.findByBookType(BookType.valueOf(value));
                    default :
                        return new ArrayList<>();
                }
            case BOOK_NO :
                switch (operator){
                    case EQUALS :
                        return bookRepository.findByBookNo(value);
                    default :
                        return new ArrayList<>();
                }
        }
        return new ArrayList<>();
    }

    public void updateBookData(Book bookFromDB) {
        bookRepository.save(bookFromDB);
    }
}
