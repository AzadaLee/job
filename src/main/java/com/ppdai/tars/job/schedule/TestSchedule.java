package com.ppdai.tars.job.schedule;

import com.ppdai.tars.job.annotation.CuratorComponent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@CuratorComponent
public class TestSchedule extends BaseSchedule {


    @Override
    @Scheduled(fixedDelay = 60 * 1000)
    public void start() {
        init();
    }

    @Override
    public void prosess(String ids) {

    }
}
