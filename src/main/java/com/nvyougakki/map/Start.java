package com.nvyougakki.map;

import com.nvyougakki.map.bean.*;
import com.nvyougakki.map.util.CoordinateTransform;
import com.nvyougakki.map.util.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @ClassName Start
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2020/3/20 19:46
 */
public class Start {

    public static void main(String[] args) {
        //百度坐标
        Point minPoint = new Point(117.964112,27.010913);
        Point maxPoint = new Point(122.986574, 31.289231);


        //百度坐标转墨卡托
        minPoint = CoordinateTransform.bd2Mc(minPoint, 5, 6);
        maxPoint = CoordinateTransform.bd2Mc(maxPoint, 5, 6);

        //获取配置文件
        Config config = new Config();
        List<Tile> tiles = CoordinateTransform.mc2TileList(minPoint, maxPoint, config.getZoomArr());
        TilesRange tilesRange = new TilesRange(tiles, config);
        int total = tilesRange.getPicCount();
        AtomicInteger hasDownload = new AtomicInteger();

        IntStream.rangeClosed(0,30).forEach(i -> {
            Thread thread = new Thread(() -> {
                PicAxis picAxis = tilesRange.getPicAxis();
                while(picAxis != null) {
                    hasDownload.addAndGet(picAxis.startDownload());
                    picAxis = tilesRange.getPicAxis();
                }

            }, "tile-thread-" + i);
            thread.start();
        });


        int lastDownload = 0;
        while (hasDownload.intValue() < total) {
            try {
                long start = System.currentTimeMillis();
                Thread.sleep(3_000);
                int finish = hasDownload.intValue();
                double downloadRate = (double)finish * 100/total;
                long second = (System.currentTimeMillis() - start)/ 1000;
                long downloadSpeed = (finish - lastDownload)/second;
                long remainSecond = Long.MAX_VALUE;
                if(downloadSpeed != 0)
                    remainSecond = (total - finish)/downloadSpeed;
                lastDownload = finish;

                System.out.printf("需要下载：%d; 已下载：%d;下载百分比：%f%%;下载速度：%d;用时：%ds;预计剩余时间：%ds/%dmin \n",total, finish, downloadRate, downloadSpeed, second,remainSecond, remainSecond/60);
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
