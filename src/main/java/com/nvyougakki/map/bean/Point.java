package com.nvyougakki.map.bean;

/**
 * @ClassName Point
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2019/12/13 21:17
 * 点坐标对象
 */
public class Point
{
    private double x;

    private double y;

    public Point() {
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
