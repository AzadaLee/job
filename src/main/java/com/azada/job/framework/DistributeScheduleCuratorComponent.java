package com.azada.job.framework;

import com.azada.job.bean.ScheduleBean;
import com.azada.job.constant.DistributeScheduleConstant;
import com.azada.job.util.CuratorFrameworkUtils;
import com.azada.job.util.NodePathUtil;
import com.azada.job.util.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分布式任务zookeeper操作类
 *
 * @author taoxiuma
 */

@Component
@Slf4j
public class DistributeScheduleCuratorComponent {

    @Resource
    private CuratorFrameworkUtils curatorFrameworkUtils;

    /**
     * 获取锁
     * true：获取成功
     * false：获取失败
     * @param scheduleBean
     * @return
     */
    public boolean acquireLock(ScheduleBean scheduleBean) {
        boolean acquireSuccess = false;
        String scheduleLockNodePathName = DistributeScheduleConstant.DIRECTORY_CHARACTER.concat(DistributeScheduleConstant.NODE_LOCK)
                .concat(DistributeScheduleConstant.DIRECTORY_CHARACTER).concat(scheduleBean.getClassFullName());
        log.info("schedule {} try acquire lock ..................", scheduleBean.getClassFullName());
        if (!tryAcquireLock(scheduleLockNodePathName)) {
            log.info("schedule {} try acquire lock fail.", scheduleBean.getClassFullName());
            return acquireSuccess;
        }
        log.info("schedule {} try acquire lock success.", scheduleBean.getClassFullName());
        String scheduleNodePathName = DistributeScheduleConstant.DIRECTORY_CHARACTER.concat(scheduleBean.getScheduleServiceName())
                .concat(DistributeScheduleConstant.DIRECTORY_CHARACTER).concat(scheduleBean.getClassFullName());

        log.info("node {} check has sub nodes & sub nodes has content ...................",scheduleNodePathName);
        try {
            String subNodeContent = checkSubNodesHasContent(scheduleNodePathName);
            if (!StringUtils.isEmpty(subNodeContent)) {
                log.info("schedule {} still running, acquire lock fail", scheduleBean.getClassFullName());
                return acquireSuccess;
            }
        } catch (Exception e) {
            log.error("node {} check has sub nodes & sub nodes has content error :{}.", scheduleNodePathName, e);
            return acquireSuccess;
        }

        log.info("schedule {} acquire success.", scheduleBean.getClassFullName());
        acquireSuccess = true;
        return acquireSuccess;
    }

    /**
     * 尝试获取锁：该方法的作用是防止某个具体schedule在所有节点未执行完任务，但是上次获取到锁的节点突然掉线
     * 导致锁节点（临时节点）自动删除
     * true :尝试成功
     * false :尝试失败
     * @param scheduleLockNodePathName
     * @return
     */
    public boolean tryAcquireLock(String scheduleLockNodePathName) {
        boolean acquireSuccess = true;
        try {
            curatorFrameworkUtils.createEphemeraLNodeWithOutCheckExists(scheduleLockNodePathName);
        } catch (Exception e) {
            log.info("{} try acquire lock fail ",scheduleLockNodePathName);
            // TODO 扫描schedule impl 节点，如果没有节点或者全部节点的内容都为空， 释放锁
            acquireSuccess = false;
        }
        return acquireSuccess;
    }

    /**
     * 检查节点下是否有节点并且节点下是否有内容
     * @param parentNodePathName
     * @return 返回结果为空，代表子没有节点或者子节点下没有内容，
     */
    public String checkSubNodesHasContent(String parentNodePathName) throws Exception {
        List<String> subNodes = curatorFrameworkUtils.getChildrenNode(parentNodePathName);
        if (!CollectionUtils.isEmpty(subNodes)) {
            for (String subNodePathName : subNodes) {
                String subNodeFullPathName = parentNodePathName.concat(DistributeScheduleConstant.DIRECTORY_CHARACTER).concat(subNodePathName);
                String subNodeContent = curatorFrameworkUtils.getNodeData(subNodeFullPathName);
                if (!StringUtils.isEmpty(subNodeContent)) {
                    return subNodeContent;
                }
            }
        }
        return null;
    }

    /**
     * 释放锁
     * @param scheduleBean
     */
    @Async
    public void releaseLockGuaranteed(ScheduleBean scheduleBean) {
        String scheduleLockNodePathName = DistributeScheduleConstant.DIRECTORY_CHARACTER.concat(DistributeScheduleConstant.NODE_LOCK)
                .concat(DistributeScheduleConstant.DIRECTORY_CHARACTER).concat(scheduleBean.getClassFullName());
        while (true) {
            try {
                curatorFrameworkUtils.deleteNodePath(scheduleLockNodePathName);
                break;
            } catch (Exception e) {
                if (e instanceof KeeperException.NoNodeException) {
                    break;
                }
            }
        }
    }

    /**
     * 异步（应用如果开启支持异步）并且在结点存在的情况下，一定清空当前任务结点内容
     * @param scheduleBean
     */
    @Async
    public void emptyCurrentScheduleImplNodeContentGuaranteed(ScheduleBean scheduleBean) {
        String scheduleImplNodePathName = ScheduleUtil.generateServiceImplNodePathName(scheduleBean);
        while (true) {
            try {
                curatorFrameworkUtils.emptyNodeContent(scheduleImplNodePathName);
                break;
            } catch (Exception e) {
                if (e instanceof KeeperException.NoNodeException) {
                    break;
                }
            }

        }
    }

    /**
     * 异步（应用如果开启支持异步）并且在结点存在的情况下，一定将数据写到结点内容中去
     * @param scheduleBean
     * @param data
     */
    @Async
    public void writeData2CurrentScheduleImplNodeGuaranteed(ScheduleBean scheduleBean, String data) {
        String scheduleImplNodePathName = ScheduleUtil.generateServiceImplNodePathName(scheduleBean);
        while (true) {
            try {
                curatorFrameworkUtils.setDataToNode(scheduleImplNodePathName,data.getBytes());
                break;
            } catch (Exception e) {
                if (e instanceof KeeperException.NoNodeException) {
                    break;
                }
            }

        }
    }
}
