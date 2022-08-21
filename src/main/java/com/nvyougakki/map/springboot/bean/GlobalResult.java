package com.nvyougakki.map.springboot.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GlobalResult<T> {

    private T data;

    private Integer code;

    private String msg;

    private boolean success = false;

    public GlobalResult(T data, Integer code, boolean success) {
        this.data = data;
        this.code = code;
        this.success = success;
    }

    public GlobalResult ok(T data) {
        return this.ok(data, "成功");
    }

    public GlobalResult ok(T data, String msg) {
        this.data = data;
        this.success = true;
        this.code = 2000;
        this.msg = msg;
        return this;
    }

    public GlobalResult fail(int code, String errorMsg) {
        this.success = false;
        this.msg = errorMsg;
        this.code = code;
        return this;
    }

    public static <T> GlobalResult<T> success(T data) {
        return new GlobalResult<T>().ok(data);
    }

    public static <T> GlobalResult<T> success(T data, String msg) {
        return new GlobalResult<T>().ok(data, msg);
    }

    public static <T> GlobalResult<T> success() {
        return success(null);
    }

    public static <T> GlobalResult<T> error(String errorMsg) {
        return new GlobalResult<T>().fail(4000, errorMsg);
    }

    public static <T> GlobalResult<T> error(int code, String errorMsg) {
        return new GlobalResult<T>().fail(code, errorMsg);
    }

}
