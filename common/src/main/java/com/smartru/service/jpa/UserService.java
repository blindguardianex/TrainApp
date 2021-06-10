package com.smartru.service.jpa;

import com.smartru.entity.User;
import com.smartru.exceptions.EntityAlreadyExists;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface UserService {

    User add(User user) throws EntityAlreadyExists;
    Optional<User> getByUsername(String login);
    User update(User user);
}
