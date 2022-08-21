package com.nvyougakki.map.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * @ClassName HttpClientUtils
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2020/3/21 23:18
 */
public class HttpClientUtils {
//    private static Logger logger = new Logger();
    // 默认超时时间：10s



    private static final int TIME_OUT = 10 * 1000;

    public static RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setConnectTimeout(TIME_OUT).setConnectionRequestTimeout(TIME_OUT)
            .setSocketTimeout(TIME_OUT).build();

    private static PoolingHttpClientConnectionManager cm = null;
    static{
        LayeredConnectionSocketFactory sslsf = null;
        try{
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        }catch(NoSuchAlgorithmException e){
            System.err.println("创建SSL连接失败...");
            //logger.error("创建SSL连接失败...");
        }
        Registry<ConnectionSocketFactory> sRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        cm = new PoolingHttpClientConnectionManager(sRegistry);
        // 设置最大的连接数
        cm.setMaxTotal(100);
        // 设置每个路由的基础连接数【默认，每个路由基础上的连接不超过2个，总连接数不能超过20】
        cm.setDefaultMaxPerRoute(20);
    }
    public static CloseableHttpClient getHttpClient(){
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm).build();
        return httpClient;
    }


    /**
     * 发送get请求
     * @param url 路径
     * @return
     */
    public static int httpGet(String url, OutputStream ops, int stackDeep) {
        if(stackDeep > 5) return 0;
        InputStream result = null;
        CloseableHttpClient httpClient = getHttpClient();
        // get请求返回结果
        CloseableHttpResponse response = null;
        try {
            // 配置请求超时时间

            // 发送get请求
            HttpGet request = new HttpGet(url);
            request.setHeader("Connection", "keep-alive");
            request.setHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
            request.setHeader("Accept-Encoding", "gzip, deflate");
            request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36 Edg/89.0.774.75\n" +
                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            request.setConfig(REQUEST_CONFIG);
            int flag = 0;
            while(flag < 5) {
                response = httpClient.execute(request);
                // 请求发送成功，并得到响应
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    // 读取服务器返回过来的json字符串数据
                    //String strResult = EntityUtils.toString(response.getEntity());
                    // 把json字符串转换成json对象
                    //  jsonResult = JSONObject.parseObject(strResult);
                    // url = URLDecoder.decode(url, "UTF-8");
                    if(response.getEntity().getContent().available() > 0) {
                        response.getEntity().writeTo(ops);
                        return 1;
                    }

                } else {
                    System.err.println("get请求提交失败:" + url);

                    // logger.error("get请求提交失败:" + url);
                }
                Thread.sleep(3000);
                flag++;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("get请求提交失败:" + url + "，重新请求");
            httpGet(url, ops, stackDeep++);

            //logger.error("get请求提交失败:" + url, e);
        } finally {
            if( response != null){
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (IOException e) {
                    System.err.println("关闭response失败:"+e);
                    //logger.error("关闭response失败:", e);
                }
            }
        }
        return 0;
    }
}
