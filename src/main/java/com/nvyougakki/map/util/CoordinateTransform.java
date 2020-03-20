package com.nvyougakki.map.util;

import com.alibaba.fastjson.JSON;
import com.nvyougakki.map.bean.Config;
import com.nvyougakki.map.bean.PicAxis;
import com.nvyougakki.map.bean.Point;
import com.nvyougakki.map.bean.Tile;
import org.apache.commons.io.IOUtils;
import sun.net.www.http.HttpClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 百度坐标转其他坐标
 */
public class CoordinateTransform {

    private final static String AK = "qi50bY3STQhYsDmISw8yP1EI8UUbppf6";

    private final static String URL = "http://api.map.baidu.com/geoconv/v1/";

    /**
     *
     * @param point
     * @param from
     * 坐标转换方法
     * 1：GPS设备获取的角度坐标，WGS84坐标;
     * 2：GPS获取的米制坐标、sogou地图所用坐标;
     * 3：google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标，国测局（GCJ02）坐标;
     * 4：3中列表地图坐标对应的米制坐标;
     * 5：百度地图采用的经纬度坐标;
     * 6：百度地图采用的米制坐标;
     * 7：mapbar地图坐标;
     * 8：51地图坐标
     * @param to
     * 5：bd09ll(百度经纬度坐标);
     * 6：bd09mc(百度米制经纬度坐标)
     * @return
     */
    public static Point coorTransform(Point point, int from, int to){
        URL url = null;
        HttpURLConnection connection = null;
        try {
            String httpUrl = URL + "?coords="+point.getX() + "," + point.getY() +"&from="+from+"&to="+to+"&ak=" + AK;
            System.out.println(httpUrl);
            url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream ips = null;
            String sb = null;
            if(connection.getResponseCode() == 200) {
                ips = connection.getInputStream();
                byte[] b = new byte[ips.available()];
                ips.read(b);
                sb = new String(b, "utf-8");
            }
            System.out.println(sb);
            Map map = JSON.parseObject(sb, Map.class);
            String result = map.get("result").toString();
            return JSON.parseObject(result.substring(1, result.length() - 1), Point.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    //百度经纬度转化成墨卡托坐标
    public static Point bd2mc(Point point){
        return coorTransform(point, 5, 6);
    }
    //墨卡托坐标转图块坐标
    public static PicAxis mc2PicAxis(Point point, int z) {
        return mc2PicAxis(point.getX(), point.getY(), z);
    }

    /**
     *墨卡托转图块坐标
     **/
    public static PicAxis mc2PicAxis(double x, double y, int z) {
        PicAxis result = new PicAxis();
        result.setX((int) Math.floor(x * Math.pow(2, z - 18)/256));
        result.setY((int) Math.floor(y * Math.pow(2, z - 18)/256));
        return result;
    }

    //墨卡托坐标矩形转化成图层类
    public static Tile mc2Tile(Point minPoint, Point maxPoint, int zoom){
        PicAxis picAxis = mc2PicAxis(minPoint, zoom);
        PicAxis picAxis1 = mc2PicAxis(maxPoint, zoom);
        return new Tile(picAxis, picAxis1, zoom);
    }


    public static List<Tile> mc2TileList(Point minPoint, Point maxPoint, List<Integer> zoomList){
        List<Tile> result = new ArrayList<>(zoomList.size());
        for(int z : zoomList) {
            result.add(mc2Tile(minPoint, maxPoint, z));
        }
        return result;
    }

    public static List<Tile> mc2TileList(Config config){
        return mc2TileList(config.getMinMcPoint(), config.getMaxMcPoint(), config.getZoomArr());
    }

    public static List<Tile> mc2TileList(Point minPoint, Point maxPoint, int[] zoomArr){
        List<Tile> result = new ArrayList<>(zoomArr.length);
        for(int z : zoomArr) {
            result.add(mc2Tile(minPoint, maxPoint, z));
        }
        return result;
    }

}
