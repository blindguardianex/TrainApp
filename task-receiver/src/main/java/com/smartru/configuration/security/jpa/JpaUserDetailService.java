package com.smartru.configuration.security.jpa;

import com.smartru.configuration.security.SecurityUser;
import com.smartru.entity.User;
import com.smartru.service.jpa.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
public class JpaUserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<User> optUser = userService.getByUsername(login);
        if (optUser.isPresent()){
            return new SecurityUser(optUser.get());
        }
        throw new UsernameNotFoundException("User not found");
    }
}
