package com.azada.job.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author taoxiuma
 */
@Configuration
public class BeanConfig {

    @Value("${curator.zookeeper.address}")
    private String address;

    /**
     * application name
     */
    @Value("${spring.application.name}")
    private String nameSpace;

    @Bean
    @ConditionalOnProperty("curator.zookeeper.address")
    public CuratorFramework curatorFramework () {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(address)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace(nameSpace)
                .build();
        if (null == client) {
            throw new TarsException("init curator connection error");
        }
        return client;
    }
}
