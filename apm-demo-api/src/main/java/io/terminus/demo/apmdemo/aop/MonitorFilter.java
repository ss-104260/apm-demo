package io.terminus.demo.apmdemo.aop;

import io.terminus.dice.monitor.sdk.metrics.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: liuhaoyang
 * @create: 2019-01-21 14:50
 **/
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "MonitorFilter")
public class MonitorFilter implements Filter {

    private static Counter requestCounter;
    private static Counter globalRequestCounter;
    private static Meter requestMeter;
    private static Histogram respHistogram;
    private static Summary respSummary;

    static {
        CounterBuilder requestCounterBuilder = new CounterBuilder("request_count", "REQUEST");
        requestCounterBuilder.setLabels(new String[]{"method", "url", "status_code"});
        requestCounter = MetricRegistry.registerCounter(requestCounterBuilder);

        CounterBuilder globalRequestCounterBuilder = new CounterBuilder("global_request_count", "REQUEST");
        globalRequestCounter = MetricRegistry.registerCounter(globalRequestCounterBuilder);

        MeterBuilder meterBuilder = new MeterBuilder("request_rate", "REQUEST");
        meterBuilder.setLabels(new String[]{"method", "url", "status_code"});
        requestMeter = MetricRegistry.registerMeter(meterBuilder);

        HistogramBuilder histogramBuilder = new HistogramBuilder("rt_histogram", "RT");
        histogramBuilder.setLabels(new String[]{"method", "url", "status_code"});
        respHistogram = MetricRegistry.registerHistogram(histogramBuilder);

        SummaryBuilder summaryBuilder = new SummaryBuilder("rt_summary", "RT");
        summaryBuilder.setLabels(new String[]{"method", "url", "status_code"});
        respSummary = MetricRegistry.registerSummary(summaryBuilder);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            long end = System.currentTimeMillis() - start;
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            globalRequestCounter.inc();
            String[] args = new String[]{request.getMethod(), request.getRequestURI(), String.valueOf(response.getStatus())};
            requestCounter.inc(args);
            requestMeter.mark(1, args);
            respHistogram.observe(end - start, args);
            respSummary.observe(end - start, args);
        }
    }
}
