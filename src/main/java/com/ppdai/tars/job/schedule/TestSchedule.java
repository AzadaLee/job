package com.ppdai.tars.job.schedule;

import com.ppdai.tars.job.annotation.CuratorComponent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@CuratorComponent
public class TestSchedule extends BaseSchedule {


    @Override
    @Scheduled(fixedDelay = 60 * 1000)
    public void start() {
        init();
    }

    @Override
    public List<Integer> getIdList() {
        Integer[] arr = {1,2,3,4,5,6,7,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24};
        List<Integer> testList = Arrays.asList(arr);
        return testList;
    }

    @Override
    public void prosess(String ids) {

    }

}
