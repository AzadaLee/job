package com.ppdai.tars.job.schedule;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ISchedule {

    /**
     * 开始任务
     */
    void start();

    /**
     * 获取待处理数据的id集合
     * @return
     */
    List<Integer> getIdList();

    /**
     * 执行具体业务
     * @param ids
     */
    void prosess(@NotNull String ids);

}
