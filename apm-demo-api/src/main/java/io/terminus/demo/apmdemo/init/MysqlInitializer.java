package io.terminus.demo.apmdemo.init;

import io.terminus.demo.apmdemo.dao.MysqlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class MysqlInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MysqlInitializer.class);

    @Autowired
    MysqlMapper mysqlMapper;

    @Override
    // Spring容器加载完成触发
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        try {
            mysqlMapper.createMetricsTable();
        } catch (Exception e) {
            logger.error("mysql init failed.", e);
        }
    }
}
