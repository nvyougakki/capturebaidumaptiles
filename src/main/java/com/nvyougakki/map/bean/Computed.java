package com.nvyougakki.map.bean;

import java.util.concurrent.atomic.LongAdder;

public class Computed {

    private LongAdder hasDownload;

    private int total;

    private PicAxis currPxis;

    public Computed() {
        this.hasDownload = new LongAdder();
    }

    public LongAdder getHasDownload() {
        return hasDownload;
    }

    public void setHasDownload(LongAdder hasDownload) {
        this.hasDownload = hasDownload;
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

    public void addDownloadOne(){
        this.hasDownload.increment();
    }
}
