package com.azada.job.framework;

import com.azada.job.bean.ScheduleBean;
import com.azada.job.config.TarsException;
import com.azada.job.constant.DistributeScheduleConstant;
import com.azada.job.listener.ScheduleImpNodeData;
import com.azada.job.listener.ScheduleImpNodeEvent;
import com.azada.job.listener.ScheduleImpNodePublisher;
import com.azada.job.util.NodePathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
@Slf4j
public class CuratorClient {

    private static final byte[] emptyData = null;

    @Value("${server.port}")
    public Integer port;

    @Resource
    private CuratorFramework curatorFramework;

    @Resource
    private ScheduleImpNodePublisher scheduleImpNodePublisher;

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
    public void createScheduleServiceNode(ScheduleBean scheduleBean) {
        String scheduleServicePath = NodePathUtil.generateServiceNodePathName(scheduleBean);
        try {
            Stat stat = curatorFramework.checkExists().forPath(scheduleServicePath);//需要先判断节点是否存在，节点存在时，创建节点会抛NodeExists异常
            if (null == stat) {
                curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(scheduleServicePath, emptyData);
            }
        } catch (Exception e) {
            if (!(e instanceof KeeperException.NodeExistsException)) {
                log.error("init schedule bean {} node error :{}", scheduleBean.getClassFullName() ,e);
                return;
            }
            log.info("schedule bean {} node is exists, create schedule bean imp node still", scheduleBean.getClassFullName());
        }
        //创建实例节点
        createScheduleServiceImpNode(scheduleBean);
    }

    /**
     * 创建业务实现节点
     * @param scheduleBean
     */
    public void createScheduleServiceImpNode(ScheduleBean scheduleBean) {
        String scheduleServiceImpNodePath = NodePathUtil.generateServiceImplNodePathName(scheduleBean, port);
        try {
            //模式必须为EPHEMERAL，应用节点停机后会自动删除节点
            curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(scheduleServiceImpNodePath, emptyData);
        } catch (Exception e) {
            //创建失败直接返回，不尝试重新注册该节点
            log.error("init service implement node {} error :{}", scheduleServiceImpNodePath, e);
            return;
        }
        //创建实例节点的监听
        createNodeListener(scheduleBean);
    }

    /**
     * 针对某个节点创建监听
     * @param scheduleBean
     */
    public void createNodeListener(ScheduleBean scheduleBean) {
        String scheduleServiceImpNodePath = NodePathUtil.generateServiceImplNodePathName(scheduleBean, port);
        // 创建监听
        final NodeCache nodeCache = new NodeCache(curatorFramework, scheduleServiceImpNodePath, false);
        try {
            nodeCache.start(true);
            nodeCache.getListenable().addListener(() -> {
                String nodeData = new String(nodeCache.getCurrentData().getData());
                if (!StringUtils.isEmpty(nodeData)) {
                    //节点内容不为空时才发布事件
                    ScheduleImpNodeData scheduleImpNodeData = new
                            ScheduleImpNodeData(scheduleBean.getClazz(),scheduleBean.getClassFullName(),nodeData);
                    ScheduleImpNodeEvent event = new ScheduleImpNodeEvent(scheduleImpNodeData);
                    scheduleImpNodePublisher.publish(event);
                }
            });
        } catch (Exception e) {
            log.info("listen node {} error :{} ",scheduleServiceImpNodePath, e);
            //TODO 对节点创建监听失败，是否需要删除该节点
        }
    }
}
