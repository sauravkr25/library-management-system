package com.project.library_management_system.controller;

import com.project.library_management_system.dto.BookRequest;
import com.project.library_management_system.model.entity.Book;
import com.project.library_management_system.model.BookFilterType;
import com.project.library_management_system.model.Operator;
import com.project.library_management_system.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/addBook")
    public Book addBook(@RequestBody @Valid BookRequest bookRequest){

        // validations before the business logic
        /*  Old method -- now we add validation dependency

        if(StringUtil.isNullOrEmpty(bookRequest.getBookNo())){
            throw new Exception("Book No. should not be blank");
        }
        */

        // to call the business logic
        Book book = bookService.addBook(bookRequest);


        // return the required data
        return book;

    }

    @GetMapping("/filter")
    public List<Book> filter(@RequestParam("filterBy") BookFilterType filterType,
                                  @RequestParam("operator") Operator operator,
                                  @RequestParam("value") String value){

        return bookService.filter(filterType,operator,value);
    }
}
