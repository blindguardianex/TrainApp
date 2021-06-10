package com.smartru.controller;

import com.smartru.configuration.security.jwt.JwtTokenProvider;
import com.smartru.dto.AuthenticationRequestDto;
import com.smartru.entity.User;
import com.smartru.service.jpa.UserService;
import com.smartru.service.redis.RedisTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/",
        consumes="application/json")
public class AuthenticationController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RedisTokenService redisTokenService;

    @Autowired
    public AuthenticationController(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, UserService userService, RedisTokenService redisTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.redisTokenService = redisTokenService;
    }

    @PostMapping("sign")
    public ResponseEntity signIn(@RequestBody AuthenticationRequestDto auth){
        String username = auth.getUsername();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, auth.getPassword()));
        User user = userService.getByUsername(username).get();

        Map<Object, Object> response = getTokens(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("refresh_token")
    public ResponseEntity refreshToken(@CookieValue(value = "Refresh_Token") String refreshToken,
                                       @CookieValue(value = "Expired_Token") String expiredToken){
        if (jwtTokenProvider.checkTokensRelation(refreshToken,expiredToken)){
            String username = jwtTokenProvider.getLogin(expiredToken);
            User user = userService.getByUsername(username).orElseThrow(()->{
                throw new UsernameNotFoundException("User with username: " + username + " not found");
            });

            if (refreshToken.equals(user.getRefreshToken())){
                Map<Object, Object>response = getTokens(user);
                return ResponseEntity.ok(response);
            }
        }
        log.error("Incorrect refresh token");
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    private Map<Object, Object> getTokens(User user){
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(accessToken);
        updateUser(user,accessToken,refreshToken);

        Map<Object, Object> response = new HashMap<>();
        response.put("username", user.getLogin());
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        return response;
    }

    private void updateUser(final User user, String accessToken, String refreshToken){
        try {
            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            userService.update(user);

            redisTokenService.addToken(user.getId(), accessToken);
        } catch (JedisConnectionException e){
            log.error("Server redis does not respond! Saving only mysql db");
        }
    }
}

