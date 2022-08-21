package com.nvyougakki.map.springboot.service;

import com.nvyougakki.map.springboot.bean.MapConfig;
import com.nvyougakki.map.springboot.bean.PicAxis;
import com.nvyougakki.map.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Service;
import sun.nio.ch.ThreadPool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.LongAdder;

@Service
@Slf4j
public class MapService {


    public static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    public static MapConfig nowMapConfig = null;

    public static boolean startMonitor = false;

    static {
        //监控进行中的线程数判断是否结束
        singleThreadExecutor.submit(() -> {
            while (true) {
                try {
                    if(startMonitor && executorService != null && !executorService.isShutdown() && nowMapConfig != null && nowMapConfig.isRunning()){
                        if(((ThreadPoolExecutor)executorService).getActiveCount() <= 0) {
                            nowMapConfig.setRunning(false);
                            LongAdder hasDownload = nowMapConfig.getHasDownload();
                            hasDownload.reset();
                            hasDownload.add(nowMapConfig.getTotal());
                            startMonitor = false;
                        }
                    }
                    Thread.sleep(3_000);
                } catch (Exception e) {

                }

            }

        });
    }

    public static volatile boolean isRunning = false;


    public void getPic(MapConfig mapConfig) {
        if(executorService.isShutdown()) executorService = Executors.newFixedThreadPool(mapConfig.getThreadNum());
        for (Integer zoom : mapConfig.getZoomList()) {
//            try {
//                getPicByZoom(mapConfig, zoom);
            PicAxis picAxis = getPicAxis(mapConfig, zoom);
            mapConfig.getPicAxes().add(picAxis);
            mapConfig.setTotal(picAxis.getTotal() + mapConfig.getTotal());

//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

//        ((ThreadPoolExecutor)executorService).setMaximumPoolSize(mapConfig.getThreadNum());
//        ((ThreadPoolExecutor)executorService).setCorePoolSize(mapConfig.getThreadNum());
        mapConfig.setRunning(true);
        for (int i = 0; i < mapConfig.getThreadNum(); i++) {
            executorService.submit(() -> {
                List<Point> nextPoints = mapConfig.getPoints();
                while (nextPoints.size() > 0) {
                    downloadPoints(mapConfig, nextPoints);
                    nextPoints = mapConfig.getPoints();
//                    mapConfig.plusHasDownload(nextPoints.size());
                }
            });
        }
        startMonitor = true;



    }
    private void downloadPoints(MapConfig mapConfig, List<Point> points){
        HttpGet request = new HttpGet("");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
        request.setHeader("Accept-Encoding", "gzip, deflate");
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36 Edg/89.0.774.75\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        request.setConfig(HttpClientUtils.REQUEST_CONFIG);
        CloseableHttpClient httpClient = HttpClientUtils.getHttpClient();
        int download = 0;
        for (Point point : points) {
            downloadPoint(mapConfig, point, request, httpClient);
            download++;
            if(download == 20) {
                mapConfig.plusHasDownload(download);
                download = 0;
            }
        }
        mapConfig.plusHasDownload(download);

    }
    private void downloadPoint(MapConfig mapConfig, Point point, HttpGet request, HttpClient httpClient) {
        int x = (int)point.getX();
        int y = (int)point.getY();
        int z = point.getZ();
        InputStream ips = null;

        try {
            File file = new File(mapConfig.getDir() + File.separator + z + File.separator + x + File.separator + y + ".png");
            if(file.exists()) {
                return;
            }
            if(!file.getParentFile().exists()) file.getParentFile().mkdirs();

            String url = mapConfig.getUrl().replace("${x}", x + "").replace("${y}", y+"").replace("${z}", z+"");
            log.info(url);
            request.setURI(URI.create(url));
            HttpResponse response = httpClient.execute(request);
            ips =  response.getEntity().getContent();
            if(ips.available() > 0) {
                file.createNewFile();
                FileUtils.copyInputStreamToFile(ips, file);
            }
            if(ips != null) ips.close();
        } catch (Exception e) {
            e.printStackTrace();
            if(ips != null) {
                try {
                    ips.close();
                } catch (IOException ex) {

                }
            }
        }
    }

    public PicAxis getPicAxis(MapConfig mapConfig, int z) {
        PicAxis result = new PicAxis();
        Double[][] coordinates = mapConfig.getCoordinates();
        double minx = 10000;
        double miny = 10000;

        double maxx = -10000;
        double maxy = -10000;
        for (int i = 0; i < coordinates.length; i++) {
            Double[] coordinate = coordinates[i];

            // 第一个层级转换成百度经纬度
            if(z == mapConfig.getZoomList().get(0)) {
                Point tmpPoint = MapUtil.wgs84ToGcj02(coordinate[1], coordinate[0]);
                Point point = MapUtil.gcj02ToBd09(tmpPoint.getY(), tmpPoint.getX());
                coordinate = new Double[]{point.getX(), point.getY()};
                coordinates[i] = coordinate;
            }

            double x = coordinate[0];
            double y = coordinate[1];

            //获取最大最小经纬度
            minx = Math.min(minx, x);
            miny = Math.min(miny, y);
            maxx = Math.max(maxx, x);
            maxy = Math.max(maxy, y);

            //所有点转成图块坐标
            Point point = CoordinateTransform.mc2PicAxis(CoordinateTransform.convertLL2MC(x, y), z);
            result.getPolygonList().add(point);
//            mapConfig.getPicCoordinates().add(point);
        }
//        System.out.println(IsPtInPolygon.isPtInPoly(new Point(822,209), mapConfig.getPicCoordinates()));
//        System.out.println(IsPtInPolygon.isPtInPoly(new Point(822,199), mapConfig.getPicCoordinates()));
        Point minPt = CoordinateTransform.mc2PicAxis(CoordinateTransform.convertLL2MC(minx, miny), z);
        Point maxPt = CoordinateTransform.mc2PicAxis(CoordinateTransform.convertLL2MC(maxx, maxy), z);
        result.setMinPoint(minPt);
        result.setMaxPoint(maxPt);
        result.setZ(z);
        result.setTotal(CoordinateTransform.getTotalInPoints(minPt, maxPt));
        return result;
    }

    public void getPicByZoom(MapConfig mapConfig, int z) throws IOException {
        mapConfig.getPicCoordinates().clear();
        Double[][] coordinates = mapConfig.getCoordinates();
        //所有经纬度转图块坐标
        double minx = 10000;
        double miny = 10000;

        double maxx = -10000;
        double maxy = -10000;
        for (Double[] coordinate : coordinates) {
            double x = coordinate[0];
            double y = coordinate[1];
            Point tmpPoint = MapUtil.wgs84ToGcj02(y, x);
            tmpPoint = MapUtil.gcj02ToBd09(tmpPoint.getY(), tmpPoint.getX());
            x = tmpPoint.getX();
            y = tmpPoint.getY();
            //获取最大最小经纬度
            minx = Math.min(minx, x);
            miny = Math.min(miny, y);
            maxx = Math.max(maxx, x);
            maxy = Math.max(maxy, y);

//            System.out.println(CoordinateTransform.LonlattoWebMercator(x, y));
//            System.out.println(CoordinateTransform.convertLL2MC(x, y));
            //所有点转成图块坐标
            Point point = CoordinateTransform.mc2PicAxis(CoordinateTransform.convertLL2MC(x, y), z);
            mapConfig.getPicCoordinates().add(point);
        }
//        System.out.println(IsPtInPolygon.isPtInPoly(new Point(822,209), mapConfig.getPicCoordinates()));
//        System.out.println(IsPtInPolygon.isPtInPoly(new Point(822,199), mapConfig.getPicCoordinates()));
        Point minPt = CoordinateTransform.mc2PicAxis(CoordinateTransform.convertLL2MC(minx, miny), z);
        Point maxPt = CoordinateTransform.mc2PicAxis(CoordinateTransform.convertLL2MC(maxx, maxy), z);
        System.out.println("层级:" + z + ",总数:" + CoordinateTransform.getTotalInPoints(minPt, maxPt));
        Point tmpPoint = new Point();
        HttpGet request = new HttpGet("");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
        request.setHeader("Accept-Encoding", "gzip, deflate");
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36 Edg/89.0.774.75\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        request.setConfig(HttpClientUtils.REQUEST_CONFIG);
        CloseableHttpClient httpClient = HttpClientUtils.getHttpClient();
        int yPianyi = 0;
        if(z > 16) yPianyi = 2;
        for(int x = (int)minPt.getX(); x <= maxPt.getX(); x++) {
           int minY = 0;
           int maxY = 0;
            for(int y = (int) minPt.getY(); y <= maxPt.getY(); y++) {
                tmpPoint.setX(x);
                tmpPoint.setY(y);
                boolean ptInPoly = IsPtInPolygon.isPtInPoly(tmpPoint, mapConfig.getPicCoordinates());
                if(ptInPoly) {
                    minY = y-yPianyi;
//                    downloadPic(mapConfig, request, httpClient, x, y, z);
                    break;

                }
            }

            for(int y = (int) maxPt.getY(); y >= minPt.getY(); y--) {
                tmpPoint.setX(x);
                tmpPoint.setY(y);
                boolean ptInPoly = IsPtInPolygon.isPtInPoly(tmpPoint, mapConfig.getPicCoordinates());
                if(ptInPoly) {
                    maxY = y+yPianyi;
                    break;
                }
            }
            for(; minY <= maxY; minY++) {
                downloadPic(mapConfig, request, httpClient, x, minY, z);
            }
        }
    }

    public void downloadPic(MapConfig mapConfig, HttpGet request, HttpClient httpClient, int x, int y, int z) {
        InputStream ips = null;
        String url = mapConfig.getUrl().replace("${x}", x + "").replace("${y}", y+"").replace("${z}", z+"");
        log.debug(url);
        try {
            File file = new File(mapConfig.getDir() + File.separator + z + File.separator + x + File.separator + y + ".png");
            if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if(file.exists()) {
                return;
            }
            request.setURI(URI.create(url));
            log.info(url);
            HttpResponse response = httpClient.execute(request);
            ips =  response.getEntity().getContent();
            if(ips.available() > 0) {
                file.createNewFile();
                FileUtils.copyInputStreamToFile(ips, file);
            }
            if(ips != null) ips.close();
        } catch (Exception e) {
            e.printStackTrace();
            if(ips != null) {
                try {
                    ips.close();
                } catch (IOException ex) {

                }
            }
        }

    }



}
