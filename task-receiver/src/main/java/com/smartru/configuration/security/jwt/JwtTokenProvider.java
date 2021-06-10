package com.smartru.configuration.security.jwt;

import com.smartru.entity.User;
import com.smartru.service.jpa.UserService;
import com.smartru.service.redis.RedisTokenService;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${token.access.secret}")
    private String secret;
    @Value("${token.access.expired}")
    private long sessionTime;

    private final RedisTokenService redis;
    private final UserService userService;

    @Autowired
    public JwtTokenProvider(RedisTokenService redis, UserService userService) {
        this.redis = redis;
        this.userService = userService;
    }

    @PostConstruct
    void init(){
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createAccessToken(User user){
        Claims claims = Jwts.claims().setSubject(user.getLogin());
        claims.put("id",user.getId());
        claims.put("role", user.getRole());

        Date now = new Date();
        Date validateDate = new Date(now.getTime()+ sessionTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validateDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String createRefreshToken(String token){
        String tokenTail = token.substring(token.length()-8);
        String randomString = UUID.randomUUID().toString();
        return tokenTail+"_"+randomString;
    }

    public boolean checkTokensRelation(String refreshToken, String accessToken){
        return refreshToken.startsWith(accessToken.substring(accessToken.length()-8));
    }

    public Authentication getAuthentication(String token){
        UserDetails userDetails = tokenToUserDetails(token);
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    public String getLogin(String token){
        try{
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException ex){
            return ex.getClaims().getSubject();
        }
    }

    public String getRole(String token){
        try{
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().get("role", String.class);
        } catch (ExpiredJwtException ex){
            return ex.getClaims().getSubject();
        }
    }

    private UserDetails tokenToUserDetails(String token){
        String login = getLogin(token);
        String role = getRole(token);
        UserDetails details = org.springframework.security.core.userdetails.User.builder()
                .username(login)
                .password("")
                .authorities(role)
                .build();
        return details;
    }

    public String resolveToken(HttpServletRequest req){
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken!=null&&bearerToken.startsWith("Bearer_")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token){
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            if (claims.getBody().getExpiration().before(new Date())) {
                log.error("Token is expired!");
                return false;
            }
            return checkTokenInRedis(token);
        }catch (JedisConnectionException ex){
            log.error("Redis server does not respond! Check token in mysql db");
            return checkTokenInDatabase(token);
        } catch (JwtException | IllegalArgumentException ex){
            log.error("JWT token is expired or invalid");
            return false;
        }
    }

    private boolean checkTokenInRedis(String token) throws JedisConnectionException{
            if (!redis.tokenExists(token)) {
                log.error("Access token is invalid");
                return false;
            }
            return true;
    }

    private boolean checkTokenInDatabase(String token){
        String username = getLogin(token);
        User user = userService.getByUsername(username).orElseThrow(()->{
            throw new UsernameNotFoundException("User with username: " + username + " not found. Token is broken");
        });
        return token.equals(user.getAccessToken());
    }
}
