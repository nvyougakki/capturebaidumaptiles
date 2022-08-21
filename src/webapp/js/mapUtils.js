function initMap(center, target) {
    var projBD09 = new ol.proj.Projection({
        code: 'BD:09',
        extent: [-20037726.37, -11708041.66, 20037726.37, 12474104.17],
        units: 'm',
        axisOrientation: 'neu',
        global: false
    });

    ol.proj.addProjection(projBD09);
    ol.proj.addCoordinateTransforms("EPSG:4326", "BD:09",
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
    return new ol.Map({
        view: new ol.View({
            center: ol.proj.transform(center, 'EPSG:4326', 'BD:09'),
            projection: 'BD:09',
            zoom: 6
        }),
        target: target
    })
}

function transformLocation(center) {
    return ol.proj.transform(center, 'EPSG:4326', 'BD:09')
}

function addMapLayer(map) {


    /*定义百度地图分辨率与瓦片网格*/
    var resolutions = [];
    for (var i = 0; i <= 18; i++) {
        resolutions[i] = Math.pow(2, 18 - i);
    }

    var tilegrid = new ol.tilegrid.TileGrid({
        origin: [0, 0],
        resolutions: resolutions
    });


    /*加载百度地图离线瓦片不能用ol.source.XYZ，ol.source.XYZ针对谷歌地图（注意：是谷歌地图）而设计，
    而百度地图与谷歌地图使用了不同的投影、分辨率和瓦片网格。因此这里使用ol.source.TileImage来自行指定
    投影、分辨率、瓦片网格。*/
    var source = new ol.source.TileImage({
        projection: "BD:09",
        tileGrid: tilegrid,
        tileUrlFunction: function (tileCoord, pixelRatio, proj) {
//openlayer5的版本

            var z = tileCoord[0];
            var x = tileCoord[1];
            var y = Math.abs(tileCoord[2]) - 1;

            // return `https://maponline0.bdimg.com/starpic/?qt=satepc&u=x=${x};y=${y};z=${z};v=009;type=sate&fm=46&udt=20220630`
            // return `https://maponline0.bdimg.com/tile/?qt=vtile&x=${x}&y=${y}&z=${z}&styles=pl&scaler=1&udt=20220819&from=jsapi3_0`
            return `http://localhost/offlineMap/${z}/${x}/${y}.png`
        }
    });
    let layer = new ol.layer.Tile({
        id: 'map-layer',
        source: source
    });
    return addLayer(map, layer)
}

function addLayerBySource(map, id, source, styles) {
    if(hasLayer(map, id)) {
        return getLayerById(map, id).setSource(source)
    } else {
        const layer = new ol.layer.Vector({
            id: id,
            source: source,
            style: styles,
        });
        return addLayer(map, layer)
    }
}

function addLayer(map, layer) {
    if(hasLayer(map, layer.get('id'))) {
        return
    }
    map.addLayer(layer)
    return layer
}

function hasLayer(map, id) {
    return map.getAllLayers().filter(o => o.get('id') == id).length > 0
}
function getLayerById(map, id) {
    if(hasLayer(map, id)) {
        return map.getAllLayers().filter(o => o.get('id') == id)[0]
    }
    return null

}


function mapAnimate(map, param) {
    map.getView().animate({
        zoom: param.zoom,
        center: param.center,
        duration: 1000
    });
}

function getZoomByLevel(key){
    return {
        'province': 7,
        'city': 9
    }[key] || 12
}

function addGeoLayer(map, geoJson) {
    geoJson = JSON.parse(JSON.stringify(geoJson))
    geoJson.geometry.coordinates[0][0] = geoJson.geometry.coordinates[0][0].map(o => transformLocation(o))
    mapAnimate(map, {
        zoom: getZoomByLevel(geoJson.properties.level),
        center: transformLocation(geoJson.properties.center)
    })
    let that = this
    const source = new ol.source.Vector({
        features: new ol.format.GeoJSON().readFeatures(geoJson),
    });
    let styles = [
        /* We are using two different styles for the polygons:
         *  - The first style is for the polygons themselves.
         *  - The second style is to draw the vertices of the polygons.
         *    In a custom `geometry` function the vertices of a polygon are
         *    returned as `MultiPoint` geometry, which will be used to render
         *    the style.
         */
        new ol.style.Style({
            stroke: new ol.style.Stroke({
                color: 'blue',
                width: 3,
            }),
            fill: new ol.style.Fill({
                color: 'rgba(0, 0, 255, 0.1)',
            }),
        }),
        new ol.style.Style({
            image: new ol.style.Circle({
                radius: 5,
                fill: new ol.style.Fill({
                    color: 'orange',
                }),
            }),
            geometry: function (feature) {
                // return the coordinates of the first ring of the polygon
                const coordinates = feature.getGeometry().getCoordinates()[0];
                return new ol.geom.MultiPoint(coordinates);
            },
        }),
    ];
    return addLayerBySource(map,'geo-layer', source, styles)
}
