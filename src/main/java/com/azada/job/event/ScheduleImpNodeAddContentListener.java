package com.azada.job.event;

import com.azada.job.config.TarsException;
import com.azada.job.schedule.ISchedule;
import com.azada.job.util.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时任务实例结点增加内容监听器
 *
 * @author taoxiuma
 */
@Component
@Slf4j
public class ScheduleImpNodeAddContentListener implements ApplicationListener<ScheduleImpNodeAddContentEvent> {

    @Resource
    private ApplicationContextUtil applicationContextUtil;

    @Override
    public void onApplicationEvent(ScheduleImpNodeAddContentEvent event) {
        Object o = applicationContextUtil.getBeanByClazz(event.getScheduleImpNodeData().getClazz());
        if (null == o) {
            log.error("{} bean is not found", event.getScheduleImpNodeData().getClassFullName());
            throw new TarsException("");
        }
        String nodeData = event.getScheduleImpNodeData().getNodeData();
        ISchedule schedule = (ISchedule) o;

        schedule.doBusiness(nodeData);
    }
}
