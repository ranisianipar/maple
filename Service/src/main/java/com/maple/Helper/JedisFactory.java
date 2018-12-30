package com.maple.Helper;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

// implements Singleton Design Pattern
public class JedisFactory {
    private static JedisPool jedisPool;
    private static JedisFactory instance;

    private JedisFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        jedisPool = new JedisPool("localhost", 6379);
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public static JedisFactory getInstance() {
        if (instance == null) {
            instance = new JedisFactory();
        }

        return instance;
    }
}
