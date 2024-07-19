package com.project.library_management_system.service;

import com.project.library_management_system.dto.TxnRequest;
import com.project.library_management_system.exception.TxnException;
import com.project.library_management_system.model.entity.Book;
import com.project.library_management_system.model.entity.Txn;
import com.project.library_management_system.model.entity.User;
import com.project.library_management_system.repository.TxnRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class TestTxnService {
    @InjectMocks
    private TxnService txnService;

    @Mock  // gives empty objects
    private TxnRepository txnRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Before
    public void setUp() {
//        txnService = new TxnService(); -> don't create object by yourself, use @InjectMocks and @Mock
        //can set below values using setters when u create object by yourself but not recommended

        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(txnService,"validDays","12");
        ReflectionTestUtils.setField(txnService,"finePerDay","2");
    }

//    @Test
//    public void testCalculateSettlement() throws ParseException {
//        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2024-05-01");
//        Txn txn = Txn.builder().createdOn(date).build();
//        int calculatedSettlement = txnService.calculateSettlement(txn, 100);
//        Assert.assertEquals(44, calculatedSettlement); // setting expected result
//    }

    @Test(expected = TxnException.class)
    public void testGetUserFromDBForException() throws TxnException {
        TxnRequest txnRequest = TxnRequest.builder().build();
        when(userService.getStudentByPhoneNo(any())).thenReturn(null);
        txnService.getUserFromDB(txnRequest.getUserPhoneNo());
    }

    @Test
    public void testGetUserFromDBForNoException() throws TxnException {
        TxnRequest txnRequest = TxnRequest.builder().build();
        User user = User.builder().id(1).build();
        when(userService.getStudentByPhoneNo(any())).thenReturn(user);
        User output = txnService.getUserFromDB(txnRequest.getUserPhoneNo());
        Assert.assertEquals(user.getId(),output.getId());
    }

    @Test
    public void TestReturnBook() throws TxnException, ParseException {
        TxnRequest txnRequest = TxnRequest.builder().build();
        User user = User.builder().id(1).build();
        when(userService.getStudentByPhoneNo(any())).thenReturn(user);

        List<Book> list = new ArrayList<>();
        list.add(Book.builder().id(1).bookNo("1").user(user).securityAmount(100).build());
        when(bookService.filter(any(), any(), any())).thenReturn(list);

        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2024-05-01");
        Txn txn = Txn.builder().id(1).user(user).book(list.get(0)).createdOn(date).build();
        when(txnRepository.findByUserPhoneNoAndBookBookNoAndTxnStatus(any(),any(),any())).thenReturn(txn);

        int settlementAmount = txnService.returnBook(txnRequest);
        Assert.assertEquals(44,settlementAmount);
    }

}
