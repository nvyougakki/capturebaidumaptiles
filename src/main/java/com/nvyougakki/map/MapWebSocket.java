package com.nvyougakki.map;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class MapWebSocket extends WebSocketServer {

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
        System.out.println(conn.getRemoteSocketAddress());
        conn.send("from server：" + LocalDateTime.now());
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
