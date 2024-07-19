package com.project.library_management_system.controller;

import com.project.library_management_system.dto.UserRequest;
import com.project.library_management_system.model.entity.User;
import com.project.library_management_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/addStudent")
    public User addStudent(@RequestBody @Valid UserRequest userRequest){
        return userService.addStudent(userRequest);
    }

    @PostMapping("/addAdmin")
    public User addAdmin(@RequestBody @Valid UserRequest userRequest){
        return userService.addAdmin(userRequest);
    }

    @GetMapping("/getStudent")
    public User getStudent(){
        return null;
    }


    // query on multiple parameters -> using comma separated inputs
    //url structure -> localhost:8081/user/filter?filterBy=NAME,EMAIL&operator=EQUALS,EQUALS&values=Saurav Kumar,sk@gmail.com
    @GetMapping("/filter")
    public List<User> filter(@RequestParam("filterBy") String filterBy,
                             @RequestParam("operator") String operator,
                             @RequestParam("values") String values){

        return userService.filter(filterBy, operator, values);
    }
}
