package com.nvyougakki.map.bean;

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

    public PicAxis() {
    }

    public PicAxis(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    @Override
    public String toString() {
        return "PicAxis{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
