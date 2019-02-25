package io.terminus.demo.apmdemo.Service;

import io.terminus.demo.service.MetricService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author: liuhaoyang
 * @create: 2019-01-02 15:08
 **/
@Slf4j
@Component
@Async
public class MockMetricService {

    @Value("${mock.metric.count}")
    private int count;
    
    @Autowired
    private MetricService metricService;

    @Scheduled(initialDelay = 60000, fixedDelay = 10000)
    public void mockMetric() {
//        for (int i = 0; i < count; i++) {
//            MetricEvent event = new MetricEvent();
//            event.setName("APM-Demo-Async-Mock");
//            event.setTimestamp(DateUtils.currentTimeNano());
//            Map<String, Object> fields = new HashMap<>();
//            fields.put("random", i);
//            event.setFields(fields);
//            event.setTags(new HashMap<>());
//            metricService.send(event);
//        }

        log.info("Send apm-Demo-Async-Mock.");
    }
}
