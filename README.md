### 抓取百度地图图层文档

* [抓取百度地图图层文档](#%E6%8A%93%E5%8F%96%E7%99%BE%E5%BA%A6%E5%9C%B0%E5%9B%BE%E5%9B%BE%E5%B1%82%E6%96%87%E6%A1%A3)
  * [项目介绍](#%E9%A1%B9%E7%9B%AE%E4%BB%8B%E7%BB%8D)
  * [需求背景知识](#%E9%9C%80%E6%B1%82%E8%83%8C%E6%99%AF%E7%9F%A5%E8%AF%86)
  * [需求](#%E9%9C%80%E6%B1%82)
  * [项目启动](#%E9%A1%B9%E7%9B%AE%E5%90%AF%E5%8A%A8)
    * [后台](#%E5%90%8E%E5%8F%B0)
    * [前端](#%E5%89%8D%E7%AB%AF)
    * [关于图块地址](#%E5%85%B3%E4%BA%8E%E5%9B%BE%E5%9D%97%E5%9C%B0%E5%9D%80)
    * [关于坐标范围](#%E5%85%B3%E4%BA%8E%E5%9D%90%E6%A0%87%E8%8C%83%E5%9B%B4)
    * [关于地图样式](#%E5%85%B3%E4%BA%8E%E5%9C%B0%E5%9B%BE%E6%A0%B7%E5%BC%8F)

#### 项目介绍

本项目为抓取百度地图底图的小项目

#### 需求背景知识

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

#### 需求

首先地图上确定要抓取的矩形范围，可通过百度地图获取左下和右上的经纬度

![image](https://raw.githubusercontent.com/nvyougakki/capturebaidumaptiles/master/images/1584748928112.png)

![1584749376007](https://raw.githubusercontent.com/nvyougakki/capturebaidumaptiles/master/images/1584749376007.png)

- 然后将最小和最大的经纬度转化成墨卡托坐标

- 再将墨卡托转化成不同层级的图层类，确定图层平面中最小点和最大点的坐标

- 再依次遍历每个图层中的所有点坐标，拼接url下载图片

  图块url样例: http://maponline0.bdimg.com/tile/?qt=vtile&x=102&y=34&z=9&styles=pl&scaler=1&udt=20191212&from=jsapi2_0

  主要参数x,y,z就对应图块坐标，下载的图块保存方式为z/x/y.png

#### 项目启动

项目采用前后台方式，交互的形式采用websocket

##### 后台

启动Start类的main方式，后台便启动成功

##### 前端

前端为在src/main/webapp/map.html，打开页面设置端口一致，设置好参数

![1589441114357](https://raw.githubusercontent.com/nvyougakki/capturebaidumaptiles/master/images/1589441114357.png)

红色为必填，其他可选

##### 关于图块地址

调出控制台，获取其中一个图块的url粘贴即可

##### 关于坐标范围

只能选择用上方的矩形选择框，在地图上画矩形区域

##### 关于地图样式

点击对应的连接，填如相应的数字，然后渲染