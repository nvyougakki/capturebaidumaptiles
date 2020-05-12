package com.nvyougakki.map.bean;

import java.util.concurrent.atomic.LongAdder;

public class Computed {

    //当前下载总量
    private LongAdder finish;

    //需要下载的总量
    private int total;

    //当前图层
    private PicAxis currPxis;

    public Computed() {
        this.finish = new LongAdder();
    }

    public long getFinish() {
        return finish.longValue();
    }

    public void setHasDownload(LongAdder hasDownload) {
        this.finish = hasDownload;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public PicAxis getCurrPxis() {
        return currPxis;
    }

    public void setCurrPxis(PicAxis currPxis) {
        this.currPxis = currPxis;
    }

    public double getFinishRate(){
        if(total == 0) return 0;
        return getFinish()*1.0/total;
    }

    public void addDownloadOne(){
        this.finish.increment();
    }
}
