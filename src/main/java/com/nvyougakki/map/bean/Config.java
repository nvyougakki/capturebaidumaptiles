package com.nvyougakki.map.bean;

import com.nvyougakki.map.util.CoordinateTransform;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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

    private String ak;

    private String bd2mcUrl;

    private PicAxis start;

    private int ThreadNum;

    private String cmd;

    private boolean run;

    public void init(){
        Properties ps = new Properties();
        InputStream ips = null;
        URL url = null;
        try {
            url = this.getClass().getResource("/config.properties");
            ips = url.openStream();
            ps.load(ips);
            fileRootPath = ps.getProperty("fileRootPath");
            ak = ps.getProperty("ak");
            bd2mcUrl = ps.getProperty("bd2mcUrl");
            picSuffix = ps.getProperty("picSuffix");
            //格式化mapurl
            mapUrl = mapUrl.replaceAll("x=\\d{0,20}", "x=?")
                    .replaceAll("y=\\d{0,20}", "y=?")
                    .replaceAll("z=\\d{0,20}", "z=?");
            run = true;
            //mapStyle = ps.getProperty("mapStyle");
            //String zoomStr = ps.getProperty("zoomArr");
            /*if("ALL".equals(zoomStr.toUpperCase())) {
                zoomArr = IntStream.range(1, 19).toArray();
            } else if(zoomStr.indexOf("-") >= 0) {
                String[] zoomPartStrArr = zoomStr.split(";");
                Set<Integer> zoomSet = new HashSet<>();
                for(String str : zoomPartStrArr) {
                    String[] arr = str.split("-");
                    IntStream.range(Integer.parseInt(arr[0]), Integer.parseInt(arr[1])).forEach(i -> zoomSet.add(i));
                }
                zoomArr = new int[zoomSet.size()];
                AtomicInteger zoomIndex = new AtomicInteger();
                zoomSet.stream().sorted(Integer::compareTo).forEach(i -> {
                    zoomArr[zoomIndex.getAndIncrement()] = i;
                });
            } else {
                zoomArr = Arrays.asList(ps.getProperty("zoomArr").split(",")).stream().mapToInt(Integer::parseInt).toArray();
            }*/
            String startPicStr = ps.getProperty("startPicAxis");
            if(startPicStr != null) {
                String[] arr = startPicStr.split("/");
                start = new PicAxis();
                start.setZ(Integer.parseInt(arr[0]));
                start.setX(Integer.parseInt(arr[1]));
                start.setY(Integer.parseInt(arr[2]));
            }
            //today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            //String tmpPoint = ps.getProperty("minPoint");
            //String[] arr = tmpPoint.split(",");
            //minPoint = new Point(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
            //tmpPoint = ps.getProperty("maxPoint");
            //arr = tmpPoint.split(",");
            //maxPoint = new Point(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
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

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getFileRootPath() {
        return fileRootPath;
    }

    public void setFileRootPath(String fileRootPath) {
        this.fileRootPath = fileRootPath;
    }

    public String getPicSuffix() {
        return picSuffix;
    }

    public void setPicSuffix(String picSuffix) {
        this.picSuffix = picSuffix;
    }

    public String getMapStyle() {
        return mapStyle;
    }

    public void setMapStyle(String mapStyle) {
        this.mapStyle = mapStyle;
    }

    public int[] getZoomArr() {
        return zoomArr;
    }

    public void setZoomArr(int[] zoomArr) {
        this.zoomArr = zoomArr;
    }

    public Point getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(Point minPoint) {
        this.minPoint = minPoint;
    }

    public Point getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(Point maxPoint) {
        this.maxPoint = maxPoint;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getBd2mcUrl() {
        return bd2mcUrl;
    }

    public void setBd2mcUrl(String bd2mcUrl) {
        this.bd2mcUrl = bd2mcUrl;
    }

    public PicAxis getStart() {
        return start;
    }

    public void setStart(PicAxis start) {
        this.start = start;
    }

    public int getThreadNum() {
        return ThreadNum;
    }

    public void setThreadNum(int threadNum) {
        ThreadNum = threadNum;
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    //获取墨卡托坐标
    public Point getMaxMcPoint(){
        return CoordinateTransform.bd2mc(this, maxPoint);
    }
    //获取墨卡托坐标
    public Point getMinMcPoint(){
        return CoordinateTransform.bd2mc(this, minPoint);
    }


    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
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

    @Override
    public String toString() {
        return "Config{" +
                "mapUrl='" + mapUrl + '\'' +
                ", today='" + today + '\'' +
                ", fileRootPath='" + fileRootPath + '\'' +
                ", picSuffix='" + picSuffix + '\'' +
                ", mapStyle='" + mapStyle + '\'' +
                ", zoomArr=" + Arrays.toString(zoomArr) +
                ", minPoint=" + minPoint +
                ", maxPoint=" + maxPoint +
                ", ak='" + ak + '\'' +
                ", bd2mcUrl='" + bd2mcUrl + '\'' +
                ", start=" + start +
                ", ThreadNum=" + ThreadNum +
                ", cmd='" + cmd + '\'' +
                ", run=" + run +
                '}';
    }
}
