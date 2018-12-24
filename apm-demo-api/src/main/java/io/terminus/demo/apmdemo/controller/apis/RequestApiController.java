package io.terminus.demo.apmdemo.controller.apis;

import io.terminus.demo.apmdemo.parser.utils.GsonUtils;
import io.terminus.demo.apmdemo.utils.TimeUtil;
import io.terminus.demo.dao.HttpService;
import io.terminus.demo.utils.Dic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping(path = "/api", method = RequestMethod.GET)
public class RequestApiController {

    private Logger logger = LoggerFactory.getLogger(RequestApiController.class);

    @GetMapping("/tick")
    @ResponseBody
    public void slow(HttpServletResponse resp, @RequestParam(defaultValue = "10") int count) throws InterruptedException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        if(count < 0) {
            count = 1 + (int)(Math.random()*15.0);
        }
        for(int i = 0; i < count; i++) {
            out.print("var tick_" + i + " = " + GsonUtils.toJson(TimeUtil.nowText()) + ";\n");
            Thread.sleep(1000);
        }
        out.flush();
    }

    @GetMapping("/sleep")
    @ResponseBody
    public String sleep(HttpServletResponse resp, @RequestParam(defaultValue = "10") int seconds) throws InterruptedException, IOException {
        Thread.sleep(seconds*1000);
        return "Sleep " + seconds + " OK";
    }

    @GetMapping("/time")
    @ResponseBody
    public Map<String,Object> time() {
        return new Dic("time", TimeUtil.nowText());
    }

    @PostMapping("/echo")
    @ResponseBody
    public String echo(@RequestBody String body) {
        return body;
    }

    @PostMapping("/hole")
    @ResponseBody
    public String helo(@RequestBody String body) {
        return "OK";
    }

    @PostMapping("/falls")
    @ResponseBody
    public String falls(@RequestParam(defaultValue = "2000") int count) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < count; i++) {
            sb.append(i);
            sb.append("\n");
        }
        return sb.toString();
    }

    @GetMapping("/400")
    @ResponseBody
    public ResponseEntity<Dic> badRequest() {
        return new ResponseEntity<>(new Dic("error", "this is Bad Request for test"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/500")
    @ResponseBody
    public ResponseEntity<Dic> serverError() {
        return new ResponseEntity<>(new Dic("error", "this is Internal Server Error for test"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/http/get")
    @ResponseBody
    public String httpRequest(HttpServletResponse resp, String url) throws IOException {
        HttpService.Response res = HttpService.request(url);
        resp.setStatus(res.getStatus());
        return res.getBody();
    }

    @GetMapping("/log")
    @ResponseBody
    public String log(HttpServletResponse resp, String level, String msg) throws IOException {
        if("info".equals(level)) {
            logger.info(msg);
        } else {
            logger.error(msg);
        }
        return msg;
    }
}
