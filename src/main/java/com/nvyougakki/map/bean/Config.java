package com.nvyougakki.map.bean;

import com.nvyougakki.map.util.CoordinateTransform;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Properties;

/**
 * @ClassName Config
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2020/3/20 20:55
 */
public class Config {

    private String mapUrl;

    private String today;  //今天年月日

    private String fileRootPath;  //文件根目录

    private String picSuffix;  //图片后缀

    private String mapStyle;  //地图样式

    private int[] zoomArr; //需要抓取的图层数组

    private Point minPoint; //左下经纬度

    private Point maxPoint;  //右上经纬度

    public Config(){
        Properties ps = new Properties();
        InputStream ips = null;
        URL url = null;
        try {
            url = this.getClass().getClassLoader().getResource("config.properties");
            ips = url.openStream();
            ps.load(ips);
            mapUrl = ps.getProperty("mapUrl");
            fileRootPath = ps.getProperty("fileRootPath");
            picSuffix = ps.getProperty("picSuffix");
            mapStyle = ps.getProperty("mapStyle");
            zoomArr = Arrays.asList(ps.getProperty("zoomArr").split(",")).stream().mapToInt(Integer::parseInt).toArray();
            today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String tmpPoint = ps.getProperty("minPoint");
            String[] arr = tmpPoint.split(",");
            minPoint = new Point(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
            tmpPoint = ps.getProperty("maxPoint");
            arr = tmpPoint.split(",");
            maxPoint = new Point(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
            createDir();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(ips != null)
                    ips.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getMapStyle() {
        return mapStyle;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public String getToday() {
        return today;
    }

    public String getFileRootPath() {
        return fileRootPath;
    }

    public String getPicSuffix() {
        return picSuffix;
    }

    public int[] getZoomArr() {
        return zoomArr;
    }

    public static void main(String[] args) {
        Config config = new Config();
        System.out.println(config);
    }

    public Point getMinPoint() {
        return minPoint;
    }

    public Point getMaxPoint() {
        return maxPoint;
    }

    //获取墨卡托坐标
    public Point getMaxMcPoint(){
        return CoordinateTransform.bd2mc(maxPoint);
    }
    //获取墨卡托坐标
    public Point getMinMcPoint(){
        return CoordinateTransform.bd2mc(minPoint);
    }

    public void createDir(){
        File f = new File(fileRootPath);
        if(!f.exists()) {
            f.mkdirs();
        }
        for(int z : zoomArr) {
            f = new File(fileRootPath + z);
            if(!f.exists()) f.mkdir();
        }
    }
}
