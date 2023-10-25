package net.khaibq.ecommerce.config;

import net.khaibq.ecommerce.config.properties.AppConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);
    private final AppConfigurationProperties appConfigurationProperties;

    public AsyncConfig(AppConfigurationProperties appConfigurationProperties) {
        this.appConfigurationProperties = appConfigurationProperties;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        log.info("corePoolSize : {}, MaxPoolSize : {}",
                this.appConfigurationProperties.getAsyncExecutorCorePoolSize(),
                this.appConfigurationProperties.getAsyncExecutorMaxPoolSize());
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(this.appConfigurationProperties.getAsyncExecutorCorePoolSize());
        executor.setMaxPoolSize(this.appConfigurationProperties.getAsyncExecutorMaxPoolSize());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix(this.appConfigurationProperties.getAsyncExecutorThreadNamePrefix());
        return executor;
    }

}
