package io.terminus.demo.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;

public class HttpService {
    @Data
    @AllArgsConstructor
    public static class Response implements Serializable {
        private int status;
        private String body;
    }

    public static Response request(String url) throws IOException {
        CloseableHttpClient httpCilent2 = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(30000)
                .setSocketTimeout(30000)
                .setRedirectsEnabled(true)
                .build();
        HttpGet httpGet2 = new HttpGet(url);
        httpGet2.setConfig(requestConfig);
        try {
            HttpResponse httpResponse = httpCilent2.execute(httpGet2);
            String srtResult = EntityUtils.toString(httpResponse.getEntity(), Charset.forName("UTF-8"));
            return new Response(httpResponse.getStatusLine().getStatusCode(),srtResult);
        } finally {
            httpCilent2.close();
        }
    }
}
