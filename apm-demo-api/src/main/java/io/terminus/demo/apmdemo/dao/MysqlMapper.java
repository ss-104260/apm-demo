package io.terminus.demo.apmdemo.dao;

import io.terminus.demo.model.MetricMeta;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MysqlMapper {

    @Select("SELECT SLEEP(#{seconds});")
    int sleep(int seconds);

    @Select("SHOW DATABASES;")
    List<String> showDBs();

    @Select("SHOW TABLES;")
    List<String> showTables();

    @Select("SELECT NOW();")
    String now();

    @Select("SELECT `User` FROM `mysql`.`user`;")
    List<String> users();

    static final String metricTable = "apm_metrics";

    @Update("CREATE TABLE IF NOT EXISTS `" + metricTable + "` (\n" +
            "  `id` int(10) NOT NULL AUTO_INCREMENT,\n" +
            "  `name` varchar(64) NOT NULL,\n" +
            "  `tags` varchar(4096) NOT NULL,\n" +
            "  `fields` varchar(4096) NOT NULL,\n" +
            "  `desc` varchar(512) NOT NULL,\n" +
            "  `agg1m` tinyint(1) NOT NULL,\n" +
            "  `sourceToEs` tinyint(1) NOT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `name_unique` (`name`)\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;")
    void createMetricsTable();

    @Select("SELECT * FROM `" + metricTable + "`;")
    List<MetricMeta> metrics();

    @Select("INSERT INTO `" + metricTable + "`(`name`,`tags`,`fields`,`desc`,`agg1m`,`sourceToEs`) VALUES(#{name},#{tags},#{fields},#{desc},#{agg1m},#{sourceToEs})")
    void addMetrics(MetricMeta meta);

    @Select("UPDATE `" + metricTable + "` SET `name`=#{name},`tags`=#{tags},`fields`=#{fields},`desc`=#{desc},`agg1m`=#{agg1m},`sourceToEs`=#{sourceToEs} WHERE id=#{id};")
    void setMetrics(MetricMeta meta);

    @Select("SELECT * FROM `" + metricTable + "` WHERE id=#{id};")
    MetricMeta getMetricsById(long id);
}
