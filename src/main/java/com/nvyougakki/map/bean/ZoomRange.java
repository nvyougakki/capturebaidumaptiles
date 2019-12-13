package com.nvyougakki.map.bean;

/**
 * @ClassName ZoomRange
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2019/12/13 22:44
 */
public class ZoomRange {

    private int minX;

    private int maxX;

    private int minY;

    private int maxY;

    private int z;

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "ZoomRange{" +
                "minX=" + minX +
                ", maxX=" + maxX +
                ", minY=" + minY +
                ", maxY=" + maxY +
                ", z=" + z +
                '}';
    }
}
