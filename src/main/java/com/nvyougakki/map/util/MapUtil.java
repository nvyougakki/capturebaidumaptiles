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

    //百度地图图层url
//    public final static String URL_PREFIX = "http://maponline0.bdimg.com/tile/?qt=vtile&styles=pl&scaler=1";
    public final static String URL_PREFIX = "http://api0.map.bdimg.com/customimage/tile/?scale=1";

    //今天日期
    public final static String TODAY = new SimpleDateFormat("YYYYMMdd").format(new Date());

    //下载目录
    private final static String LOCAL_PATH = "F:/tiles/blueTiles/";

    //文件后缀
    private final static String PIC_SUFFIX= ".png";

    //墨卡托坐标转图块坐标
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

    //获取当前图层图片总量
    public static int getPicNum(PicAxis minPic, PicAxis maxPic){
        return (maxPic.getX() - minPic.getX() + 1) * (maxPic.getY() - minPic.getY() + 1);
    }

    //下载图片
    public static void downloadPicToLocal(PicAxis picAxis, String localPath, String styles){
        downloadPicToLocal(picAxis.getX(), picAxis.getY(), picAxis.getZ(), localPath, styles, 0);
    }

    /**
     * @Author 女友Gakki
     * 下载图片
     * @Date 1:33 2019/12/14
     * @Param [x, y, z, localPath, stackDeep]
     * @return void
     **/
    public static void downloadPicToLocal(int x, int y, int z, String localPath ,String styles, int stackDeep){
        if(stackDeep > 10) return;
        URL url = null;
        HttpURLConnection connection = null;
        InputStream ips = null;
        OutputStream ops = null;
        String _url = URL_PREFIX + "&x=" + x + "&y=" + y + "&z=" + z + "&udt=" + TODAY + "&styles=" + styles;
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
            //System.out.println(_url);
//            Thread.sleep(2000);
            downloadPicToLocal(x, y, z, localPath, styles, stackDeep++);
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
        PicAxis min = mercatorToPicAxis(minPoint, zoom);
        PicAxis max = mercatorToPicAxis(maxPoint, zoom);
        ZoomRange result = new ZoomRange(min, max, zoom);
        result.setZ(zoom);
        return result;
    }


    public static void main(String[] args) {

        //百度坐标
        Point minPoint = new Point(117.964112,27.010913);
        Point maxPoint = new Point(122.986574, 31.289231);

        //墨卡托坐标--直接从百度地图api获取
//        Point minPoint = new Point(13207995.69, 3420821.13);
//        Point maxPoint = new Point(13397179.77, 3514005.1);
        //百度转墨卡托
        minPoint = BDPointToMc.changeToMc(minPoint, 5, 6);
        maxPoint = BDPointToMc.changeToMc(maxPoint, 5, 6);

        //需要抓取的图层层级
        List<Integer> zoomList = Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
//        List<Integer> zoomList = Arrays.asList(7, 8, 9, 10);

        Tiles tiles = new Tiles(minPoint, maxPoint, zoomList);

        //获取各图层图块数量
        Map<Integer, Integer> zoomCount = tiles.countZoom();

        //
        List<ZoomRange> zoomRange = tiles.getZoomRange();

        TilesRange tilesMsg = new TilesRange(zoomRange);

        AtomicInteger total = new AtomicInteger();

        long start = System.currentTimeMillis();
        String styles = "t%3Abackground%7Ce%3Aall%7Cc%3Atransparent%2Ct%3Awater%7Ce%3Aall%7Cc%3A%23044161%2Ct%3Aland%7Ce%3Aall%7Cc%3A%2301215c%2Ct%3Aboundary%7Ce%3Ag%7Cc%3A%23064f85%2Ct%3Arailway%7Ce%3Aall%7Cv%3Aoff%2Ct%3Ahighway%7Ce%3Ag%7Cc%3A%23004981%2Ct%3Ahighway%7Ce%3Ag.f%7Cc%3A%23005b96%7Cl%3A1%2Ct%3Ahighway%7Ce%3Al%7Cv%3Aon%2Ct%3Aarterial%7Ce%3Ag%7Cc%3A%23004981%7Cl%3A-39%2Ct%3Aarterial%7Ce%3Ag.f%7Cc%3A%2300508b%2Ct%3Apoi%7Ce%3Aall%7Cv%3Aoff%2Ct%3Agreen%7Ce%3Aall%7Cv%3Aoff%7Cc%3A%23056197%2Ct%3Asubway%7Ce%3Aall%7Cv%3Aoff%2Ct%3Amanmade%7Ce%3Aall%7Cv%3Aoff%2Ct%3Alocal%7Ce%3Aall%7Cv%3Aoff%2Ct%3Aarterial%7Ce%3Al%7Cv%3Aoff%2Ct%3Aboundary%7Ce%3Ag.f%7Cc%3A%23029fd4%2Ct%3Abuilding%7Ce%3Aall%7Cc%3A%231a5787%2Ct%3Alabel%7Ce%3Aall%7Cv%3Aoff%2Ct%3Apoi%7Ce%3Al.t.f%7Cc%3A%23ffffff%2Ct%3Apoi%7Ce%3Al.t.s%7Cc%3A%231e1c1c%2Ct%3Aadministrative%7Ce%3Al%7Cv%3Aon%2Ct%3Aroad%7Ce%3Al%7Cv%3Aoff";
        IntStream.rangeClosed(0,30).forEach(i -> {
            Thread thread = new Thread(() -> {

                PicAxis picAxis;
                while ((picAxis = tilesMsg.getPicAxis()) != null) {
                    downloadPicToLocal(picAxis, LOCAL_PATH, styles);
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
