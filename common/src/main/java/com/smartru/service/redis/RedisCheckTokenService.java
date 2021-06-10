package com.smartru.service.redis;

public interface RedisCheckTokenService {

    boolean tokenExists(String token);
}
