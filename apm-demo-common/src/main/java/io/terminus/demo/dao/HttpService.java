package io.terminus.demo.dao;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;

public class HttpService {

    private static Gson gson = new Gson();
    private static HttpClient httpClient = buildClient();

    @Data
    @AllArgsConstructor
    public static class Response implements Serializable {
        private int status;
        private String body;
    }

    public static Response request(String url) throws IOException {
        HttpGet httpGet2 = new HttpGet(url);
        httpGet2.setConfig(buildConfig());
        HttpResponse httpResponse = httpClient.execute(httpGet2);
        String srtResult = EntityUtils.toString(httpResponse.getEntity(), Charset.forName("UTF-8"));
        return new Response(httpResponse.getStatusLine().getStatusCode(), srtResult);
    }

    public static <T> HttpResponse post(String url, T data) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(buildConfig());
        httpPost.setEntity(new StringEntity(gson.toJson(data), "application/json"));
        return httpClient.execute(httpPost);
    }

    private static HttpClient buildClient() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(1024);
        cm.setMaxTotal(1024);
        SocketConfig socketConfig = SocketConfig.custom()
                //.setTcpNoDelay(true)     //是否立即发送数据，设置为true会关闭Socket缓冲，默认为false
                .setSoReuseAddress(true) //是否可以在一个进程关闭Socket后，即使它还没有释放端口，其它进程还可以立即重用端口
                .setSoTimeout(500)       //接收数据的等待超时时间，单位ms
                .setSoLinger(60)         //关闭Socket时，要么发送完所有数据，要么等待60s后，就关闭连接，此时socket.close()是阻塞的
                .setSoKeepAlive(true)    //开启监视TCP连接是否有效
                .build();
        cm.setDefaultSocketConfig(socketConfig);
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    private static RequestConfig buildConfig() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(30000)
                .setSocketTimeout(30000)
                .setRedirectsEnabled(true)
                .build();
        return requestConfig;
    }
}
