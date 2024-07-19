package com.project.library_management_system.repository;

import com.project.library_management_system.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class UserCacheRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.user.details.timeout}")
    private int timeout;

    private static String userKey = "user::";

    public User getUser(String phoneNo){
        String key = userKey+phoneNo;
        return (User) redisTemplate.opsForValue().get(key);
    }

    public void setUser(String phoneNo, User user){
        String key = userKey+phoneNo;
        redisTemplate.opsForValue().set(key,user,timeout, TimeUnit.MINUTES);
    }
}
