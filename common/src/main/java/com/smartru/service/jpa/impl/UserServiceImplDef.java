package com.smartru.service.jpa.impl;

import com.smartru.entity.User;
import com.smartru.exceptions.EntityAlreadyExists;
import com.smartru.repository.UserRepository;
import com.smartru.service.jpa.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Qualifier("defaultUserService")
public class UserServiceImplDef implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    public User add(User user) throws EntityAlreadyExists {
        Optional<User>optUser = repository.findByUsername(user.getLogin());
        if (optUser.isEmpty()){
            user = repository.saveAndFlush(user);
            log.info("IN add - user: {} successfully saving in database",user.getLogin());
            return user;
        }
        log.warn("IN add - user: {} already exist", user.getLogin());
        throw new EntityAlreadyExists("User already exist");
    }

    @Override
    public Optional<User> getByUsername(String login) {
        Optional<User>optUser =repository.findByUsername(login);
        if(optUser.isPresent()){
            log.info("IN getByUsername - by username: {} find user", login);
        } else {
            log.warn("IN getByUsername - not found user by username: {}", login);
        }
        return optUser;
    }

    @Override
    public User update(User user) {
        Optional<User>optUser = repository.findByUsername(user.getLogin());
        if (optUser.isPresent()){
            user = repository.saveAndFlush(user);
            log.info("IN update - user: {} successfully updated in database",user.getLogin());
            return user;
        }
        log.warn("IN update - user: {} is absent", user.getLogin());
        throw new IllegalArgumentException("User is absent");
    }
}
