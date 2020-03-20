## 抓取百度地图矩形内各层级图层底图方法

1. #### 前导知识

   地图相关：

   - 地图层级：地图放大到越大对应的层级越大，屏幕单位像素内所能表示的距离越小

   - 图块：地图的图层是由一张张有坐标的256*256的图片构成，地图放大后每张图片所能表示的范围越小，所以相同经纬度矩形范围内的图块数量将呈指数级增长，相当于平面中的点

   - 图层：由一张张z轴相同的图块构成的平面

   - 经纬度坐标：确定地图上点的位置的坐标、

   - 墨卡托坐标：从经纬度原点到某个点的距离，可以理解为把地图平面直角坐标系中的位置

   - 图块坐标：通过墨卡托坐标转化为图块坐标

     ```java
     //z表示地图层级
     public static PicAxis mc2PicAxis(double x, double y, int z) {
         PicAxis result = new PicAxis();
         result.setX((int) Math.floor(x * Math.pow(2, z - 18)/256));
         result.setY((int) Math.floor(y * Math.pow(2, z - 18)/256));
         return result;
     }
     ```

     z不同所以图块位置范围会发生改变

   

   要抓取的是矩形范围内不同层级的图片，相当于抓取不同层级的图层中每个图块的坐标

   先将左下和右上点的经纬度转化为墨卡托坐标，再由墨卡托坐标计算各层级图块坐标范围

   图块url样例: http://maponline0.bdimg.com/tile/?qt=vtile&x=102&y=34&z=9&styles=pl&scaler=1&udt=20191212&from=jsapi2_0

   主要参数x,y,z就对应图块坐标，下载的图块保存方式为z/x/y.png

2. #### 类

   | 类名                     | 含义                                             |
   | ------------------------ | ------------------------------------------------ |
   | bean.Point               | 点                                               |
   | bean.Tile                | 图层（平面）                                     |
   | bean.PicAxis             | 图块坐标（点）                                   |
   | bean.TilesRange          | 有每个层级图层类，并且获取当前图块坐标（三维体） |
   | util.CoordinateTransform | 坐标转换                                         |

   