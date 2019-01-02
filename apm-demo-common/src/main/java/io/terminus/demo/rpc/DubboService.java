package io.terminus.demo.rpc;

import io.terminus.demo.dao.HttpService;
import io.terminus.demo.utils.Dic;
import java.util.List;

public interface DubboService {

    Dic hello();

    void error() throws Exception;

    List<String> mysqlUsers() throws Exception;

    String redisGet(String key) throws Exception;

    HttpService.Response httpRequest(String url) throws Exception;

    
}
