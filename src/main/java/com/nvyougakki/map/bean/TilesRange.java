package com.nvyougakki.map.bean;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TilesMsg
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2019/12/13 23:22
 */
public class TilesRange {

    private List<Tile> tiles;

    private Tile currTile;

    private int currIndex;

    private int currZ;

    private int currX;

    private int currY;

    private int picCount;

    private Config config;

    public TilesRange(List<Tile> tiles, Config config) {
        this.tiles = tiles;
        if(config.getStart() == null)
            this.currTile = tiles.get(currIndex);
        else
            for(Tile tile : tiles) {
                if(tile.getZ() == config.getStart().getZ()) {
                    this.currTile = tile;
                    break;
                }
                currIndex++;
            }
        this.config = config;
        resetZ();
        resetX();
        resetY();
        try{
            if(currZ >= 18) {
                currTile.createFile();
            } else {
                currTile.createFileByX(currX);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        initCount();
    }

    public void initCount(){
        for(Tile tile: tiles) {
            picCount += tile.getPicCount();
        }
    }

    public int getPicCount() {
        return picCount;
    }

    public void setPicCount(int picCount) {
        this.picCount = picCount;
    }
    /*public int getPicCount(PicAxis p){
        if(p == null){
            if(picCount == 0)
                for(Tile tile : tiles) {
                    picCount += tile.getPicNum();
                }
        }
        else
            getPicCountFrom(p);
        return picCount;
    }
    public int getPicCountFrom(PicAxis picAxis){
        int z = picAxis.getZ();
        if(picCount == 0)
            for(Tile tile : tiles) {
                if(tile.getZ() > z)
                    picCount += tile.getPicNum();
                if(tile.getZ() == z) {
                    PicAxis min = tile.getMinPicAxis();
                    PicAxis max = tile.getMinPicAxis();
                    picCount += Math.abs((picAxis.getX() - max.getX() + 1) * (picAxis.getY() - max.getY() + 1)) + 1;
                }
            }
        return picCount;
    }*/

    private void resetX(){
        currX = currTile.getMinPicAxis().getX();
    }

    private void resetY(){
        currY = currTile.getMinPicAxis().getY();
    }

    private void resetZ() {
        currZ = currTile.getZ();
    }

    //获取当前下载图块
    public synchronized PicAxis getPicAxis(){
        PicAxis result = new PicAxis(config);

        //判断y是否越界，越界则x+1
        if(currY > currTile.getMaxPicAxis().getY()) {
            currX++;
            //判断x是否越界，x越界则z+1
            if(currX > currTile.getMaxPicAxis().getX()) {
                currIndex++;
                //z越界则结束
                if(currIndex >= tiles.size()) return null;
                else {
                    currTile = tiles.get(currIndex);
                    //当图层大于18不一次性创建所有文件
                    if (currZ < 18)
                        try {
                            currTile.createFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    resetZ();
                    resetX();
                    resetY();
                }
            } else {
                resetY();
            }
            //当图层大于18，改为创建z/x/*.png
            if(currZ >= 18) {
                try {
                    currTile.createFileByX(currX);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        result.setX(currX);
        result.setY(currY++);
        result.setZ(currZ);
        return result;
    }

    public Tile getCurrTile() {
        return currTile;
    }

    public String currXYZ(){
        return currZ + "/" + currX + "/" + currY;
    }
}
