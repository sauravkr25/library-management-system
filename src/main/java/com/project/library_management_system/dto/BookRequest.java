package com.project.library_management_system.dto;


import com.project.library_management_system.model.entity.Author;
import com.project.library_management_system.model.entity.Book;
import com.project.library_management_system.model.BookType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequest {

    @NotBlank(message = "Book Title should not be blank")
    private String bookTitle;

    @NotBlank(message = "Book No. should not be blank")
    private String bookNo;

    @NotBlank(message = "Author Name should not be blank")
    private String authorName;

    @NotBlank(message = "Author Email should not be blank")
    private String authorEmail;

    @NotNull(message = "Book Type should not be null")
    private BookType type;

    @Positive(message = "Security Amount should not be blank")
    private int securityAmount;

    public Author toAuthor(){
        return Author.
                builder().
                email(this.authorEmail).
                name(this.authorName).
                build();
    }

    public Book toBook(){
        return Book.
                builder().
                bookNo(this.bookNo).
                title(this.bookTitle).
                securityAmount(this.securityAmount).
                bookType(this.type).
                build();
    }

}
