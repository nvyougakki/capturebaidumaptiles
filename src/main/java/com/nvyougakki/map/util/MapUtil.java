package com.nvyougakki.map.util;

import com.nvyougakki.map.bean.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
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

    public final static String URL_PREFIX = "http://maponline0.bdimg.com/tile/?qt=vtile&styles=pl&scaler=1";

    public final static String TODAY = new SimpleDateFormat("YYYYMMdd").format(new Date());

    private final static String LOCAL_PATH = "F:/tiles/";
    private final static String PIC_SUFFIX= ".png";

    public static PicAxis mercatorToPicAxis(Point point, int z) {
        return mercatorToPicAxis(point.getX(), point.getY(), z);
    }

    /**
     *墨卡托转图块坐标
     **/
    public static PicAxis mercatorToPicAxis(double x, double y, int z) {
        PicAxis result = new PicAxis();
        result.setX((int) Math.floor(x * Math.pow(2, z - 18)/256));
        result.setY((int) Math.floor(y * Math.pow(2, z - 18)/256));
        return result;
    }

    public static int getPicNum(PicAxis minPic, PicAxis maxPic){
        return (maxPic.getX() - minPic.getX() + 1) * (maxPic.getY() - minPic.getY() + 1);
    }


    public static void downloadPicToLocal(PicAxis picAxis, String localPath){
        downloadPicToLocal(picAxis.getX(), picAxis.getY(), picAxis.getZ(), localPath, 0);
    }

    public static void downloadPicToLocal(int x, int y, int z, String localPath, int stackDeep){
        if(stackDeep > 10) return;
        URL url = null;
        HttpURLConnection connection = null;
        InputStream ips = null;
        OutputStream ops = null;
        String _url = URL_PREFIX + "&x=" + x + "&y=" + y + "&z=" + z + "&udt=" + TODAY;
        try{
            File f = new File(localPath + z + "/" + x + "/" + y + PIC_SUFFIX);

            if(!f.getParentFile().exists()){
                f.getParentFile().mkdirs();
            }
           /* if (f.exists()) {
                //System.out.println("重复下载");
                return;
            }*/
            f.createNewFile();
            url = new URL(_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if(connection.getResponseCode() == 200) {
                ips = connection.getInputStream();
                ops = new FileOutputStream(f);
                IOUtils.copy(ips, ops);
            }
        } catch (IOException e) {
            System.out.println(_url);
            downloadPicToLocal(x, y, z, localPath, stackDeep++);
            e.printStackTrace();
        } finally {
            try {
                if(ops != null)
                    ops.close();
                if(ips != null)
                    ips.close();
                if(connection != null)
                    connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static int getPicCountByZoom(Point minPoint, Point maxPoint, int z) {
        PicAxis minPicAxis = mercatorToPicAxis(minPoint, z);
        PicAxis maxPicAxis = mercatorToPicAxis(maxPoint, z);
        return getPicNum(minPicAxis, maxPicAxis);
    }

    public static ZoomRange getZoomRange(Point minPoint, Point maxPoint, int zoom){
        ZoomRange result = new ZoomRange();
        PicAxis min = mercatorToPicAxis(minPoint, zoom);
        PicAxis max = mercatorToPicAxis(maxPoint, zoom);
        result.setMinX(min.getX());
        result.setMaxX(max.getX());
        result.setMinY(min.getY());
        result.setMaxY(max.getY());
        result.setZ(zoom);
        return result;
    }


    public static void main(String[] args) {

        Point minPoint = new Point(13207995.69, 3420821.13);
        Point maxPoint = new Point(13397179.77, 3514005.1);


        List<Integer> zoomList = Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        Tiles tiles = new Tiles(minPoint, maxPoint, zoomList);

        Map<Integer, Integer> zoomCount = tiles.countZoom();

        List<ZoomRange> zoomRange = tiles.getZoomRange();

        TilesRange tilesMsg = new TilesRange(zoomRange);

        AtomicInteger total = new AtomicInteger();

        long start = System.currentTimeMillis();
        IntStream.rangeClosed(0,30).forEach(i -> {
            Thread thread = new Thread(() -> {

                PicAxis picAxis = tilesMsg.getPicAxis();
                while (picAxis != null) {
                    downloadPicToLocal(picAxis, LOCAL_PATH);
                    picAxis = tilesMsg.getPicAxis();
                    total.incrementAndGet();
                }

            }, "tile-thread-" + i);
            thread.start();
        });
        int count = zoomCount.get(-1);
        int downloadCount = 0;

        while (downloadCount < count) {

            try {
                Thread.sleep(3_000);
                downloadCount = total.intValue();
                double downloadRate = (double)downloadCount * 100/count;
                long second = (System.currentTimeMillis() - start)/ 1000;
                long downloadSpeed = downloadCount/second;
                long remainSecond = (count - downloadCount)/downloadSpeed;

                System.out.printf("需要下载：%d; 已下载：%d;下载百分比：%f%%;下载速度：%d;用时：%ds;预计剩余时间：%ds/%dmin \n",count, downloadCount, downloadRate, downloadSpeed, second,remainSecond, remainSecond/60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("下载完成");
       /* for(int i = 4;  i <= 16; i++) {
            PicAxis minPicAxis = mercatorToPicAxis(minPoint, i);
            PicAxis maxPicAxis = mercatorToPicAxis(maxPoint, i);
            int minX = minPicAxis.getX();
            int maxX = maxPicAxis.getX();
            int minY = minPicAxis.getY();
            int maxY = maxPicAxis.getY();
            for(int x = minX; x <= maxX; x++) {
                for(int y = minY; y <= maxY; y++) {
                    downloadPicToLocal(x, y, i, LOCAL_PATH);
                }
            }
        }*/


    }

}
