package com.nvyougakki.map.bean;

/**
 * @ClassName ZoomRange
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2019/12/13 22:44
 * 层级对应的图块坐标范围
 */
public class ZoomRange {


    private PicAxis minPicAxis;  //图块最小坐标，即左下角图块坐标

    private PicAxis maxPicAxis; //图块最大坐标，即右上角图块坐标

    private int z;

    public ZoomRange() {
    }

    public ZoomRange(PicAxis minPicAxis, PicAxis maxPicAxis, int z) {
        this.minPicAxis = minPicAxis;
        this.maxPicAxis = maxPicAxis;
        this.z = z;
    }

    public ZoomRange(int minX, int minY, int maxX, int maxY, int z) {
        this(new PicAxis(minX, minY, z), new PicAxis(maxX, maxY, z), z);
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

    /*private int minX;

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
    }*/
}
