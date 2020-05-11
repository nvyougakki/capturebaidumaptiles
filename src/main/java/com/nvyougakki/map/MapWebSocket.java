package com.nvyougakki.map;

import com.alibaba.fastjson.JSON;
import com.nvyougakki.map.bean.Computed;
import com.nvyougakki.map.bean.Config;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapWebSocket extends WebSocketServer {

    private Map<String, Config> connConfigMap = new HashMap<>();


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
        String remoteAddress = conn.getRemoteSocketAddress().toString();
        synchronized (connConfigMap) {
            //Config config = connConfigMap.get(remoteAddress);
            Config innerConfig = JSON.parseObject(message, Config.class);
            String cmd = innerConfig.getCmd();
            if("stop".equals(cmd)) {
                connConfigMap.get(remoteAddress).setRun(false);
                conn.close();
            } else if("start".equals(cmd)) {
                innerConfig.init();
                connConfigMap.put(remoteAddress, innerConfig);
                Computed computed = new Computed();
                //定时返回下载进度
                new Thread(() -> {
                    try {
                        while (innerConfig.isRun()) {
                            conn.send(JSON.toJSONString(computed));
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                //下载线程开启
                new Thread(new Start(innerConfig, computed, conn)).start();
            }
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
