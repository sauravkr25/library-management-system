package com.project.library_management_system.service;

import com.project.library_management_system.dto.UserRequest;
import com.project.library_management_system.exception.UserException;
import com.project.library_management_system.model.Operator;
import com.project.library_management_system.model.UserFilterType;
import com.project.library_management_system.model.entity.User;
import com.project.library_management_system.model.UserType;
import com.project.library_management_system.repository.UserCacheRepository;
import com.project.library_management_system.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService implements UserDetailsService {
    private static final Log logger = LogFactory.getLog(UserService.class);
    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    @Value("${student.authority}")
    private String studentAuthority;

    @Value("${admin.authority}")
    private String adminAuthority;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserCacheRepository userCacheRepository;

    public User addStudent(UserRequest userRequest) {
        User user = userRequest.toUser();
        user.setAuthorities(studentAuthority);
        user.setPassword(encoder.encode(userRequest.getPassword()));
        user.setUserType(UserType.STUDENT);
        return userRepository.save(user);
    }

    public User addAdmin(UserRequest userRequest) {
        User user = userRequest.toUser();
        user.setAuthorities(adminAuthority);
        user.setPassword(encoder.encode(userRequest.getPassword()));
        user.setUserType(UserType.ADMIN);
        return userRepository.save(user);
    }

    public List<User> filter (String filterBy, String operator, String value) {
        String[] filters = filterBy.split(",");
        String[] operators = operator.split(",");
        String[] values = value.split(",");

        StringBuilder query = new StringBuilder();
        query.append("select * from user where ");
        for(int i=0;i< operators.length;i++){
            UserFilterType userFilterType = UserFilterType.valueOf(filters[i]);
            Operator operator1 = Operator.valueOf(operators[i]);
            String finalValue = values[i];
            query = query.append(userFilterType).
                    append(operator1.getValue()).
                    append("'").append(finalValue).
                    append("'").append(" and ");
        }

        String finalQuery = query.substring(0, query.length() - 4);
        logger.info("Query is : " + finalQuery);

        Query emQuery = em.createNativeQuery(finalQuery, User.class);
        return emQuery.getResultList();

    }

    public User getStudentByPhoneNo(String userPhoneNo) {
        return userRepository.findByPhoneNoAndUserType(userPhoneNo,UserType.STUDENT);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // load this user from redis first
        // if the user is present in redis, I want to get the data from redis

        // if the data is not present in redis, I want to go to db
        // I will be checking at db

        // if the data is present in db, then I will be keeping that data in my cache as well

        String phoneNo = username; // search by phone no.

        User user = userCacheRepository.getUser(phoneNo);
        if(user != null){
            return user;
        }

        user = userRepository.findByPhoneNo(phoneNo);
        if(user == null){
            new UserException("The user you are looking for does not belong to the library");
        }

        userCacheRepository.setUser(phoneNo,user);
        return user;
    }


}
