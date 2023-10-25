package net.khaibq.ecommerce.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
        prefix = "app.cache.memory"
)
@Getter
@Setter
public class LocalCacheConfigurationProperties {
    private boolean enable;
    private String cacheNames = "";
    private long expireTime = 60_000 * 10; //Milisecond
}
