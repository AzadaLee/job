package com.ppdai.tars.job.framework;

import com.ppdai.tars.job.annotation.CuratorComponent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class CuratorClassProcessor implements BeanPostProcessor {

    @Autowired
    private CuratorClient curatorClient;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        CuratorComponent annotation = bean.getClass().getAnnotation(CuratorComponent.class);
        if (null != annotation) {
            String serviceModuleName = annotation.serviceModule();
            String classFullName = bean.getClass().getTypeName();
            curatorClient.createServiceNode(serviceModuleName, classFullName);
        }
        return bean;
    }
}
