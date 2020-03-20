package com.nvyougakki.map.util;

import com.nvyougakki.map.bean.*;
import org.apache.commons.io.IOUtils;

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



    //
/*    public static ZoomRange getZoomRange(Point minPoint, Point maxPoint, int zoom){
        PicAxis min = mercatorToPicAxis(minPoint, zoom);
        PicAxis max = mercatorToPicAxis(maxPoint, zoom);
        ZoomRange result = new ZoomRange(min, max, zoom);
        result.setZ(zoom);
        return result;
    }*/




}
