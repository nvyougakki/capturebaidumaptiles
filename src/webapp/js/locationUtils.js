
/**
 *   地图位置计算工具(将GPS坐标转换成百度地图坐标)
 *    参考文档：http://bbs.lbsyun.baidu.com/forum.php?mod=viewthread&tid=10923&qq-pf-to=pcqq.group
 *
 *    使用示例：批量转换坐标位置
 *
 *    var transferedData = GpsToBaiduPoints(prePoints);
 *    $.each(transferedData,function(index,point){
 *       console.log(point);
 *    });
 *
 *-------------------以下是提供的一个简单的访问接口-------------------------
 *    参数: points:new BMap.Point(lng,lat)的集合
 *    返回值:resultPoints:转换后 BMap.point点集
 *    function GpsToBaiduPoints(points){
 *        var resultPoints = [];
 *        $.each(points,function(index,point){
 *            //世界大地坐标转为百度坐标
 *            var _t = wgs2bd(point.lat,point.lng);
 *            var _BPoint = new BMap.Point(_t[1], _t[0]);
 *            resultPoints.push(_BPoint);
 *        });
 *        return resultPoints;
 *    }
 */
//默认提供一个接口直接调用
function GpsToBaiduPoints(points){
    var resultPoints = [];
    $.each(points,function(index,point){
        var _t = wgs2bd(point.lat,point.lng);
        var _BPoint = new BMap.Point(_t[1], _t[0]);
        resultPoints.push(_BPoint);
    });
    return resultPoints;
}

//////////////////////////////////////////
//////////////转换核心代码////////////////
//////////////////////////////////////////
var pi = 3.14159265358979324;
var a = 6378245.0;
var ee = 0.00669342162296594323;
var x_pi = 3.14159265358979324*3000.0/180.0;


//世界大地坐标转为百度坐标
function wgs2bd(lat,lon) {
    var wgs2gcjR = wgs2gcj(lat, lon);
    var gcj2bdR = gcj2bd(wgs2gcjR[0], wgs2gcjR[1]);
    return gcj2bdR;
}



function gcj2bd(lat,lon) {
    var x = lon, y = lat;
    var z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
    var theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
    var bd_lon = z * Math.cos(theta) + 0.0065;
    var bd_lat = z * Math.sin(theta) + 0.006;
    var result = [];
    result.push(bd_lat);
    result.push(bd_lon);
    return result;
}

function bd2gcj(lat,lon) {
    var x = lon - 0.0065, y = lat - 0.006;
    var z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
    var theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
    var gg_lon = z * Math.cos(theta);
    var gg_lat = z * Math.sin(theta);
    var result = [];
    result.push(gg_lat);
    result.push(gg_lon);
    return result;
}

function wgs2gcj(lat,lon) {
    var dLat = transformLat(lon - 105.0, lat - 35.0);
    var dLon = transformLon(lon - 105.0, lat - 35.0);
    var radLat = lat / 180.0 * pi;
    var magic = Math.sin(radLat);
    magic = 1 - ee * magic * magic;
    var sqrtMagic = Math.sqrt(magic);
    dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
    dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
    var mgLat = lat + dLat;
    var mgLon = lon + dLon;
    var result = [];
    result.push(mgLat);
    result.push(mgLon);
    return result;
}

function transformLat(lat,lon) {
    var ret = -100.0 + 2.0 * lat + 3.0 * lon + 0.2 * lon * lon + 0.1 * lat * lon + 0.2 * Math.sqrt(Math.abs(lat));
    ret += (20.0 * Math.sin(6.0 * lat * pi) + 20.0 * Math.sin(2.0 * lat * pi)) * 2.0 / 3.0;
    ret += (20.0 * Math.sin(lon * pi) + 40.0 * Math.sin(lon / 3.0 * pi)) * 2.0 / 3.0;
    ret += (160.0 * Math.sin(lon / 12.0 * pi) + 320 * Math.sin(lon * pi  / 30.0)) * 2.0 / 3.0;
    return ret;
}

function transformLon(lat,lon) {
    var ret = 300.0 + lat + 2.0 * lon + 0.1 * lat * lat + 0.1 * lat * lon + 0.1 * Math.sqrt(Math.abs(lat));
    ret += (20.0 * Math.sin(6.0 * lat * pi) + 20.0 * Math.sin(2.0 * lat * pi)) * 2.0 / 3.0;
    ret += (20.0 * Math.sin(lat * pi) + 40.0 * Math.sin(lat / 3.0 * pi)) * 2.0 / 3.0;
    ret += (150.0 * Math.sin(lat / 12.0 * pi) + 300.0 * Math.sin(lat / 30.0 * pi)) * 2.0 / 3.0;
    return ret;
}



/**
 * 根据方向角计算扇形点
 */


function getCenterPoi(angle,pointNum,range){
    var halfPoint=Math.ceil(pointNum/2);
    var everyAngle=(range/2)/halfPoint;
    var angleArr=[];
    angleArr.push(getPoi(angle));
    for (var int = 1; int <=halfPoint; int++) {
        var angleLeft=angle-everyAngle*int;
        var angleRight=angle+everyAngle*int;
        if(angleLeft<0){
            angleLeft=360+angleLeft;
        }

        if(angleRight>=360){
            angleRight=angleRight-360
        }
        angleArr.push(getPoi(angleLeft));
        angleArr.push(getPoi(angleRight));

    }
    return angleArr;

}


function getPoi(angle){

    console.log(angle)

    if(angle==360) angle=0;
    var par= 1+Math.sin(angle)*Math.sin(angle);

    var x2=1/par;
    var y2=Math.sin(angle)*Math.sin(angle)/par;
    var x;
    var y;
    if(angle>=0 && angle<=90){
        x=Math.sqrt(x2);
        y=Math.sqrt(y2);
    }else if(angle>90 && angle<=180 ){
        x=Math.sqrt(x2);
        y=-Math.sqrt(y2);
    }else if(angle>180 && angle<=270){
        x=-Math.sqrt(x2);
        y=-Math.sqrt(y2);
    }else{
        x=-Math.sqrt(x2);
        y=Math.sqrt(y2);
    }

    var Object={};
    Object.x=x;
    Object.y=y;
    return Object;
}






function getAngleRangeNew(angle,r){
    var array= [];
    if(angle-r<0){
        for(var i=angle+360-r;i<angle+360+r;i=i+0.5){
            array.push(i);
        }
    }else if(angle==360){
        for(var i=0;i<360;i=i+0.1){
            array.push(i);
        }
    }else{
        for(var i=angle-r;i<angle+r;i=i+0.5){
            array.push(i);
        }
    }
    return array;
}


function GeodeticPoint
(start,startBearing,distance)
{
    var a = 6378137.0;
    var b= 6356752.3142;
    var aSquared = a * a;
    var bSquared = b * b;
    var f = 1/298.257224;
    var phi1 =degree2rad(start.y);
    var alpha1 = degree2rad(startBearing);
    var cosAlpha1 = Math.cos(alpha1);
    var sinAlpha1 = Math.sin(alpha1);
    var s = distance;
    var tanU1 = (1.0 - f) * Math.tan(phi1);
    var cosU1 = 1.0 / Math.sqrt( 1.0 + tanU1 * tanU1 );
    var sinU1 = tanU1 * cosU1;

    // eq. 1
    var sigma1 = Math.atan2(tanU1, cosAlpha1);

    // eq. 2
    var sinAlpha = cosU1 * sinAlpha1;

    var sin2Alpha = sinAlpha * sinAlpha;
    var cos2Alpha = 1 - sin2Alpha;
    var uSquared = cos2Alpha * (aSquared - bSquared) / bSquared;

    // eq. 3
    var A = 1 + (uSquared / 16384) * (4096 + uSquared * (-768 + uSquared * (320 - 175 * uSquared)));

    // eq. 4
    var B = (uSquared / 1024) * (256 + uSquared * (-128 + uSquared * (74 - 47 * uSquared)));

    // iterate until there is a negligible change in sigma
    var deltaSigma;
    var sOverbA = s/(b * A);
    var sigma = sOverbA;
    var sinSigma;
    var prevSigma = sOverbA;
    var sigmaM;
    var cosSigmaM2;
    var cos2SigmaM2;

    for (;;)
    {
        // eq. 5
        sigmaM2 = 2.0*sigma1 + sigma;
        cosSigmaM2 = Math.cos(sigmaM2);
        cos2SigmaM2 = cosSigmaM2 * cosSigmaM2;
        sinSigma = Math.sin(sigma);
        var cosSignma = Math.cos(sigma);

        // eq. 6
        deltaSigma = B * sinSigma * (cosSigmaM2 + (B / 4.0) * (cosSignma * (-1 + 2 * cos2SigmaM2)
            - (B / 6.0) * cosSigmaM2 * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM2)));

        // eq. 7
        sigma = sOverbA + deltaSigma;

        // break after converging to tolerance
        if (Math.abs(sigma - prevSigma) < 0.0000000000001)
        {
            break;
        }

        prevSigma = sigma;
    }

    sigmaM2 = 2.0*sigma1 + sigma;
    cosSigmaM2 = Math.cos(sigmaM2);
    cos2SigmaM2 = cosSigmaM2 * cosSigmaM2;

    var cosSigma = Math.cos(sigma);
    sinSigma = Math.sin(sigma);

    // eq. 8
    var phi2 = Math.atan2( sinU1 * cosSigma + cosU1 * sinSigma * cosAlpha1,
        (1.0-f) * Math.sqrt( sin2Alpha + Math.pow(sinU1 * sinSigma - cosU1 * cosSigma * cosAlpha1, 2.0)));

    // eq. 9

    var lambda = Math.atan2(sinSigma * sinAlpha1, cosU1 * cosSigma - sinU1 * sinSigma * cosAlpha1);

    // eq. 10
    var C = (f / 16) * cos2Alpha * (4 + f * (4 - 3 * cos2Alpha));

    // eq. 11
    var L = lambda - (1 - C) * f * sinAlpha * (sigma + C * sinSigma * (cosSigmaM2 + C * cosSigma * (-1 + 2 * cos2SigmaM2)));

    // eq. 12
    var alpha2 = Math.atan2(sinAlpha, -sinU1 * sinSigma + cosU1 * cosSigma * cosAlpha1);

    // build result
    var latitude;
    var longitude;

    var lat2r = phi2;
    var lon2r =degree2rad(start.x) + L;

    latitude=rad2degree(lat2r);
    longitude=rad2degree(lon2r);
    // endBearing = new Angle();
    //endBearing.Radians = alpha2;

    //return new MapPoint(latitude, longitude);
    var object={};
    object.latitude=latitude;
    object.longitude=longitude;
    return object;
}


function path(string)
{
    var tmpArray = string.split("#");
    var ar = new Array();
    for(var i=0;i<tmpArray.length;i++)
    {
        var str = tmpArray[i];
        var longtitude = Number(str.substr(0,str.indexOf(",")));
        var latitude = Number(str.substr(str.indexOf(",")+1));
        var mp = new MapPoint(longtitude,latitude);
        ar.push(mp);
    }

    return ar;
}



function Rad(d)
{
    return d * Math.PI/180.0;
}

function distance(longtitude1,latitude1,longtitude2,latitude2)
{
    var r = 6378137.0;
    var radLat1 = Rad(latitude1);
    var radLat2 = Rad(latitude2);
    var a = radLat1-radLat2;
    var b = Rad(longtitude1)-Rad(longtitude2);
    var s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
    var dis = Math.round(s * r * 10000)/10000;
    return dis;
}

function r_RADIUS()
{
    return 6378137.0;
}
function degree2rad(degree)
{
    return degree*Math.PI/180;
}

function rad2degree(rad)
{
    return rad*180/Math.PI;
}



//----------------位置偏移-----------------

const VincentyConstants = {
    a: 6378137,
    b: 6356752.3142,
    f: 1/298.257223563
}
/**
 *Calculate destination point given start point lat/long (numeric degrees),
 * bearing (numeric degrees) & distance (in m).
 */
function destinationVincenty(lonlat, brng, dist) {
    // var u = this;
    var ct = VincentyConstants;
    var a = ct.a, b = ct.b, f = ct.f;

    var lon1 = lonlat.lon*1;  //乘一（*1）是为了确保经纬度的数据类型为number
    var lat1 = lonlat.lat*1;

    var s = dist;
    var alpha1 = rad(brng);
    var sinAlpha1 = Math.sin(alpha1);
    var cosAlpha1 = Math.cos(alpha1);

    var tanU1 = (1-f) * Math.tan(rad(lat1));
    var cosU1 = 1 / Math.sqrt((1 + tanU1*tanU1)), sinU1 = tanU1*cosU1;
    var sigma1 = Math.atan2(tanU1, cosAlpha1);
    var sinAlpha = cosU1 * sinAlpha1;
    var cosSqAlpha = 1 - sinAlpha*sinAlpha;
    var uSq = cosSqAlpha * (a*a - b*b) / (b*b);
    var A = 1 + uSq/16384*(4096+uSq*(-768+uSq*(320-175*uSq)));
    var B = uSq/1024 * (256+uSq*(-128+uSq*(74-47*uSq)));

    var sigma = s / (b*A), sigmaP = 2*Math.PI;
    while (Math.abs(sigma-sigmaP) > 1e-12) {
        var cos2SigmaM = Math.cos(2*sigma1 + sigma);
        var sinSigma = Math.sin(sigma);
        var cosSigma = Math.cos(sigma);
        var deltaSigma = B*sinSigma*(cos2SigmaM+B/4*(cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)-
            B/6*cos2SigmaM*(-3+4*sinSigma*sinSigma)*(-3+4*cos2SigmaM*cos2SigmaM)));
        sigmaP = sigma;
        sigma = s / (b*A) + deltaSigma;
    }

    var tmp = sinU1*sinSigma - cosU1*cosSigma*cosAlpha1;
    var lat2 = Math.atan2(sinU1*cosSigma + cosU1*sinSigma*cosAlpha1,
        (1-f)*Math.sqrt(sinAlpha*sinAlpha + tmp*tmp));
    var lambda = Math.atan2(sinSigma*sinAlpha1, cosU1*cosSigma - sinU1*sinSigma*cosAlpha1);
    var C = f/16*cosSqAlpha*(4+f*(4-3*cosSqAlpha));
    var L = lambda - (1-C) * f * sinAlpha *
        (sigma + C*sinSigma*(cos2SigmaM+C*cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)));

    var revAz = Math.atan2(sinAlpha, -tmp);  // final bearing


    var lon_destina = lon1*1+deg(L);

    //var num_lon_dest = lon_destina*1;

    //var lon_destina = new Number;

    var lonlat_destination = {lon: lon_destina, lat: deg(lat2)};

    return lonlat_destination;
}

/**
 * 度换成弧度
 * @param  {Float} d  度
 * @return {[Float}   弧度
 */
function rad(d)
{
    return d * Math.PI / 180.0;
}

/**
 * 弧度换成度
 * @param  {Float} x 弧度
 * @return {Float}   度
 */
function deg(x) {
    return x*180/Math.PI;
}

