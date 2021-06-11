package com.smartru.controller;

import com.smartru.configuration.security.jwt.JwtTokenProvider;
import com.smartru.dto.AuthenticationRequestDto;
import com.smartru.entity.User;
import com.smartru.service.jpa.UserService;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/",
        consumes="application/json")
public class AuthenticationController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Autowired
    public AuthenticationController(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("sign")
    public ResponseEntity signIn(@RequestBody AuthenticationRequestDto auth){
        String username = auth.getUsername();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, auth.getPassword()));
        User user = userService.getByUsername(username).get();

        Map<Object, Object> response = jwtTokenProvider.createTokens(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("refresh_token")
    public ResponseEntity refreshToken(@CookieValue(value = "Refresh_Token") String refreshToken,
                                       @CookieValue(value = "Expired_Token") String expiredToken){
        try{
            Map<Object, Object>response = jwtTokenProvider.refreshTokens(refreshToken, expiredToken);
            log.info("Token refresh is successful");
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException | JwtException ex) {
            log.error("Incorrect refresh token");
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }
}

