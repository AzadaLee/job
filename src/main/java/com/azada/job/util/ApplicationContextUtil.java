package com.azada.job.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 容器应用类
 *
 * @author taoxiuma
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Object getBeanByName(String beanName){
        return applicationContext.getBean(beanName);
    }

    public Object getBeanByClazz(Class<?> clazz) {
        return applicationContext.getBean(clazz);
    }
}
