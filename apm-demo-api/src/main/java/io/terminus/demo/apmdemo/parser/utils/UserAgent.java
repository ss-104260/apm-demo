package io.terminus.demo.apmdemo.parser.utils;

import com.decibel.uasparser.OnlineUpdater;
import com.decibel.uasparser.UASparser;
import com.decibel.uasparser.UserAgentInfo;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

@Data
public class UserAgent {
    private final static Logger logger = LoggerFactory.getLogger(UserAgent.class);

    private static UASparser parser = null;
    static  {
        try {
            parser = new UASparser(OnlineUpdater.getVendoredInputStream());
        } catch (Exception e) {
            logger.error("init UASparser fail.", e);
        }
    }

    public final static String OTHER = "其他";

    public String browser = OTHER;
    public String browserVersion = "";
    public String os = OTHER;
    public String osVersion = "";
    public String device = OTHER;

    public static UserAgent parse(String ua) {
        UserAgent result = new UserAgent();
        if(ua == null || "".equals(ua)) {
            return result;
        }
        UserAgentInfo info = null;
        try {
            info = parser.parse(ua);
        } catch (IOException e) {
            return  result;
        }
        if(info == null) return result;
        String val = info.getOsName();
        if(val != null && !"unknown".equals(val)) {
            result.os = val;
        }
        val = info.getUaFamily();
        if(val != null && !"unknown".equals(val)) {
            result.browser = val;
        }
        val = info.getBrowserVersionInfo();
        if(val != null && !"unknown".equals(val)) {
            result.browserVersion = val;
        }
        val = info.getDeviceType();
        if(val != null && !"unknown".equals(val)) {
            result.device = val;
        }
        return result;
    }
}
