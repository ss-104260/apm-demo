package io.terminus.demo.apmdemo.controller.apis;

import io.terminus.demo.mock.MockHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author: liuhaoyang
 * @create: 2019-01-25 12:04
 **/
@Controller
@RequestMapping(path = "/api/mock", method = RequestMethod.GET)
public class MockApiController {

    @Autowired
    private MockHttpClient mockHttpClient;

    @GetMapping("/log")
    @ResponseBody
    public String log(
            @RequestParam(value = "thread", required = false, defaultValue = "10") Integer thread,
            @RequestParam(value = "count", required = false, defaultValue = "100") Integer count) throws Exception {
        mockHttpClient.mockLog(thread, count);
        return "OK";
    }
}
