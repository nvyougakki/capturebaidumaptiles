package com.nvyougakki.map.springboot.bean;

import com.nvyougakki.map.util.IsPtInPolygon;
import com.nvyougakki.map.util.Point;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PicAxis {

    private int z;

    private Point minPoint;

    private Point maxPoint;

    private Integer currx;

    private int curry;

    private int currMinY;

    private int currMaxY;

    private int total;

    private List<Point> polygonList = new ArrayList<>();

    private boolean isFinish = false;


    private synchronized void setCurrYRange() {
        int yOffset = Math.max(0, z-15);

        if(currx == null) currx = (int) minPoint.getX();
        if(currx > maxPoint.getX()) {
            return;
        }
        for(int y = (int) minPoint.getY(); y <= maxPoint.getY(); y++) {
            boolean ptInPoly = IsPtInPolygon.isPtInPoly(new Point(currx, y), polygonList);
            if(ptInPoly) {
                currMinY = y - yOffset;
                break;
            }
        }

        for(int y = (int) maxPoint.getY(); y >= minPoint.getY(); y--) {
            boolean ptInPoly = IsPtInPolygon.isPtInPoly(new Point(currx, y), polygonList);
            if(ptInPoly) {
                currMaxY = y + yOffset;
                break;
            }
        }
        curry = currMinY;
    }

    public synchronized Point getPoint() {
        if(currx == null) setCurrYRange();
        if(currx > maxPoint.getX()) {
            isFinish = true;
            return null;
        }

        Point p = new Point(currx, curry);
        curry++;
        if(curry > currMaxY) { //当y超过最大值，x+1，y重置
            currx++;
            setCurrYRange();
//            curry = currMinY;
        }
        p.setZ(z);
        return p;
    }
}
