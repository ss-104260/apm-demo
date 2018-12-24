package io.terminus.demo.apmdemo.parser.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qiniu.ip17mon.LocationInfo;
import qiniu.ip17mon.Locator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarFile;

public class IPDB implements java.io.Serializable {
    private final static Logger logger = LoggerFactory.getLogger(IPDB.class);
    private static Locator locator = null;

    public static LocationInfo find(String ip) {
        if(locator == null) {
            return new LocationInfo("中国","局域网","局域网","");
        }
        LocationInfo info = locator.find(ip);
        if(info == null) {
            return new LocationInfo("中国","局域网","局域网","");
        }
        return info;
    }

    static {
        try {
            File file = getFile("ipdb.dat");
            if(file == null) {
                InputStream is = getStream("ipdb.dat");
                if(is != null) {
                    locator = Locator.loadFromStreamOld(is);
                    is.close();
                } else {
                    logger.error("ipdb.dat not exist");
                }
            } else {
                locator = Locator.loadFromLocal(file.getPath());
            }
        } catch (Exception e) {
            logger.error("ipdb load failed.",e);
        }
    }

    private static File getFile(String name) {
        File file = new File(name);
        if(!file.exists()) {
            URL url = IPDB.class.getClassLoader().getResource(name);
            System.out.println(url);
            if(url != null) {
                file = new File(url.getFile());
                if(file.exists()) {
                    return file;
                }
            }
            file = new File("/mnt/mesos/sandbox/" + name);
            if(file.exists()) {
                return file;
            }
            return null;
        }
        return file;
    }

    private static InputStream getStream(String name) {
        return IPDB.class.getClassLoader().getResourceAsStream(name);
    }
}
