package com.project.library_management_system.repository;

import com.project.library_management_system.model.UserType;
import com.project.library_management_system.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByPhoneNoAndUserType (String phoneNo, UserType type);

    User findByPhoneNo(String phoneNo);
}
