package com.smartru.service.redis;

public interface RedisTokenService extends RedisCheckTokenService{

    void addToken(String userId, String token);
}
