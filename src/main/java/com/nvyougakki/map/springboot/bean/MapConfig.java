package com.nvyougakki.map.springboot.bean;

import com.google.gson.Gson;
import com.nvyougakki.map.springboot.service.MapService;
import com.nvyougakki.map.util.CoordinateTransform;
import com.nvyougakki.map.util.IsPtInPolygon;
import com.nvyougakki.map.util.Point;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

@Data
public class MapConfig {

    private static final int[] SIZES = new int[]{50, 80, 100, 90, 60, 80};

    //层级
    private List<Integer> zoomList;

    //多边形坐标点经纬度
    private Double[][] coordinates;

    private List<Point> coordinatesPoint;

    //图块坐标
    private List<Point> picCoordinates = new ArrayList<>();


    private String url;

    private double minx = 10000;

    private double miny = 10000;

    private double maxx = -10000;
    private double maxy = -10000;

    private boolean isRunning = false;

    private int total;

    private LongAdder hasDownload = new LongAdder();

    private int zoomIndex;

    private int threadNum = 5;

    List<PicAxis> picAxes = new ArrayList<>();

    private int getIndex = 0;

    public synchronized List<Point> getPoints() {
        int size = SIZES[getIndex%SIZES.length];
        List<Point> result = new ArrayList<>(size);
//        getIndex++;
        for(; zoomIndex < picAxes.size(); zoomIndex++) {
            PicAxis picAxis = picAxes.get(zoomIndex);
            Point point = picAxis.getPoint(); //图块坐标
            while (point != null) {
                result.add(point);
                if(result.size() >= size) {
                    break;
                }
                point = picAxis.getPoint();
            }
//            if(point == null) {
////                if(zoomIndex >= picAxes.size()) {
////                    break;
////                }
////                i++;
//                zoomIndex++;
//                continue;
//            }
//            result.add(point);
            if(result.size() >= size || zoomIndex >= picAxes.size()) {
                break;
            }
        }
        return result;
    }

    public void plusHasDownload(int count) {
        hasDownload.add(count);
    }

    private String dir;

    public static void main(String[] args) throws IOException {
        String param = FileUtils.readFileToString(new File("E:\\workspace\\study\\capturebaidumaptiles\\param.txt"), "utf-8");
        MapConfig mapConfig = new Gson().fromJson(param, MapConfig.class);
        MapService mapService = new MapService();
        for (Integer integer : mapConfig.getZoomList()) {
            mapService.getPicByZoom(mapConfig, integer);
        }
       /* double minx = 10000;
        double miny = 10000;
        double maxx = -10000;
        double maxy = -10000;
        int zoom = 9;
        for (Double[] coordinate : mapConfig.getCoordinates()) {
            double x = coordinate[0];
            double y = coordinate[1];
            Point point = new Point(x, y);
            mapConfig.getPicCoordinates().add(CoordinateTransform.mc2PicAxis(CoordinateTransform.LonlattoWebMercator(point), 9));
            minx = Math.min(minx, x);
            miny = Math.min(miny, y);
            maxx = Math.max(maxx, x);
            maxy = Math.max(maxy, x);
        }
        Point minPoint = new Point(minx, miny);
        Point maxPoint = new Point(maxx, maxy);
        System.out.println(minPoint);
        Config config = new Config();
        config.setBd2mcUrl("http://api.map.baidu.com/geoconv/v1/");
        config.setAk("qi50bY3STQhYsDmISw8yP1EI8UUbppf6");
//        System.out.println(CoordinateTransform.mc2PicAxis(CoordinateTransform.bd2mc(config, minPoint), 9));
        Point minPicAxis = CoordinateTransform.mc2PicAxis(CoordinateTransform.LonlattoWebMercator(minPoint), 9);
        Point maxPicAxis = CoordinateTransform.mc2PicAxis(CoordinateTransform.LonlattoWebMercator(maxPoint), 9);
        double minPicx = minPicAxis.getX();
        double minPicy = minPicAxis.getY();
        double maxPicx = maxPicAxis.getX();
        double maxPicy = maxPicAxis.getY();
        for(; minPicx <= maxPicx; minPicx++) {
//            int y = minPicy;
            for(double y = minPicy; y < maxPicy; y++) {
                boolean ptInPoly = IsPtInPolygon.isPtInPoly(new Point(minPicx, y), mapConfig.getPicCoordinates());

            }
        }*/

    }

}
