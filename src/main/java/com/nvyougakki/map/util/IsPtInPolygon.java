package com.nvyougakki.map.util;

import java.util.List;

/**
 * java判断某个点是否在所画范围内(多边形【isPtInPoly】/圆形【distencePC】)
 * @param point 检测点
 * @param pts  多边形的顶点
 * @return   点在多边形内返回true,否则返回false
 * @author   ardo
 */
public class IsPtInPolygon {
    
    /**
     * 判断点是否在多边形内
     * @param point 检测点
     * @param pts  多边形的顶点
     * @return   点在多边形内返回true,否则返回false
     */
    public static boolean isPtInPoly(Point point, List<Point> pts){

        int N = pts.size();
        boolean boundOrVertex = true; //如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
        int intersectCount = 0;//cross points count of x
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        Point p1, p2;//neighbour bound vertices
        Point p = point; //当前点

        p1 = pts.get(0);//left vertex
        for(int i = 1; i <= N; ++i){//check all rays
            if(p.equals(p1)){
                return boundOrVertex;//p is an vertex
            }

            p2 = pts.get(i % N);//right vertex
            if(p.getX() < Math.min(p1.getX(), p2.getX()) || p.getX() > Math.max(p1.getX(), p2.getX())){//ray is outside of our interests
                p1 = p2;
                continue;//next ray left point
            }

            if(p.getX() > Math.min(p1.getX(), p2.getX()) && p.getX() < Math.max(p1.getX(), p2.getX())){//ray is crossing over by the algorithm (common part of)
                if(p.getY() <= Math.max(p1.getY(), p2.getY())){//x is before of ray
                    if(p1.getX() == p2.getX() && p.getY() >= Math.min(p1.getY(), p2.getY())){//overlies on a horizontal ray
                        return boundOrVertex;
                    }

                    if(p1.getY() == p2.getY()){//ray is vertical
                        if(p1.getY() == p.getY()){//overlies on a vertical ray
                            return boundOrVertex;
                        }else{//before ray
                            ++intersectCount;
                        }
                    }else{//cross point on the left side
                        double xinters = (p.getX() - p1.getX()) * (p2.getY() - p1.getY()) / (p2.getX() - p1.getX()) + p1.getY();//cross point of y
                        if(Math.abs(p.getY() - xinters) < precision){//overlies on a ray
                            return boundOrVertex;
                        }

                        if(p.getY() < xinters){//before ray
                            ++intersectCount;
                        }
                    }
                }
            }else{//special case when ray is crossing through the vertex
                if(p.getX() == p2.getX() && p.getY() <= p2.getY()){//p crossing over p2
                    Point p3 = pts.get((i+1) % N); //next vertex
                    if(p.getX() >= Math.min(p1.getX(), p3.getX()) && p.getX() <= Math.max(p1.getX(), p3.getX())){//p.getX() lies between p1.getX() & p3.getX()
                        ++intersectCount;
                    }else{
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;//next ray left point
        }

        if(intersectCount % 2 == 0){//偶数在多边形外
            return false;
        } else { //奇数在多边形内
            return true;
        }

    }

    /**
     * 判断是否在圆形内
     * @param p
     * @param c
     * @return
     */
    public static String distencePC(Point p,Circle c){//判断点与圆心之间的距离和圆半径的关系
        String s ;
        double d2 = Math.hypot( (p.getX() - c.getCc().getX() ), (p.getY() - c.getCc().getY()) );
        System.out.println("d2=="+d2);
        double r = c.getR();
        if(d2 > r){
            s = "圆外";
        }else if(d2 < r){
            s = "圆内";
        }else{
            s = "圆上";
        }
        return s;
    }

    public static void main(String[] args) {


    }

}
