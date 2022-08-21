package com.nvyougakki.map.util;

import lombok.Data;

/**
 * @ClassName Point
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2019/12/13 21:17
 * 点坐标
 */
@Data
public class Point
{
    private double x;

    private double y;

    private int z;

    public Point() {
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

}
