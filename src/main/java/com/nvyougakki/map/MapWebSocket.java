package com.nvyougakki.map;

import com.alibaba.fastjson.JSON;
import com.nvyougakki.map.bean.Computed;
import com.nvyougakki.map.bean.Config;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;

public class MapWebSocket extends WebSocketServer {

    private static Config config = null;


    public MapWebSocket(int port){
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("连接开启");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.printf("连接关闭");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Config innerConfig = JSON.parseObject(message, Config.class);
        if("stop".equals(innerConfig.getCmd())) {
            config.setRun(false);
            conn.close();
        }
        if("start".equals(innerConfig.getCmd())) {
            config = innerConfig;
            config.init();
            Computed computed = new Computed();
            new Thread(() -> {

                try {
                    while (config.isRun()) {
                        conn.send(JSON.toJSONString(computed));
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(new Start(config, computed, conn)).start();
        }

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        conn.close();
    }

    public static void main(String[] args) {
        new MapWebSocket(9999).start();
    }
}
