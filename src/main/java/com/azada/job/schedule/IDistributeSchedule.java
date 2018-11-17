package com.azada.job.schedule;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface IDistributeSchedule {

    /**
     * 开始任务
     */
    void start();

    /**
     * 获取待处理数据的id集合
     * @return
     */
    List<Long> getIdList();

    /**
     * 执行具体业务
     * @param
     */
    void doBusiness(Long minId, Long maxId);

    /**
     * 由基础类重写
     * @param ids
     */
    void process(@NotNull String ids);

}
