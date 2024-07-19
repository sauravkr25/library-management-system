package com.project.library_management_system.service;

import com.project.library_management_system.dto.TxnRequest;
import com.project.library_management_system.exception.TxnException;
import com.project.library_management_system.model.BookFilterType;
import com.project.library_management_system.model.Operator;
import com.project.library_management_system.model.TxnStatus;
import com.project.library_management_system.model.entity.Book;
import com.project.library_management_system.model.entity.Txn;
import com.project.library_management_system.model.entity.User;
import com.project.library_management_system.repository.TxnRepository;
import com.project.library_management_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class TxnService {

    @Autowired
    private TxnRepository txnRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Value("${book.valid.upto}")
    private String validDays;

    @Value("${book.fine.amount.per.day}")
    private String finePerDay;


//    @Transactional(value = Transactional.TxType.NOT_SUPPORTED)
    public User getUserFromDB(String phoneNo) throws TxnException {
        // student exists ?  // only student can take book, not admin
        User userFromDB = userService.getStudentByPhoneNo(phoneNo);
        if(userFromDB == null){
            throw new TxnException("Student does not belong to library");
        }
        return userFromDB;
    }

//    @Transactional(value = Transactional.TxType.NOT_SUPPORTED)
    public Book getBookFromDB(String bookNo) throws TxnException {
        // book exists ?
        List<Book> books = bookService.filter(BookFilterType.BOOK_NO, Operator.EQUALS, bookNo);
        if(books.isEmpty()){
            throw new TxnException("Book does not belong to library");
        }
        Book bookFromDB = books.get(0);

        return bookFromDB;
    }

    // writing data in db should be transactional, not retrieving data from db
    @Transactional(rollbackOn = {TxnException.class})
    public String createTxn(User userFromDB, Book bookFromDB){
        String txnId = UUID.randomUUID().toString();

        Txn txn = Txn.
                builder().
                txnId(txnId).
                user(userFromDB).
                book(bookFromDB).
                txnStatus(TxnStatus.ISSUED).
                build();

        txnRepository.save(txn);

        // set the book as issued to the user
        bookFromDB.setUser(userFromDB);
        bookService.updateBookData(bookFromDB);

        return txnId;
    }


    public String create(TxnRequest txnRequest) throws TxnException {

        User userFromDB = getUserFromDB(txnRequest.getUserPhoneNo());
        Book bookFromDB = getBookFromDB(txnRequest.getBookNo());
        // book unissued ?
        if(bookFromDB.getUser() != null){
            throw new TxnException("Book already has been issued to someone else");
        }

        return createTxn(userFromDB,bookFromDB);
    }


    @Transactional(rollbackOn = {TxnException.class})
    public int returnBook(TxnRequest txnRequest) throws TxnException {
        User userFromDB = getUserFromDB(txnRequest.getUserPhoneNo());
        Book bookFromDB = getBookFromDB(txnRequest.getBookNo());
        if(bookFromDB.getUser() != userFromDB){
            throw new TxnException("Book not assigned to this student");
        }
        Txn txn = txnRepository.findByUserPhoneNoAndBookBookNoAndTxnStatus(txnRequest.getUserPhoneNo(),txnRequest.getBookNo(),TxnStatus.ISSUED);
        int settlementAmount = calculateSettlement(txn, bookFromDB.getSecurityAmount());

        if(settlementAmount == bookFromDB.getSecurityAmount()){
            txn.setTxnStatus(TxnStatus.RETURNED);
        }
        else{
            txn.setTxnStatus(TxnStatus.FINED);
        }
        txn.setSettlementAmount(settlementAmount);
        bookFromDB.setUser(null);
        bookService.updateBookData(bookFromDB);
        txnRepository.save(txn);

        return settlementAmount;
    }

    // it is actually the amount returned to student i.e. security - fine
    private int calculateSettlement(Txn txn, int securityAmount) {
        long issueDate = txn.getCreatedOn().getTime();
        long returnDate = System.currentTimeMillis();
        long timeDiff = returnDate - issueDate;
        int daysPassed = (int) TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

        if(daysPassed > Integer.valueOf(validDays)){
            int fineAmount = (daysPassed - Integer.valueOf(validDays)) * Integer.valueOf(finePerDay);
            return securityAmount-fineAmount;
        }
        return securityAmount;
    }
}
