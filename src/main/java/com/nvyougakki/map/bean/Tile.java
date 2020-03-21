package com.nvyougakki.map.bean;

import com.nvyougakki.map.util.MapUtil;

import java.io.File;
import java.io.IOException;

/**
 * @ClassName Tile
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2020/3/20 19:53
 * 图层类,相当于z轴固定的平面
 */
public class Tile {

    private PicAxis minPicAxis;  //左下图块坐标

    private PicAxis maxPicAxis;  //右下图块坐标

    private int z; //地图层级

    private int picCount;  //当前图层屏幕图块数量

    private Config config;

//    MapUtil mapUtil;

    public Tile() {
    }

    public Tile(PicAxis minPicAxis, PicAxis maxPicAxis, int z) {
        this.minPicAxis = minPicAxis;
        this.maxPicAxis = maxPicAxis;
        this.z = z;
//        mapUtil = new MapUtil();
    }

    public PicAxis getMinPicAxis() {
        return minPicAxis;
    }

    public void setMinPicAxis(PicAxis minPicAxis) {
        this.minPicAxis = minPicAxis;
    }

    public PicAxis getMaxPicAxis() {
        return maxPicAxis;
    }

    public void setMaxPicAxis(PicAxis maxPicAxis) {
        this.maxPicAxis = maxPicAxis;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getPicCount() {
        if(picCount == 0) {
            picCount = getPicNum();
        }
        return picCount;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    //获取当前图层图片总量
    public int getPicNum(){
        return (maxPicAxis.getX() - minPicAxis.getX() + 1) * (maxPicAxis.getY() - minPicAxis.getY() + 1);
    }

    //创建当前图层下的所有文件。文件创建格式为 z/x/y.png
    public void createFile() throws IOException {
        String rootPath = config.getFileRootPath();
        File f = null;
        rootPath += z;

        for(int x = minPicAxis.getX(); x <= maxPicAxis.getX(); x++) {
            String tmpPath = rootPath + "/" + x;
            f = new File(tmpPath);
            if(!f.exists()) {
                f.mkdir();
            }
            for(int y = minPicAxis.getY(); y <= maxPicAxis.getY(); y++) {
                f = new File(tmpPath + "/" + y + config.getPicSuffix());
                f.createNewFile();
            }
        }
    }

    public void createFileByX(int x) throws IOException {
        String rootPath = config.getFileRootPath();
        File f = null;
        rootPath += z;
//        for(int x = minPicAxis.getX(); x <= maxPicAxis.getX(); x++) {
            String tmpPath = rootPath + "/" + x;
            f = new File(tmpPath);
            if(!f.exists()) {
                f.mkdir();
            }
            for(int y = minPicAxis.getY(); y <= maxPicAxis.getY(); y++) {
                f = new File(tmpPath + "/" + y + config.getPicSuffix());
                f.createNewFile();
            }
//        }
    }
}
