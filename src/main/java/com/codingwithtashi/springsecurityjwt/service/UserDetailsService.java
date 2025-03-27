package com.codingwithtashi.springsecurityjwt.service;

import com.codingwithtashi.springsecurityjwt.model.User;
import com.codingwithtashi.springsecurityjwt.repository.UserRepository;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
@Service
@Log4j2
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
        User user = repository.findByUserName(phoneNo);
        if(user==null){
            user = new User();
            user.setUserName(phoneNo);
            repository.save(user);
        }
        log.info("User loaded: {}",phoneNo);
        return new org.springframework.security.core.userdetails.User(user.getUserName(), "",
                new ArrayList<>());
    }
}