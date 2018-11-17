package com.azada.job.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 定时任务实例结点内容清空监听器
 *
 * @author azada
 */
@Component
public class ScheduleImpNodeEmptyContentListener implements ApplicationListener<ScheduleImpNodeEvent> {


    @Override
    public void onApplicationEvent(ScheduleImpNodeEvent scheduleImpNodeEvent) {

    }
}
