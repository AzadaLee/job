package com.azada.job.listener;

import com.azada.job.config.TarsException;
import com.azada.job.schedule.ISchedule;
import com.azada.job.util.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 定时任务实例监听
 *
 * @author taoxiuma
 */
@Component
@Slf4j
public class ScheduleImpNodeListener implements ApplicationListener<ScheduleImpNodeEvent> {

    @Autowired
    private ApplicationContextUtil applicationContextUtil;

    @Override
    public void onApplicationEvent(ScheduleImpNodeEvent event) {
        Object o = applicationContextUtil.getBeanByClazz(event.getScheduleImpNodeData().getClazz());
        if (null == o) {
            log.error("{} bean is not found", event.getScheduleImpNodeData().getClassFullName());
            throw new TarsException("");
        }
        String nodeData = event.getScheduleImpNodeData().getNodeData();
        ISchedule schedule = (ISchedule) o;
        schedule.prosess(nodeData);
    }
}
