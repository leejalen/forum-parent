package com.jqh.forum.article;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tk.mybatis.spring.annotation.MapperScan;
import util.IdWorker;

import javax.annotation.Resource;

/**
 * @Auther: 几米
 * @Date: 2019/5/11 13:39
 * @Description: 文章部分模块儿
 */
@SpringCloudApplication
@EnableFeignClients
@MapperScan("com.jqh.forum.article.mapper")
public class ArticleApplication {
    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    public static void main(String[] args) {
        SpringApplication.run(ArticleApplication.class);
    }


    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }

    /**
     * 解决redis的key value乱码问题
     *
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
