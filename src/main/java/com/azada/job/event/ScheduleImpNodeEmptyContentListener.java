package com.azada.job.event;

import com.azada.job.constant.DistributeScheduleConstant;
import com.azada.job.framework.DistributeScheduleCuratorComponent;
import com.azada.job.util.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 定时任务实例结点内容清空监听器
 *
 * @author azada
 */
@Component
@Slf4j
public class ScheduleImpNodeEmptyContentListener implements ApplicationListener<ScheduleImpNodeEmptyContentEvent> {

    @Resource
    private DistributeScheduleCuratorComponent distributeScheduleCuratorComponent;

    @Override
    public void onApplicationEvent(ScheduleImpNodeEmptyContentEvent event) {
        String classFullName = event.getScheduleBean().getClassFullName();
        String scheduleServiceName = event.getScheduleBean().getScheduleServiceName();
        String parentNodePathName = DistributeScheduleConstant.DIRECTORY_CHARACTER.concat(scheduleServiceName)
                .concat(DistributeScheduleConstant.DIRECTORY_CHARACTER).concat(classFullName);
        String nodeContent = null;
        try {
            nodeContent = distributeScheduleCuratorComponent.checkSubNodesHasContent(parentNodePathName);
        } catch (Exception e) {
            if (!(e instanceof KeeperException.NoNodeException)) {
                return;
            }
        }
        if (StringUtils.isEmpty(nodeContent)) {
            try {
                distributeScheduleCuratorComponent.releaseLockGuaranteed(event.getScheduleBean());
            } catch (Exception e) {
                log.error("schedule {} release lock error :{}", classFullName, e);
            }
        }
    }
}
