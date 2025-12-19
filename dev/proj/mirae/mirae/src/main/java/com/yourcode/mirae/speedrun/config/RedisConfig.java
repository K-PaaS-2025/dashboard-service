package com.yourcode.mirae.speedrun.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisObjectTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisScript<Long> submitResultScript() {
        String script = """
            local state = redis.call('HGET', KEYS[1], 'state')
            if state ~= 'RUNNING' then
                return -1
            end

            local solved = redis.call('HGET', KEYS[2], 'solved_problems')
            if solved and string.find(solved, ARGV[1]) then
                return -2
            end

            redis.call('HINCRBY', KEYS[2], 'score', ARGV[2])
            redis.call('HINCRBY', KEYS[2], 'solved_count', 1)
            redis.call('ZINCRBY', KEYS[3], ARGV[2], ARGV[3])

            local newSolved = solved and (solved .. ',' .. ARGV[1]) or ARGV[1]
            redis.call('HSET', KEYS[2], 'solved_problems', newSolved)

            return 1
            """;
        return new DefaultRedisScript<>(script, Long.class);
    }
}