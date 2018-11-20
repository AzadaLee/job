package com.azada.job.config;

import com.azada.job.constant.DistributeScheduleConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 * @author taoxiuma
 */
@ConfigurationProperties(prefix = "curator.zookeeper")
@Data
@Configuration
public class CuratorProperties {

    /**
     * zookeeper 地址
     */
    private String address;


    /**
     * 命名空间
     */
    private String nameSpace = DistributeScheduleConstant.DEFAULT_NAMESPACE;
}
