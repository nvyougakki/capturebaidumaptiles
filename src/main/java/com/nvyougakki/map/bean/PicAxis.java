package com.nvyougakki.map.bean;

import com.nvyougakki.map.util.MapUtil;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.ConnectException;
import java.util.Optional;

/**
 * @ClassName Point
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2019/12/13 21:06
 * 图块坐标
 */
public class PicAxis {

    private int x;

    private int y;

    private int z;

    private Config config;

    public PicAxis() {
    }

    public PicAxis(Config config) {
        this.config = config;
    }

    public PicAxis(int x, int y, int z, Config config) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.config = config;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "PicAxis{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }


    public String getUrl(){
        String result = config.getMapUrl().replace("x=?", "x=" + x).replace("y=?", "y=" + y).replace("z=?", "z=" + z) + "&udt=" + config.getToday();
        if(null != config.getMapStyle() && !"".equals(config.getMapStyle())) {
            result += "&styles=" + config.getMapStyle();
        }
        return result;
    }

    public String getFilePath(){
        return config.getFileRootPath() + z + "/" + x + "/" + y + config.getPicSuffix();
    }

    public void startDownload(){
        InputStream ips = null;
        FileOutputStream fos = null;
        try {
            String filePath = getFilePath();
            File f = new File(filePath);
            if(f.length() == 0) {
                ips = MapUtil.getPicIps(getUrl());
                fos = new FileOutputStream(getFilePath());
                if(ips != null) {
                    IOUtils.copy(ips, fos);
                }
            }

        } catch (FileNotFoundException e) {
            File f = new File(getFilePath());
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //startDownload();
        } catch (ConnectException e) {
            //startDownload();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(ips != null ) {
                    ips.close();
                    ips = null;
                }

                if(fos != null ) {
                    fos.close();
                    ips = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {
        Config config = new Config();
        PicAxis picAxis = new PicAxis(1,0,3,config);
        System.out.println(picAxis.getFilePath());
        System.out.println(picAxis.getUrl());
//        picAxis.startDownload();

    }
}
