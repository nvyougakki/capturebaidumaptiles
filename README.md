## 抓取百度地图矩形内各层级图层底图方法

1. #### 前导知识

   地图相关：

   - 地图层级：地图放大到越大对应的层级越大，屏幕单位像素内所能表示的距离越小

   - 图块：一张256*256的图片，每张图块有自己的坐标（x,y,z）；地图放大后每张图片所能表示的范围越小，所以相同经纬度矩形范围内的图块数量将呈指数级增长，相当于平面中的点

   - 图层：由一张张z轴相同的图块构成的平面

   - 经纬度坐标：确定地图上点的位置的坐标

   - 墨卡托坐标：从经纬度原点到某个点的距离，可以理解为点在地图平面直角坐标系中的位置

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

2. #### 业务

   首先地图上确定要抓取的矩形范围，可通过百度地图获取左下和右上的经纬度

   ![image](https://raw.githubusercontent.com/nvyougakki/capturebaidumaptiles/master/images/1584748928112.png)

   ![1584749376007](https://raw.githubusercontent.com/nvyougakki/capturebaidumaptiles/master/images/1584749376007.png)

   - 然后将最小和最大的经纬度转化成墨卡托坐标

   - 再将墨卡托转化成不同层级的图层类，确定图层平面中最小点和最大点的坐标

   - 再依次遍历每个图层中的所有点坐标，拼接url下载图片

     图块url样例: http://maponline0.bdimg.com/tile/?qt=vtile&x=102&y=34&z=9&styles=pl&scaler=1&udt=20191212&from=jsapi2_0

     主要参数x,y,z就对应图块坐标，下载的图块保存方式为z/x/y.png

3. #### 类

   | 类名                     | 含义                                             |
   | ------------------------ | ------------------------------------------------ |
   | bean.Point               | 点                                               |
   | bean.Tile                | 图层（平面）                                     |
   | bean.PicAxis             | 图块坐标（点）                                   |
   | bean.TilesRange          | 有每个层级图层类，并且获取当前图块坐标（三维体） |
   | util.CoordinateTransform | 坐标转换                                         |

   