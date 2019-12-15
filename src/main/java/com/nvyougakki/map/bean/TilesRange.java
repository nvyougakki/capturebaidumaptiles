package com.nvyougakki.map.bean;

import java.util.List;
import java.util.Map;

/**
 * @ClassName TilesMsg
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2019/12/13 23:22
 */
public class TilesRange {

    private List<ZoomRange> zoomRangeList;

    private ZoomRange currRange;

    private int zoomRangeIndex;

    private int currZ;

    private int currX;

    private int currY;

    public TilesRange(List<ZoomRange> zoomRangeList) {
        this.zoomRangeList = zoomRangeList;
        this.zoomRangeIndex = 0;
        this.currRange = zoomRangeList.get(zoomRangeIndex);
        resetZ();
        resetX();
        resetY();
    }

    private void resetX(){
        currX = currRange.getMinPicAxis().getX();
    }

    private void resetY(){
        currY = currRange.getMinPicAxis().getY();
    }

    private void resetZ() {
        currZ = currRange.getZ();
    }

    public synchronized PicAxis getPicAxis(){
        PicAxis result = new PicAxis();
        //判断y是否越界，越界则x+1
        if(currY > currRange.getMaxPicAxis().getY()) {
            currX++;
            //判断x是否越界，x越界则z+1
            if(currX > currRange.getMaxPicAxis().getX()) {
                zoomRangeIndex++;
                if(zoomRangeIndex >= zoomRangeList.size()) return null;
                else {
                    currRange = zoomRangeList.get(zoomRangeIndex);
                    resetZ();
                    resetX();
                    resetY();
                }
            } else {
                resetY();
            }

        }
        result.setX(currX);
        result.setY(currY++);
        result.setZ(currZ);
        return result;
    }



}
