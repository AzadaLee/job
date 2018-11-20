package com.azada.job;

import com.azada.job.config.CuratorProperties;
import com.azada.job.config.TarsException;
import com.azada.job.framework.CuratorClient;
import com.azada.job.framework.DistributeScheduleProcessor;
import com.azada.job.schedule.IDistributeSchedule;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author taoxiuma
 */
@Configuration
@EnableConfigurationProperties(CuratorProperties.class)
@ConditionalOnBean(IDistributeSchedule.class)
@ComponentScan(basePackageClasses = AzadaJobAutoConfiguration.class)
@EnableScheduling
public class AzadaJobAutoConfiguration {

    @Bean
    @ConditionalOnProperty("curator.zookeeper.address")
    public CuratorFramework curatorFramework (CuratorProperties curatorProperties) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(curatorProperties.getAddress())
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace(curatorProperties.getNameSpace())
                .build();
        if (null == client) {
            throw new TarsException("init curator connection error");
        }
        return client;
    }
}
