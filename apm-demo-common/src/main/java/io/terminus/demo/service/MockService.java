package io.terminus.demo.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: liuhaoyang
 * @create: 2019-01-25 10:52
 **/
@Service
public class MockService {

    public void mock(int concurrent, int taskCount, Runnable runnable) {
        ExecutorService executorService = Executors.newFixedThreadPool(concurrent);
        for (int i = 0; i < taskCount; i++) {
            executorService.execute(runnable);
        }
    }
}
