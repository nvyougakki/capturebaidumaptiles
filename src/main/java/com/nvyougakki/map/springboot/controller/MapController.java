package com.nvyougakki.map.springboot.controller;

import com.nvyougakki.map.springboot.bean.GlobalResult;
import com.nvyougakki.map.springboot.bean.MapConfig;
import com.nvyougakki.map.springboot.service.MapService;
import jdk.nashorn.internal.objects.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/map")
public class MapController {

    @Autowired
    MapService mapService;

    @PostMapping("/start")
    public GlobalResult<Object> start(@RequestBody MapConfig mapConfig) {
        MapConfig nowMapConfig = MapService.nowMapConfig;
        if(nowMapConfig != null && nowMapConfig.isRunning()) {
            return GlobalResult.error("执行中");
        }
        MapService.nowMapConfig = mapConfig;
        mapConfig.setRunning(true);
        try {
            mapService.getPic(mapConfig);
        } catch (Exception e) {
            e.printStackTrace();
            mapConfig.setRunning(false);
        }

        return GlobalResult.success("提交成功，后台执行中");
    }

    @GetMapping("/process")
    public GlobalResult<Object> process() {
        MapConfig nowMapConfig = MapService.nowMapConfig;
        if(nowMapConfig == null) return GlobalResult.error("未开始");
        Map<String, Object> result = new HashMap<>();
        result.put("total", nowMapConfig.getTotal());
        result.put("hasDownload", nowMapConfig.getHasDownload().longValue());
        result.put("isRunning", nowMapConfig.isRunning());

        return GlobalResult.success(result);
    }
    @GetMapping("/stop")
    public GlobalResult stop() throws InterruptedException {

        if(MapService.nowMapConfig != null  && MapService.nowMapConfig.isRunning()) {
//            MapService.executorService.shutdownNow();
            MapService.executorService.shutdown();
            MapService.executorService.awaitTermination(800, TimeUnit.MILLISECONDS);
            MapService.nowMapConfig.setRunning(false);
        }

        return GlobalResult.success();
    }


}
