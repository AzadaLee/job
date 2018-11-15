package com.ppdai.tars.job.schedule;

import javax.validation.constraints.NotNull;

public interface ISchedule {

    /**
     * 开始任务
     */
    void start();

    /**
     * 执行具体业务
     * @param ids
     */
    void prosess(@NotNull String ids);
}
