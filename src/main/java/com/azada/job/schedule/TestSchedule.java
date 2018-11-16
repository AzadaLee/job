package com.azada.job.schedule;

import com.azada.job.annotation.DistributeSchedule;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@DistributeSchedule
public class TestSchedule extends BaseSchedule {


    @Override
    @Scheduled(fixedDelay = 60 * 1000)
    public void start() {
        init();
    }

    @Override
    public List<Long> getIdList() {
        Long[] arr = {1L,2L,3L,4L,5L,6L,7L,9L,10L,11L,12L,13L,14L,15L,16L,17L,18L,19L,20L,21L,22L,23L,24L};
        List<Long> testList = Arrays.asList(arr);
        return testList;
    }

    @Override
    public void prosess(String ids) {
        System.out.println("success");
    }

}
