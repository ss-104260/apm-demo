package io.terminus.demo.apmdemo.controller.apis;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import io.terminus.demo.apmdemo.dao.MysqlMapper;
import io.terminus.demo.apmdemo.parser.utils.GsonUtils;
import io.terminus.demo.model.MetricMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(path = "/api/mysql", method = RequestMethod.GET)
public class MysqlApiController {

    @Autowired
    private MysqlMapper mapper;

    @GetMapping("/dbs")
    @ResponseBody
    public List<String> showDBs() {
        return mapper.showDBs();
    }

    @GetMapping("/tables")
    @ResponseBody
    public List<String> showTables() {
        return mapper.showTables();
    }

    @GetMapping("/sleep")
    @ResponseBody
    public int dbSleep(@RequestParam(defaultValue = "5") int seconds) {
        return mapper.sleep(seconds);
    }

    @GetMapping("/now")
    @ResponseBody
    public String dbTime() {
        return mapper.now();
    }

    @GetMapping("/users")
    @ResponseBody
    public List<String> mysqlUsers(HttpServletResponse resp, String url) throws IOException {
        return mapper.users();
    }

    @GetMapping("/metrics")
    @ResponseBody
    public List<MetricMeta> mysqlMetrics(HttpServletResponse resp) throws IOException {
        return mapper.metrics();
    }

    @GetMapping("/metrics/{id}")
    @ResponseBody
    public MetricMeta mysqlMetrics(@PathVariable("id") long id) throws IOException {
        return mapper.getMetricsById(id);
    }

    @PutMapping("/metric/edit")
    @ResponseBody
    public String mysqlSetMetrics(String data) throws IOException {
        MetricMeta meta = GsonUtils.fromJson(data, MetricMeta.class);
        try {
            mapper.setMetrics(meta);
            return "OK";
        } catch (Exception ex) {
            if (ex.getCause() != null && ex.getCause() instanceof MySQLIntegrityConstraintViolationException) {
                return ex.getCause().toString();
            }
            return ex.toString();
        }
    }

    @PostMapping("/metric")
    @ResponseBody
    public String mysqlMetrics(HttpServletResponse resp, String data) throws IOException {
        MetricMeta meta = GsonUtils.fromJson(data, MetricMeta.class);
        try {
            mapper.addMetrics(meta);
            return "OK";
        } catch (Exception ex) {
            if (ex.getCause() != null && ex.getCause() instanceof MySQLIntegrityConstraintViolationException) {
                return ex.getCause().toString();
            }
            return ex.toString();
        }
    }
}
