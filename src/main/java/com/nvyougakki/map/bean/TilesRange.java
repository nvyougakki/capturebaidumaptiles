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
        this.currTile = tiles.get(currIndex);
        this.config = config;
        resetZ();
        resetX();
        resetY();
    }

    public int getPicCount(){
        if(picCount == 0)
            for(Tile tile : tiles) {
                picCount += tile.getPicNum();
            }
        return picCount;
    }

    private void resetX(){
        currX = currTile.getMinPicAxis().getX();
    }

    private void resetY(){
        currY = currTile.getMinPicAxis().getY();
    }

    private void resetZ() {
        currZ = currTile.getZ();
    }

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
            //当图层大于18不一次性创建所有文件
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
