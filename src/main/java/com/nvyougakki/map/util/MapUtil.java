package com.nvyougakki.map.util;

import com.nvyougakki.map.bean.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import sun.net.www.http.HttpClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @ClassName MapUItil
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2019/12/13 21:03
 * 百度地图图层下载
 */
public class MapUtil {


    public static InputStream getPicIps(String _url) throws IOException {
        URL url = null;
        HttpURLConnection connection = null;
        InputStream ips = null;
        OutputStream ops = null;

        url = new URL(_url);
        connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        if(connection.getResponseCode() == 200) {
            ips = connection.getInputStream();
        }
//        connection.disconnect();
        return ips;

    }
    private HttpClientBuilder clientbuilder = null;
    public MapUtil(){

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager ();
        connectionManager.setMaxTotal(100);
        clientbuilder =
                HttpClients.custom().setConnectionManager(connectionManager);
    }

    public InputStream getIpsByHttpClient(String url){

        CloseableHttpClient client = clientbuilder.build();

        HttpGet httpget = new HttpGet(url);
        try {
           return client.execute(httpget).getEntity().getContent();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }



    //
/*    public static ZoomRange getZoomRange(Point minPoint, Point maxPoint, int zoom){
        PicAxis min = mercatorToPicAxis(minPoint, zoom);
        PicAxis max = mercatorToPicAxis(maxPoint, zoom);
        ZoomRange result = new ZoomRange(min, max, zoom);
        result.setZ(zoom);
        return result;
    }*/




}
