package com.azada.job.framework;

import com.azada.job.bean.ScheduleBean;
import com.azada.job.config.TarsException;
import com.azada.job.constant.DistributeScheduleConstant;
import com.azada.job.event.*;
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

    private static final byte[] EMPTY_DATA = null;

    @Value("${server.port}")
    public Integer port;

    @Resource
    private CuratorFramework curatorFramework;

    @Resource
    private ScheduleImpNodeAddContentPublisher scheduleImpNodeAddContentPublisher;

    @Resource
    private ScheduleImpNodeEmptyContentPublisher scheduleImpNodeEmptyContentPublisher;

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
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(path, EMPTY_DATA);//不设置初始值，defaultValue会变成ip
            }
        } catch (Exception e) {
            log.error("init bean node error :{}",e);
        }
    }

    /**
     * 创建具体业务节点
     */
    public void createScheduleServiceNode(ScheduleBean scheduleBean) throws Exception {
        String scheduleServicePath = NodePathUtil.generateServiceNodePathName(scheduleBean);
        try {
            Stat stat = curatorFramework.checkExists().forPath(scheduleServicePath);//需要先判断节点是否存在，节点存在时，创建节点会抛NodeExists异常
            if (null == stat) {
                curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(scheduleServicePath, EMPTY_DATA);
            }
        } catch (Exception e) {
            if (!(e instanceof KeeperException.NodeExistsException)) {
                //非结点已存在异常，停止操作
                log.error("init schedule {} bean node error :{}", scheduleBean.getClassFullName() ,e);
                throw e;
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
    public void createScheduleServiceImpNode(ScheduleBean scheduleBean) throws Exception {
        String scheduleServiceImpNodePath = NodePathUtil.generateServiceImplNodePathName(scheduleBean, port);
        //模式必须为EPHEMERAL，应用节点停机后会自动删除节点
        curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(scheduleServiceImpNodePath, EMPTY_DATA);
        //创建实例节点的监听
        createNodeListener(scheduleBean);
    }

    /**
     * 针对某个节点创建监听
     * @param scheduleBean
     */
    public void createNodeListener(ScheduleBean scheduleBean) throws Exception {
        String scheduleServiceImpNodePath = NodePathUtil.generateServiceImplNodePathName(scheduleBean, port);
        // 创建监听
        final NodeCache nodeCache = new NodeCache(curatorFramework, scheduleServiceImpNodePath, false);
        nodeCache.start(true);
        nodeCache.getListenable().addListener(() -> {
            String nodeData = new String(nodeCache.getCurrentData().getData());
            ScheduleImpNodeData scheduleImpNodeData = new
                    ScheduleImpNodeData(scheduleBean.getClazz(),scheduleBean.getClassFullName(),nodeData);
            if (!StringUtils.isEmpty(nodeData)) {
                ScheduleImpNodeAddContentEvent event = new ScheduleImpNodeAddContentEvent(scheduleImpNodeData);
                //发布节点内容设置值事件
                scheduleImpNodeAddContentPublisher.publish(event);
            } else {
                ScheduleImpNodeEmptyContentEvent event = new ScheduleImpNodeEmptyContentEvent(scheduleImpNodeData);
                //发布实例结点内容被清空事件
                scheduleImpNodeEmptyContentPublisher.publish(event);
            }
        });

    }
}
