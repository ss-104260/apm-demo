package io.terminus.demo.service;

import com.google.gson.Gson;
import io.terminus.demo.mock.MetricEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author: liuhaoyang
 * @create: 2019-01-02 14:28
 **/
@Slf4j
@Service
public class MetricService {
    private static Gson gson = new Gson();
    private static final Charset charset = Charset.forName("UTF-8");

    private boolean init;
    private InetSocketAddress socketAddress;
    private DatagramSocket socket;

    private static String getEnv(String key) {
        return System.getenv(key);
    }

    @Getter
    private String host = getEnv("HOST");

    @Getter
    private String projectId = getEnv("DICE_PROJECT");

    @Getter
    private String projectName = getEnv("DICE_PROJECT_NAME");

    @Getter
    private String applicationName = getEnv("DICE_APPLICATION");

    @Getter
    private String applicationId = getEnv("DICE_APPLICATION_NAME");

    @Getter
    private String runtimeId = getEnv("DICE_RUNTIME");

    @Getter
    private String runtimeName = getEnv("DICE_RUNTIME_NAME");

    @Getter
    private String serviceName = getEnv("DICE_SERVICE");

    @Getter
    private String workspace = getEnv("DICE_WORKSPACE");

    @Getter
    private String terminusKey = getEnv("TERMINUS_KEY");

    public MetricService() throws SocketException {
        this.socketAddress = new InetSocketAddress(host, 7082);
        log.info("Udp sender address " + socketAddress.toString());
        try {
            this.socket = new DatagramSocket();
            init = true;
        } catch (SocketException e) {
            init = false;
            log.error("Bind udp client address fail.", e);
            throw e;
        }
    }

    public void send(MetricEvent... metrics) {
        if (!init) {
            return;
        }
        if (metrics.length <= 0) {
            return;
        }
        for (MetricEvent event : metrics) {
            Map<String, String> tags = event.getTags();
            tags.put("project_id", projectId);
            tags.put("project_name", projectName);
            tags.put("application_id", applicationId);
            tags.put("application_name", applicationName);
            tags.put("runtime_id", runtimeId);
            tags.put("runtime_name", runtimeName);
            tags.put("service_name", serviceName);
            tags.put("workspace", workspace);
            tags.put("terminus_key", terminusKey);
        }
        byte[] data = gson.toJson(metrics).getBytes(charset);
        try {
            socket.send(new DatagramPacket(data, 0, data.length, socketAddress));
            log.debug("Send {} data to collector proxy success. \n {}", metrics.length, new String(data));
        } catch (IOException e) {
            log.error("Send data fail.", e);
        }
    }
}
