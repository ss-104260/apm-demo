package io.terminus.demo.mock;

import io.terminus.demo.dao.HttpService;
import io.terminus.demo.service.MockService;
import io.terminus.demo.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author: liuhaoyang
 * @create: 2019-01-25 11:03
 **/
@Slf4j
@Service
public class MockHttpClient {

    @Value("${collector}")
    private String collector = "http://collector.marathon.l4lb.thisdcos.directory:7076";

    @Autowired
    private MockService mockService;

    public void mockLog(int thread, int request, int sizePerRequest) {
        Runnable task = () -> {
            List<LogRequest> logs = new ArrayList<>();
            for (int i = 0; i < sizePerRequest; i++) {
                LogRequest log = new LogRequest();
                log.setTimestamp(DateUtils.currentTimeNano());
                log.setId("mock-" + UUID.randomUUID().toString());
                log.setSource("container");
                log.setContent("William Shakespeare was a renowned English poet, playwright, and actor born in 1564 in Stratford-upon-Avon. His birthday is most commonly celebrated on 23 April (see When was Shakespeare born), which is also believed to be the date he died on in 1616.");
                log.setStream("stdout");
                Map<String, String> tags = new HashMap<>();
                tags.put("level", "info");
                tags.put("request_id", UUID.randomUUID().toString());
                log.setTags(tags);
                logs.add(log);
            }
            try {
                HttpService.post(collector + "/collect/logs/container", logs);
            } catch (IOException e) {
                log.error("mock log error.", e);
            }
        };
        mockService.mock(thread, request, task);
    }

    public void mockTrace(int thread, int request, int sizePerRequest) {
        Runnable task = () -> {
            List<MetricEvent> metricEvents = new ArrayList<>();
            for (int i = 0; i < sizePerRequest; i++) {
                MetricEvent event = new MetricEvent();
                long timeStamp = DateUtils.currentTimeNano();
                event.setName("span");
                event.setTimestamp(timeStamp);
                Map<String, Object> fields = new HashMap<>(3);
                fields.put("start_time", timeStamp);
                fields.put("end_time", timeStamp + 3000);
                fields.put("duration", 3000);
                event.setFields(fields);
                Map<String, String> tags = new HashMap<>();


                metricEvents.add(event);
            }
            try {
                HttpService.post(collector + "/collect/trace", metricEvents);
            } catch (IOException e) {
                log.error("mock trace error.", e);
            }
        };
        mockService.mock(thread, request, task);
    }
}
