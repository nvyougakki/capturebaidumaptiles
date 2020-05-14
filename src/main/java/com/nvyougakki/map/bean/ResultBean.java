package com.nvyougakki.map.bean;

public class ResultBean {

    private ResultType type;

    private String msg;

    public ResultType getType() {
        return type;
    }

    public void setType(ResultType type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
