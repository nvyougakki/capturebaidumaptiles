<template>
  <div class="full-bg">
    <div class="map" ref="map">
    </div>
    <div ref="icon">
      <img ref="anchorImg"  id="anchorImg" src="../assets/images/engineer.svg"/>
<!--      <i class="el-icon-location" style="font-size: 100px;"></i>-->
    </div>
  </div>


</template>

<script>
  import 'ol/ol.css';
  import Map from 'ol/Map';
  import { Tile,Vector as VectorLayer } from 'ol/layer';
  import TileGrid from 'ol/tilegrid/TileGrid';
  import { XYZ, TileImage } from 'ol/source';
  import Vector from 'ol/source/Vector';
  import OSM from 'ol/source/OSM';
  import TileLayer from 'ol/layer/Tile';
  import View from 'ol/View';
  import { transform } from 'ol/proj'
  import Overlay from 'ol/Overlay';
  import { Projection, addProjection, addCoordinateTransforms } from 'ol/proj'
  import {lngLatToMercator, mercatorToLngLat} from '../utils/bd09'
  import GeoJSON from 'ol/format/GeoJSON';						//用来加载矢量数据
  import WebGLPointsLayer from 'ol/layer/WebGLPoints';
  import Feature from 'ol/Feature';
  import Polygon from 'ol/geom/Polygon';
  import Point from 'ol/geom/Point';
  import {bbox} from 'ol/loadingstrategy';
  import {toStringXY} from 'ol/coordinate';
  import * as style from 'ol/style'
  import VectorSource from 'ol/source/Vector';
  import Polyline from 'ol/format/Polyline';
  import { LineString } from 'ol/geom'
  // import { LineString } from 'ol/feature'
  import axios from 'axios'
  import {wgs2bd} from '../utils/locationUtils'
  export default {
    name: '',
    mounted() {
      this.initMap()
    },
    methods: {
      initMap() {
        let that = this

        /*定义百度投影，这是实现无偏移加载百度地图离线瓦片核心所在。
   网上很多相关资料在用OpenLayers加载百度地图离线瓦片时都认为投影就是EPSG:3857(也就是Web墨卡托投影)。
   事实上这是错误的，因此无法做到无偏移加载。
   百度地图有自己独特的投影体系，必须在OpenLayers中自定义百度投影，才能实现无偏移加载。
   百度投影实现的核心文件为bd09.js，在迈高图官网可以找到查看这个文件。*/
        var projBD09 = new Projection({
          code: 'BD:09',
          extent : [-20037726.37,-11708041.66,20037726.37,12474104.17],
          units: 'm',
          axisOrientation: 'neu',
          global: false
        });

        addProjection(projBD09);
        addCoordinateTransforms("EPSG:4326", "BD:09",
          function (coordinate) {
            let arr = wgs2bd(coordinate[1], coordinate[0])
            coordinate[0] = arr[1]
            coordinate[1] = arr[0]
            return lngLatToMercator(coordinate);
          },
          function (coordinate) {
            return mercatorToLngLat(coordinate);
          }
        );

        /*定义百度地图分辨率与瓦片网格*/
        var resolutions = [];
        for (var i = 0; i <= 18; i++) {
          resolutions[i] = Math.pow(2, 18 - i);
        }

        var tilegrid = new TileGrid({
          origin: [0, 0],
          resolutions: resolutions
        });

        /*加载百度地图离线瓦片不能用ol.source.XYZ，ol.source.XYZ针对谷歌地图（注意：是谷歌地图）而设计，
        而百度地图与谷歌地图使用了不同的投影、分辨率和瓦片网格。因此这里使用ol.source.TileImage来自行指定
        投影、分辨率、瓦片网格。*/
        var source = new TileImage({
          projection: "BD:09",
          tileGrid: tilegrid,
          tileUrlFunction: function(tileCoord, pixelRatio, proj) {
            var z = tileCoord[0];
            var x = tileCoord[1];
            var y = Math.abs(tileCoord[2]) - 1;

            // return `https://maponline2.bdimg.com/tile/?qt=vtile&x=${x}&y=${y}&z=${z}&styles=pl&scaler=1&udt=20220630&from=jsapi2_0`
            return `https://maponline0.bdimg.com/starpic/?qt=satepc&u=x=${x};y=${y};z=${z};v=009;type=sate&fm=46&udt=20220630`
          }
        });

        var mapLayer = new Tile({
          source: source
        });
        var source1 = new TileImage({
          projection: "BD:09",
          tileGrid: tilegrid,
          tileUrlFunction: function(tileCoord, pixelRatio, proj) {
            var z = tileCoord[0];
            var x = tileCoord[1];
            var y = Math.abs(tileCoord[2]) - 1;

            // return `https://maponline2.bdimg.com/tile/?qt=vtile&x=${x}&y=${y}&z=${z}&styles=pl&scaler=1&udt=20220630&from=jsapi2_0`
            return `https://maponline1.bdimg.com/tile/?qt=vtile&x=${x}&y=${y}&z=${z}&styles=sl&udt=20220630`
          }
        });

        var mapLayer1 = new Tile({
          source: source1
        });


        var map = new Map({
          layers: [
            mapLayer, mapLayer1
          ],
          view: new View({
            center: transform([119.807885, 30.735649], 'EPSG:4326', 'BD:09'),
            projection: 'BD:09',
            zoom: 6
          }),
          target: that.$refs.map
        });
        axios.get('/data/line.json').then(resp => {
          // return
          let points = resp.data
            // .features.filter(o => o.properties.country == 'cn')
            //
          // console.log(points)
           // = []
          let features = points.map(o => {
            return new Feature({
              type: 'Point',
              // prop: o.properties,
              geometry: new Point(
                transform(o, 'EPSG:4326', 'BD:09'))
            })

          })

          const vectorSource = new Vector({
            features: []
          });
          vectorSource.addFeatures(features)
          let pointsLayer = new WebGLPointsLayer({
            source: vectorSource,
            zIndex: 9999,
            style: {
              symbol: {
                symbolType: 'image',
                src: '/location.png',
                size: 20,
                color: '#000'
              }

            },

            disableHitDetection: true,	//是否开启碰撞检测
          });
          map.addLayer(pointsLayer);
        })

        axios.get('/data/line.json').then(resp => {
          let points = resp.data
            // .filter(o => o.properties.country == 'cn')
          // console.log(points)
           // = []
          let newPoints = points.map(o => {
            return transform(o, 'EPSG:4326', 'BD:09')
          })
          let features = ["R", "G", "B"].map(o => {
            return new Feature({
              type: o,
              geometry: new LineString(newPoints)
            });
          })


          // var vectorLine = new Vector({});
          // vectorLine.addFeature(featureLine);
          let styles = {
            'R': new style.Style({
              fill: new style.Fill({ color: '#FF0000', weight: 10 }),
              stroke: new style.Stroke({ color: '#FF0000', width: 12 })
            }),
            'G': new style.Style({
              fill: new style.Fill({ color: '#00FF00', weight: 10 }),
              stroke: new style.Stroke({ color: '#00FF00', width: 8 })
            }),
            'B': new style.Style({
              fill: new style.Fill({ color: '#0000FF', weight: 10 }),
              stroke: new style.Stroke({ color: '#0000FF', width: 4 })
            })
          }
          var vectorLineLayer = new VectorLayer({
            source: new VectorSource({
                features: features
            }
            ),
            style: function(feature) {
              console.log(feature.values_.type)
              return styles[feature.values_.type]
            }
          });

          // var vectorLineLayer1 = new VectorLayer({
          //   source: vectorLine,
          //   zIndex: 100,
          //   style: new style.Style({
          //     fill: new style.Fill({ color: '#FF0000', weight: 4 }),
          //     stroke: new style.Stroke({ color: '#FF0000', width: 2 })
          //   })
          // });
          //
          // var vectorLineLayer2 = new VectorLayer({
          //   source: vectorLine,
          //   zIndex: 99,
          //   style: new style.Style({
          //     fill: new style.Fill({ color: '#0000FF', weight: 4 }),
          //     stroke: new style.Stroke({ color: '#0000FF', width: 6 })
          //   })
          // });
         /*  let lineFeature = new Feature({
            type: 'Point',
            geometry: new Polyline(
              newPoints)
          })
          //
          // const vectorSource = new Vector({
          //   features: []
          // });
          const vectorLayer = new VectorLayer({
            source: new VectorSource({
              features: [lineFeature],
            }),
            style: new style.Style({
              stroke: new style.Stroke({
                color: '#000000',
                width: 100
              })
            }),
          }); */
          // vectorSource.addFeatures(features)
          // let pointsLayer = new WebGLPointsLayer({
          //   source: vectorSource,
          //
          //
          //   //disableHitDetection: true,	//是否开启碰撞检测
          // });
          map.addLayer(vectorLineLayer);
          // map.addLayer(vectorLineLayer1);
          // map.addLayer(vectorLineLayer2);
        })

       /*   */
      }
    }

  }
</script>

<style scoped>

.map {
  width: 100%;
  height: 100%;
}
#anchorImg
{
  display: block;
  position: absolute;
  animation: zoom 5s;
  animation-iteration-count: infinite; /* 一直重复动画 */
  -moz-animation: zoom 5s; /* Firefox */
  -moz-animation-iteration-count: infinite; /* 一直重复动画 */
  -webkit-animation: zoom 5s;  /* Safari 和 Chrome */
  -webkit-animation-iteration-count: infinite; /* 一直重复动画 */
  -o-animation: zoom 5s; /* Opera */
  -o-animation-iteration-count: infinite; /* 一直重复动画 */
}
.full-bg{
  background-color: transparent;
  width: 100%;
  height: calc(100vh - 84px);
  margin: -20px;
  position: relative;
}

</style>
