package com.azada.job.framework;

import com.azada.job.config.TarsException;
import com.azada.job.util.IpUtil;
import com.azada.job.constant.DistributeScheduleConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class CuratorClient {

    private static final byte[] emptyData = null;

    @Autowired
    private CuratorFramework curatorFramework;

    @Value("${server.port}")
    public String port;

    /**
     * start and create lock node
     */
    @PostConstruct
    public void init() {
        if (null == curatorFramework) {
            throw new TarsException("init curator connection error");
        }
        curatorFramework.start();
        log.info("init curator connection success");
        createLockNode();
    }

    /**
     * 创建锁模块
     * 该节点是用来每块业务的具体实现加锁使用，第一次创建时直接持久化
     */
    public void createLockNode() {
        try {
            String path = DistributeScheduleConstant.DIRECTORY_CHARACTER.concat(DistributeScheduleConstant.NODE_LOCK);
            Stat stat = curatorFramework.checkExists().forPath(path);
            if (null == stat) {
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(path, emptyData);//不设置初始值，defaultValue会变成ip
            }
        } catch (Exception e) {
            log.error("init bean node error :{}",e);
        }
    }

    /**
     * 创建具体业务节点
     */
    public void createServiceNode(String serviceModuleName, String serviceBeanName) {
        if (StringUtils.isEmpty(serviceModuleName) || StringUtils.isEmpty(serviceBeanName)) {
            throw new TarsException("serviceModuleName & serviceBeanName can not be null");
        }
        try {
            String path = DistributeScheduleConstant.DIRECTORY_CHARACTER.concat(serviceModuleName)
                    .concat(DistributeScheduleConstant.DIRECTORY_CHARACTER).concat(serviceBeanName);
            Stat stat = curatorFramework.checkExists().forPath(path);//需要先判断节点是否存在，节点存在时，创建节点会抛NodeExists异常
            if (null == stat) {
                curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, emptyData);
            }
            //创建实例节点
            createServiceImpNode(path);
        } catch (Exception e) {
            log.error("init bean node error :{}", e);
        }
    }

    /**
     * 创建业务实现节点
     * @param servicePathName
     */
    public void createServiceImpNode(String servicePathName) {
        if (StringUtils.isEmpty(servicePathName)) {
            throw new TarsException("servicePathName can not be null");
        }
        String impPathName = generateServiceImplNodePathName(servicePathName);
        try {
            Stat stat = curatorFramework.checkExists().forPath(impPathName);
            if (null == stat) {
                //模式必须为EPHEMERAL，应用节点停机后会自动删除节点
                curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(impPathName, emptyData);
            }
            final NodeCache nodeCache = new NodeCache(curatorFramework, impPathName, false);
            try {
                nodeCache.start(true);
                nodeCache.getListenable().addListener(() -> {
                            try{
//                                jobWorker.work(new String(nodeCache.getCurrentData().getData()),new String(nodeCache.getCurrentData().getPath()));
                            }catch(Exception e){
                            }
                        }
                );
            } catch (Exception e) {
                log.info("监听数据节点异常:{}",e);
            }

        } catch (Exception e) {
            log.error("init service implement node error :{}", e);
        }
    }

    /**
     * 生成service实例节点路劲名
     * @param servicePathName
     * @return
     */
    private String generateServiceImplNodePathName (String servicePathName) {
        String ip = IpUtil.getLocalIP();
        return servicePathName.concat(DistributeScheduleConstant.DIRECTORY_CHARACTER).concat(ip).concat(":" + port);
    }

    public NodeCache createNodeCache(String servicePathName) {
        String impPathName = generateServiceImplNodePathName(servicePathName);
        return new NodeCache(curatorFramework, impPathName, false);
    }

}
