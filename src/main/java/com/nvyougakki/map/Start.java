package com.nvyougakki.map;

import com.alibaba.fastjson.JSON;
import com.nvyougakki.map.bean.*;
import com.nvyougakki.map.util.CoordinateTransform;
import com.nvyougakki.map.util.MapUtil;
import org.java_websocket.WebSocket;

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
public class Start implements Runnable{

    private Config config;

    private Computed computed;

    private WebSocket conn;

    public Start(Config config, Computed computed, WebSocket conn) {
        this.config = config;
        this.computed = computed;
        this.conn = conn;
    }

    @Override
    public void run(){
        List<Tile> tiles = CoordinateTransform.mc2TileList(config);

        //加入所有图层
        TilesRange tilesRange = new TilesRange(tiles, config);

        //获取图块坐标总数
        int total = tilesRange.getPicCount();
        computed.setTotal(total);


        MapUtil mapUtil = new MapUtil();
        //开始多线程下载
        IntStream.rangeClosed(0,config.getThreadNum()).forEach(i -> {
            Thread thread = new Thread(() -> {
                PicAxis picAxis = null;
                while((picAxis = tilesRange.getPicAxis()) != null && config.isRun()) {
                    picAxis.startDownload();
                    computed.addDownloadOne();
                }
                config.setRun(false);
                if(conn.isConnecting())
                    conn.send(JSON.toJSONString(computed));
            }, "tile-thread-" + i);
            thread.start();
        });
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Computed getComputed() {
        return computed;
    }

    public void setComputed(Computed computed) {
        this.computed = computed;
    }

    public WebSocket getConn() {
        return conn;
    }

    public void setConn(WebSocket conn) {
        this.conn = conn;
    }

    public static void main(String[] args) {
        new MapWebSocket(9998).start();
    }

}
