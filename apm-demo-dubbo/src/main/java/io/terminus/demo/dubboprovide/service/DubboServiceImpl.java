package io.terminus.demo.dubboprovide.service;

import io.terminus.boot.rpc.common.annotation.RpcProvider;
import io.terminus.demo.dao.HttpService;
import io.terminus.demo.dubboprovide.dao.MysqlMapper;
import io.terminus.demo.dubboprovide.dao.RedisService;
import io.terminus.demo.rpc.DubboService;
import io.terminus.demo.service.MetricService;
import io.terminus.demo.utils.Dic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import java.util.Date;
import java.util.List;

@Service
@RpcProvider
public class DubboServiceImpl implements DubboService {

    private Logger logger = LoggerFactory.getLogger(DubboServiceImpl.class);

    @Autowired
    private MysqlMapper mapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MetricService metricService;

    @Override
    public Dic hello() {
        logger.info("time " + new Date().toString());
        return new Dic().set("message", "hello");
    }

    @Override
    public void error() throws Exception {
        throw new Exception("dubbo provide error");
    }

    @Override
    public List<String> mysqlUsers() throws Exception {
        return mapper.users();
    }

    @Override
    public String redisGet(String key) throws Exception {
        Jedis jedis = null;
        try {
            jedis = redisService.getJedis();
            return jedis.get("apm-demo-"+key);
        } finally {
            if(jedis != null) jedis.close();
        }
    }

    @Override
    public HttpService.Response httpRequest(String url) throws Exception {
        return HttpService.request(url);
    }

}
