package com.nvyougakki.map.bean;

import com.nvyougakki.map.util.MapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @ClassName Tiles
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2019/12/13 22:51
 */
public class Tiles {

    //左下角坐标
    private Point minPoint;

    //右上角坐标
    private Point maxPoint;

    //层级
    private List<Integer> zoomList;

    public Tiles(Point minPoint, Point maxPoint, List<Integer> zoomList) {
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.zoomList = zoomList;
    }



    //获取各层级点的范围
    public List<ZoomRange> getZoomRange(){
        List<ZoomRange> result = new ArrayList<>(zoomList.size() + 1);
        zoomList.forEach( z -> {
            result.add(MapUtil.getZoomRange(minPoint, maxPoint, z));
        });
        System.out.println(result);
        return result;
    }

    /*public Map<Integer, Integer> count(int minZ, int maxZ) {
        List<Integer> zoomList = new ArrayList<>();
        IntStream.rangeClosed(minZ, maxZ).forEach(i -> zoomList.add(i));
        return getPicCountByZoom();
    }*/

    //获取所以层级的数量，当key为-1时代表所有层级总量
    public Map<Integer, Integer> countZoom(){
        Map<Integer, Integer> result = new HashMap<>(zoomList.size() + 1);
        result.put(-1, 0);
        zoomList.forEach( z -> {
            int num = MapUtil.getPicCountByZoom(minPoint, maxPoint, z);
            result.put(-1, result.get(-1) + num);
            result.put(z, num);
        });
        return result;
    }








}
