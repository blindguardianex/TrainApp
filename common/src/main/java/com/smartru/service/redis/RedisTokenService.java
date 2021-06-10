package com.smartru.service.redis;

public interface RedisTokenService extends RedisCheckTokenService{

    void addToken(long userId, String token);
}
