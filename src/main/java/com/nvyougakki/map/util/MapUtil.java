package com.nvyougakki.map.util;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private static HttpClientBuilder clientbuilder = null;
    static {

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager ();
        connectionManager.setMaxTotal(100);
        clientbuilder =
                HttpClients.custom().setConnectionManager(connectionManager);
    }

    public static InputStream getIpsByHttpClient(String url){

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
