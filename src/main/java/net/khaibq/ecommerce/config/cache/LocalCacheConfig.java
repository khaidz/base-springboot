package net.khaibq.ecommerce.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import net.khaibq.ecommerce.config.properties.LocalCacheConfigurationProperties;
import org.checkerframework.checker.index.qual.NonNegative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(
        value = {"app.cache.memory.enable"},
        havingValue = "true"
)
public class LocalCacheConfig {
    private static final Logger log = LoggerFactory.getLogger(LocalCacheConfig.class);

    @Bean
    public Caffeine<Object, Object> caffeineCacheBuilder(LocalCacheConfigurationProperties properties) {
        return Caffeine.newBuilder().expireAfter(new Expiry<>() {
            @Override
            public long expireAfterCreate(Object o, Object o2, long l) {
                return TimeUnit.MILLISECONDS.toNanos(properties.getExpireTime());
            }

            @Override
            public long expireAfterUpdate(Object o, Object o2, long l, @NonNegative long l1) {
                return l1;
            }

            @Override
            public long expireAfterRead(Object o, Object o2, long l, @NonNegative long l1) {
                return l1;
            }
        });
    }

    @Bean
    @Primary
    public CacheManager localCacheManager(Caffeine<Object, Object> caffeineCacheBuilder, LocalCacheConfigurationProperties properties) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        if (properties.getCacheNames() != null && !"".equals(properties.getCacheNames())) {
            cacheManager.setCacheNames(List.of(properties.getCacheNames()));
        } else {
            cacheManager.setCacheNames(null);
        }

        log.info("Configuring local cache manager");
        log.info("Using caffeine: {}", caffeineCacheBuilder);
        log.info("Cache names: {}", properties.getCacheNames());
        cacheManager.setCaffeine(caffeineCacheBuilder);
        return cacheManager;
    }

}
