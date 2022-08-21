package com.nvyougakki.map.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度坐标转其他坐标
 */
public class CoordinateTransform {


    //墨卡托坐标转图块坐标
    public static Point mc2PicAxis(Point point, int z) {
        return mc2PicAxis(point.getX(), point.getY(), z);
    }

    /**
     *墨卡托转图块坐标
     **/
    public static Point mc2PicAxis(double x, double y, int z) {
        Point result = new Point();
        result.setX((int) Math.floor(x * Math.pow(2, z - 18)/256));
        result.setY((int) Math.floor(y * Math.pow(2, z - 18)/256));
        return result;
    }



    public static Point LonlattoWebMercator(Point point) {
        return LonlattoWebMercator(point.getX(), point.getY());
//        return new Point(stringDoubleMap.get("lon"), stringDoubleMap.get("lat"));
    }
    public static Map<String,Double> baiduTomars(Double lon, Double lat) {
        double x_pi=3.14159265358979324 * 3000.0 / 180.0;
        Map<String,Double> map = new HashMap<>();
        double x = lon-0.0065;
        double y = lat-0.006;
        double z = Math.sqrt(x*x+y*y)- 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double mars_lon=z * Math.cos(theta);
        double mars_lat=z * Math.sin(theta);
        map = GcjToWgs(mars_lon,mars_lat);
        return map;
    }

    /**
     * 火星坐标(GCJ-02坐标) 转 WGS-84坐标
     */
    public static Map<String,Double> GcjToWgs(Double lng,Double lat){
        double PI = 3.1415926535897932384626;
        double a = 6378245.0;
        double ee = 0.00669342162296594323;
        Map<String,Double> map = new HashMap<>();
        if (outOfChina(lng, lat)) {
            map.put("lng",lng);
            map.put("lat",lat);
            return map;
        } else {
            double dlat = transformlat(lng - 105.0, lat - 35.0);
            double dlng = transformlng(lng - 105.0, lat - 35.0);
            double radlat = lat / 180.0 * PI;
            double magic = Math.sin(radlat);
            magic = 1 - ee * magic * magic;
            double sqrtmagic = Math.sqrt(magic);
            dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
            dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
            double mglat = lat + dlat;
            double mglng = lng + dlng;
            map.put("lng",lng * 2 - mglng);
            map.put("lat",lat * 2 - mglat);
            return map;
        }
    }


    //是否在国内 不在国内不做偏移
    public static Boolean outOfChina(Double lon, Double lat){
        if ((lon < 72.004 || lon > 137.8347)&&(lat < 0.8293 || lat > 55.8271)){
            return true;
        }else {
            return false;
        }
    }

    // 纠偏Lat
    public static Double transformlat(Double lng,Double lat){
        double pi = 3.1415926535897932384626;
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat +         0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * pi) + 20.0 * Math.sin(2.0 * lng * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * pi) + 40.0 * Math.sin(lat / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * pi) + 320 * Math.sin(lat * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }
    // 纠偏Lon
    public static Double transformlng(Double lng,Double lat){
        double pi = 3.1415926535897932384626;
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * pi) + 20.0 * Math.sin(2.0 * lng * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * pi) + 40.0 * Math.sin(lng / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * pi) + 300.0 * Math.sin(lng / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

    // 转化墨卡托
    public static Point LonlattoWebMercator(Double lon,Double lat) {
        Map<String,Double> map = new HashMap<>();
        map = baiduTomars(lon,lat);
        double x = map.get("lng") / 180.0 * 20037508.34;
        double y = Math.log(Math.tan((90 + map.get("lat")) * Math.PI / 360)) / (Math.PI / 180);
        y = y * 20037508.34 / 180;
        return new Point(x, y);
    }

    private static double[] MCBAND = {12890594.86, 8362377.87, 5591021d, 3481989.83, 1678043.12, 0d};
    private static double[] LLBAND = {75d, 60d, 45d, 30d, 15d, 0d};
    private static double[][] MC2LL = {{1.410526172116255e-8, 0.00000898305509648872, -1.9939833816331, 200.9824383106796, -187.2403703815547, 91.6087516669843, -23.38765649603339, 2.57121317296198, -0.03801003308653, 17337981.2}, {-7.435856389565537e-9, 0.000008983055097726239, -0.78625201886289, 96.32687599759846, -1.85204757529826, -59.36935905485877, 47.40033549296737, -16.50741931063887, 2.28786674699375, 10260144.86}, {-3.030883460898826e-8, 0.00000898305509983578, 0.30071316287616, 59.74293618442277, 7.357984074871, -25.38371002664745, 13.45380521110908, -3.29883767235584, 0.32710905363475, 6856817.37}, {-1.981981304930552e-8, 0.000008983055099779535, 0.03278182852591, 40.31678527705744, 0.65659298677277, -4.44255534477492, 0.85341911805263, 0.12923347998204, -0.04625736007561, 4482777.06}, {3.09191371068437e-9, 0.000008983055096812155, 0.00006995724062, 23.10934304144901, -0.00023663490511, -0.6321817810242, -0.00663494467273, 0.03430082397953, -0.00466043876332, 2555164.4}, {2.890871144776878e-9, 0.000008983055095805407, -3.068298e-8, 7.47137025468032, -0.00000353937994, -0.02145144861037, -0.00001234426596, 0.00010322952773, -0.00000323890364, 826088.5}};
    private static double[][] LL2MC = {{-0.0015702102444, 111320.7020616939, 1704480524535203d, -10338987376042340d, 26112667856603880d, -35149669176653700d, 26595700718403920d, -10725012454188240d, 1800819912950474d, 82.5}, {0.0008277824516172526, 111320.7020463578, 647795574.6671607, -4082003173.641316, 10774905663.51142, -15171875531.51559, 12053065338.62167, -5124939663.577472, 913311935.9512032, 67.5}, {0.00337398766765, 111320.7020202162, 4481351.045890365, -23393751.19931662, 79682215.47186455, -115964993.2797253, 97236711.15602145, -43661946.33752821, 8477230.501135234, 52.5}, {0.00220636496208, 111320.7020209128, 51751.86112841131, 3796837.749470245, 992013.7397791013, -1221952.21711287, 1340652.697009075, -620943.6990984312, 144416.9293806241, 37.5}, {-0.0003441963504368392, 111320.7020576856, 278.2353980772752, 2485758.690035394, 6070.750963243378, 54821.18345352118, 9540.606633304236, -2710.55326746645, 1405.483844121726, 22.5}, {-0.0003218135878613132, 111320.7020701615, 0.00369383431289, 823725.6402795718, 0.46104986909093, 2351.343141331292, 1.58060784298199, 8.77738589078284, 0.37238884252424, 7.45}};

    /**
     * 墨卡托坐标转经纬度坐标
     * @param x x
     * @param y y
     * @return map
     */
    public static Map<String, Double> convertMC2LL(double x, double y) {
        double[] cF = null;
        x = Math.abs(x);
        y = Math.abs(y);
        for (int cE = 0; cE < MCBAND.length; cE++) {
            if (y >= MCBAND[cE]) {
                cF = MC2LL[cE];
                break;
            }
        }
        Map<String,Double> location = converter(x, y, cF);
        location.put("lng",location.get("x"));
        location.remove("x");
        location.put("lat",location.get("y"));
        location.remove("y");
        return location;
    }

    /**
     * 经纬度坐标转墨卡托坐标, 已做小数位保留（8位、10位）
     * @param lng lonti
     * @param lat lati
     * @return map
     */
    public static Point convertLL2MC(Double lng, Double lat) {
        Point result = new Point();
        double[] cE = null;
        lng = getLoop(lng, -180, 180);
        lat = getRange(lat, -74, 74);
        for (int i = 0; i < LLBAND.length; i++) {
            if (lat >= LLBAND[i]) {
                cE = LL2MC[i];
                break;
            }
        }
        if (cE!=null) {
            for (int i = LLBAND.length - 1; i >= 0; i--) {
                if (lat <= -LLBAND[i]) {
                    cE = LL2MC[i];
                    break;
                }
            }
        }
        Map<String, Double> map =  converter(lng,lat, cE);
        double x = map.get("x");
        double y = map.get("y");
        result.setX(x);
        result.setY(y);
//        BigDecimal xTemp = new BigDecimal(x);
//        BigDecimal yTemp = new BigDecimal(y);
//        String tempX = xTemp.setScale(8,BigDecimal .ROUND_HALF_UP).toPlainString();
//        String tempY = yTemp.setScale(10,BigDecimal .ROUND_HALF_UP).toPlainString();
        return result;
    }

    private static Map<String, Double> converter(double x, double y, double[] cE) {
        double xTemp = cE[0] + cE[1] * Math.abs(x);
        double cC = Math.abs(y) / cE[9];
        double yTemp = cE[2] + cE[3] * cC + cE[4] * cC * cC + cE[5] * cC * cC * cC + cE[6] * cC * cC * cC * cC + cE[7] * cC * cC * cC * cC * cC + cE[8] * cC * cC * cC * cC * cC * cC;
        xTemp *= (x < 0 ? -1 : 1);
        yTemp *= (y < 0 ? -1 : 1);
        Map<String, Double> location = new HashMap<>(4);
        BigDecimal tempX = new BigDecimal(xTemp);
        BigDecimal tempY = new BigDecimal(yTemp);
        xTemp = tempX.setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
        yTemp = tempY.setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
        location.put("x", xTemp);
        location.put("y", yTemp);
        return location;
    }

    private static Double getLoop(Double lng, Integer min, Integer max) {
        while (lng > max) {
            lng -= max - min;
        }
        while (lng < min) {
            lng += max - min;
        }
        return lng;
    }

    private static Double getRange(Double lat, Integer min, Integer max) {
        if (min != null) {
            lat = Math.max(lat, min);
        }
        if (max != null) {
            lat = Math.min(lat, max);
        }
        return lat;
    }

    public static int getTotalInPoints(Point p1, Point p2) {
        int y = (int)Math.abs(p1.getY() - p2.getY()) + 1;
        int x = (int)Math.abs(p1.getX() - p2.getX()) + 1;
        return x * y;
    }

    /*public static final int[] LLBAND = new int[]{75, 60, 45, 30, 15, 0};
    public static final double[][] LL2MC = new double[]{
            new double[]{-0.0015702102444, 111320.7020616939, 1704480524535203, -10338987376042340, 26112667856603880, -35149669176653700, 26595700718403920, -10725012454188240, 1800819912950474, 82.5},
            new double[]{0.0008277824516172526, 111320.7020463578, 647795574.6671607, -4082003173.641316, 10774905663.51142, -15171875531.51559, 12053065338.62167, -5124939663.577472, 913311935.9512032, 67.5},
            new double[]{0.00337398766765, 111320.7020202162, 4481351.045890365, -23393751.19931662, 79682215.47186455, -115964993.2797253, 97236711.15602145, -43661946.33752821, 8477230.501135234, 52.5},
            new double[]{0.00220636496208, 111320.7020209128, 51751.86112841131, 3796837.749470245, 992013.7397791013, -1221952.21711287, 1340652.697009075, -620943.6990984312, 144416.9293806241, 37.5},
            new double[]{-0.0003441963504368392, 111320.7020576856, 278.2353980772752, 2485758.690035394, 6070.750963243378, 54821.18345352118, 9540.606633304236, -2710.55326746645, 1405.483844121726, 22.5},
            new double[]{-0.0003218135878613132, 111320.7020701615, 0.00369383431289, 823725.6402795718, 0.46104986909093, 2351.343141331292, 1.58060784298199, 8.77738589078284, 0.37238884252424, 7.45}
    };

    public static getRange(cC, cB, T) {
        if (cB != null) {
            cC = Math.max(cC, cB);
        }
        if (T != null) {
            cC = Math.min(cC, T);
        }
        return cC;
    }

    function getLoop(cC, cB, T) {
        while (cC > T) {
            cC -= T - cB;
        }
        while (cC < cB) {
            cC += T - cB;
        }
        return cC;
    }

    function convertor(cC, cD) {
        if (!cC || !cD) {
            return null;
        }
        let T = cD[0] + cD[1] * Math.abs(cC.x);
    const cB = Math.abs(cC.y) / cD[9];
        let cE = cD[2] + cD[3] * cB + cD[4] * cB * cB +
                cD[5] * cB * cB * cB + cD[6] * cB * cB * cB * cB +
                cD[7] * cB * cB * cB * cB * cB +
                cD[8] * cB * cB * cB * cB * cB * cB;
        T *= (cC.x < 0 ? -1 : 1);
        cE *= (cC.y < 0 ? -1 : 1);
        return [T, cE];
    }

    function convertLL2MC(T) {
        let cD, cC, len;
        T.x = getLoop(T.x, -180, 180);
        T.y = getRange(T.y, -74, 74);
    const cB = T;
        for (cC = 0, len = LLBAND.length; cC < len; cC++) {
            if (cB.y >= LLBAND[cC]) {
                cD = LL2MC[cC];
                break;
            }
        }
        if (!cD) {
            for (cC = LLBAND.length - 1; cC >= 0; cC--) {
                if (cB.y <= -LLBAND[cC]) {
                    cD = LL2MC[cC];
                    break;
                }
            }
        }
    const cE = convertor(T, cD);
        return cE;
    }

    public static void main(String[] args){
        double lon = 116.180928000000000;
        double lat = 36.357202000000000;
        System.out.println(LonlattoWebMercator(lon,lat));
    }*/



}
