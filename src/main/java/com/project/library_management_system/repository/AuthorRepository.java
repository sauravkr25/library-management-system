package com.project.library_management_system.repository;

import com.project.library_management_system.model.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    //native query
    @Query(value = "select * from author where email = :email", nativeQuery = true)
    Author getAuthorByEmail(String email);
}
