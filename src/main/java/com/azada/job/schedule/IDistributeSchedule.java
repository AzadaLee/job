package com.azada.job.schedule;

import com.azada.job.constant.DistributeScheduleConstant;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author taoxiuma
 */
public interface IDistributeSchedule {

    String SWITCHER = DistributeScheduleConstant.SCHEDULE_SWITCHER_DEFAULT_VALUE;

    Integer LEGAL_IDS_SIZE = 2;
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
     * 执行业务
     * @param minId
     * @param maxId
     */
    void doBusiness(Long minId, Long maxId);

    /**
     * 由基础类重写
     * @param ids
     */
    void process(@NotNull String ids);

}
