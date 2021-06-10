package com.smartru.service.redis;

import com.smartru.service.redis.RedisTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Slf4j
@Service
public class RedisTokenServiceImpl implements RedisTokenService {

    private final Jedis jedis;

    @Autowired
    public RedisTokenServiceImpl(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public void addToken(long userId, String token){
        jedis.set(String.valueOf(userId), token);
        jedis.set(token, String.valueOf(userId));
    }

    @Override
    public boolean tokenExists(String token) {
        return jedis.exists(token);
    }
}
