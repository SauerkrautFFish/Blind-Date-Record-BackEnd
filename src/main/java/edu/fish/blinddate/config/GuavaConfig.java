package edu.fish.blinddate.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GuavaConfig {

    @Bean
    public Cache<Object, Object> guavaCache() {
        return CacheBuilder.newBuilder()
                //设置缓存初始大小，应该合理设置，后续会扩容
                .initialCapacity(10)
                //最大值
                .maximumSize(100)
                //并发数设置
                .concurrencyLevel(5)
                //缓存过期时间，写入后100秒过期
                .expireAfterWrite(60, TimeUnit.SECONDS)
                // 此缓存对象经过多少秒没有被访问则过期。
                .expireAfterAccess(60, TimeUnit.SECONDS)
                //统计缓存命中率
                .recordStats()
                .build();
    }
}
