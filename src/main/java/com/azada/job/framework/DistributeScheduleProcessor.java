package com.azada.job.framework;

import com.azada.job.annotation.DistributeSchedule;
import com.azada.job.bean.ScheduleBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class DistributeScheduleProcessor implements BeanPostProcessor {

    @Autowired
    private CuratorClient curatorClient;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        DistributeSchedule annotation = bean.getClass().getAnnotation(DistributeSchedule.class);
        if (null != annotation) {
            String serviceModuleName = annotation.value();
            String classFullName = bean.getClass().getTypeName();
            Class clazz = bean.getClass();
            ScheduleBean scheduleBean = new ScheduleBean(serviceModuleName, classFullName, clazz);
            curatorClient.createScheduleServiceNode(scheduleBean);
        }
        return bean;
    }
}
