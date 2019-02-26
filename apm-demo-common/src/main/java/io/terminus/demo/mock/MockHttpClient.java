package io.terminus.demo.mock;

import io.terminus.demo.dao.HttpService;
import io.terminus.demo.service.MetricService;
import io.terminus.demo.service.MockService;
import io.terminus.demo.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 *
 **/
@Slf4j
@Service
public class MockHttpClient {

    @Value("${collector}")
    private String collector = "http://collector.marathon.l4lb.thisdcos.directory:7076";

    @Autowired
    private MockService mockService;

    @Autowired
    private MetricService metricService;

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
                tags.put("trace_id", UUID.randomUUID().toString());
                tags.put("span_id", UUID.randomUUID().toString());
                tags.put("operation_name", "mock-span");
                tags.put("service_name", metricService.getServiceName());
                tags.put("terminus_key", metricService.getTerminusKey());
                tags.put("runtime_name", metricService.getRuntimeName());
                tags.put("application_name", metricService.getApplicationName());
                tags.put("application_id", metricService.getApplicationId());
                tags.put("runtime_id", metricService.getRuntimeId());
                tags.put("project_id", metricService.getProjectId());
                metricEvents.add(event);
            }
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("trace", metricEvents);
                HttpService.post(collector + "/collect/trace", body);
            } catch (IOException e) {
                log.error("mock trace error.", e);
            }
        };
        mockService.mock(thread, request, task);
    }

    public void mockMetric(int thread, int request, int sizePerRequest) {
        Runnable task = () -> {
            List<MetricEvent> metricEvents = new ArrayList<>();
            for (int i = 0; i < sizePerRequest; i++) {
                MetricEvent event = new MetricEvent();
                long timeStamp = DateUtils.currentTimeNano();
                event.setName("mock-metrics");
                event.setTimestamp(timeStamp);
                Map<String, Object> fields = new HashMap<>(1);
                fields.put("duration", 3000);
                event.setFields(fields);
                Map<String, String> tags = new HashMap<>();
                tags.put("custum_key", UUID.randomUUID().toString());
                tags.put("service_name", metricService.getServiceName());
                tags.put("terminus_key", metricService.getTerminusKey());
                tags.put("runtime_name", metricService.getRuntimeName());
                tags.put("application_name", metricService.getApplicationName());
                tags.put("application_id", metricService.getApplicationId());
                tags.put("runtime_id", metricService.getRuntimeId());
                tags.put("project_id", metricService.getProjectId());
                metricEvents.add(event);
            }
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("metrics", metricEvents);
                HttpService.post(collector + "/collect/metrics", body);
            } catch (IOException e) {
                log.error("mock trace error.", e);
            }
        };
        mockService.mock(thread, request, task);
    }
}
