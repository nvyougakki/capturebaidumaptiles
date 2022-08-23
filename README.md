# 抓取百度地图图层

## 后台启动

启动类：com.nvyougakki.map.springboot.Application

## 使用说明

- **选择区域**
  选择对应的省、市、区县会自动定位

- **选择图层**

  选择需要抓取的图层

- **图块地址**

  图块地址为百度图层的地址，对应x、y、z的值换成${x}、${y}、${z}（如源图层地址：https://maponline0.bdimg.com/tile/?qt=vtile&x=***3***&y=***1***&z=***0***&styles=pl&scaler=1&udt=20220819&from=jsapi3_0，修改为https://maponline0.bdimg.com/tile/?qt=vtile&x=${x}&y=${y}&z=${z}&styles=pl&scaler=1&udt=20220819&from=jsapi3_0到输入框，点击渲染可预览底图）

- **请求地址**
  后台项目启动的地址。

- **存放目录**
  后台抓取图片存放目录

- **线程数**
  可填入线程数

- **进度条**

  查看图片下载进度



![1661240179937](https://raw.githubusercontent.com/nvyougakki/capturebaidumaptiles/master/images/1661240179937.png)