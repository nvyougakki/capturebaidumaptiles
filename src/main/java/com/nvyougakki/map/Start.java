package com.nvyougakki.map;

import com.nvyougakki.map.bean.*;
import com.nvyougakki.map.util.CoordinateTransform;
import com.nvyougakki.map.util.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

/**
 * @ClassName Start
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2020/3/20 19:46
 */
public class Start {

    public static void main(String[] args) {
        /*//中国矩形百度坐标
        Point minPoint = new Point(64.746589,3.989424);
        Point maxPoint = new Point(152.317722, 54.297495);*/

        //浙江省矩形百度坐标
//        Point minPoint = new Point(117.964112,27.010913);
//        Point maxPoint = new Point(122.986574, 31.289231);





        //获取配置文件
        Config config = new Config();

        //百度坐标转墨卡托
//        Point minPoint = CoordinateTransform.bd2Mc(config.getMinPoint(), 5, 6);
//        Point maxPoint = CoordinateTransform.bd2Mc(config.getMaxPoint(), 5, 6);

        List<Tile> tiles = CoordinateTransform.mc2TileList(config);
        TilesRange tilesRange = new TilesRange(tiles, config);

        //获取图块坐标总数
        int total = tilesRange.getPicCount();
//        MapUtil mapUtil = new MapUtil();

        //当前下载量
        LongAdder hasDownload = new LongAdder();

        //开始多线程下载
        IntStream.rangeClosed(0,20).forEach(i -> {
            Thread thread = new Thread(() -> {
                PicAxis picAxis = tilesRange.getPicAxis();
                while(picAxis != null) {
                    picAxis.startDownload();
                    hasDownload.add(1);
                    picAxis = tilesRange.getPicAxis();
                }

            }, "tile-thread-" + i);
            thread.start();
        });


        //下载情况打印
        int lastDownload = 0;
        long start = System.currentTimeMillis();
        while (hasDownload.intValue() < total) {
            try {
                long tmpStart = System.currentTimeMillis();
                Thread.sleep(3_000);
                int finish = hasDownload.intValue();
                double downloadRate = (double)finish * 100/total;
                long now = System.currentTimeMillis();
                long second = (System.currentTimeMillis() - tmpStart)/ 1000;
                long downloadSpeed = (finish - lastDownload)/second;
                long remainSecond = Long.MAX_VALUE;
                if(downloadSpeed != 0)
                    remainSecond = (total - finish)/downloadSpeed;
                lastDownload = finish;

                System.out.printf("需要下载：%d; 已下载：%d;当前下载位置：%s;下载百分比：%f%%;下载速度：%d;用时：%ds/%fmin;预计剩余时间：%ds/%fmin \n",total, finish, tilesRange.currXYZ(), downloadRate, downloadSpeed, (now - start)/1000, (now-start)/1000/60.0,remainSecond, remainSecond/60.0);
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
