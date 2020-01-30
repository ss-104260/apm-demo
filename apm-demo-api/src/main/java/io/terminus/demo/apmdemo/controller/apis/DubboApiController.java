package io.terminus.demo.apmdemo.controller.apis;

import io.terminus.boot.rpc.common.annotation.RpcConsumer;
import io.terminus.demo.dao.HttpService;
import io.terminus.demo.rpc.DubboService;
import io.terminus.demo.utils.Dic;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(path = "/api/dubbo", method = RequestMethod.GET)
public class DubboApiController {

    // @RpcConsumer
    private DubboService dubboService;

    @GetMapping("/hello")
    @ResponseBody
    public Dic hello() throws Exception {
        return dubboService.hello();
    }

    @GetMapping("/exception")
    @ResponseBody
    public String exception() throws Exception {
        dubboService.error();
        return "OK";
    }

    @GetMapping("/mysql/users")
    @ResponseBody
    public List<String> mysqlUsers() throws Exception {
        return dubboService.mysqlUsers();
    }

    @GetMapping("/redis/get")
    @ResponseBody
    public String redisGet(String key) throws Exception {
        return dubboService.redisGet(key);
    }

    @GetMapping("/http/get")
    @ResponseBody
    public String httpRequest(HttpServletResponse resp, String url) throws Exception {
        HttpService.Response res = dubboService.httpRequest(url);
        resp.setStatus(res.getStatus());
        return res.getBody();
    }
}
