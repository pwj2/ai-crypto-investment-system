package com.digitalcoin.holdings_service.util;

/**
 * 统一响应结果工具类
 * @param <T> 数据类型
 */
public class Result<T> {
    private int code;    // 状态码：0-成功，非0-失败
    private String msg;  // 响应消息
    private T data;      // 响应数据

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(0);
        r.setData(data);
        return r;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        Result<T> r = new Result<>();
        r.setCode(0);
        r.setMsg("操作成功");
        return r;
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> fail(String msg) {
        Result<T> r = new Result<>();
        r.setCode(-1);
        r.setMsg(msg);
        return r;
    }

    /**
     * 失败响应（自定义错误码）
     */
    public static <T> Result<T> fail(int code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    /**
     * 错误响应（通常用于系统错误）
     */
    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.setCode(1);
        r.setMsg(msg);
        return r;
    }
}