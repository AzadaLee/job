package com.azada.job.listener;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * 定时任务实例发布
 *
 * @author taoxiuma
 */
@Component
public class ScheduleImpNodePublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publish(ScheduleImpNodeEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
