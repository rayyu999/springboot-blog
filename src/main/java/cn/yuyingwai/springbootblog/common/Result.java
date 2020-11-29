package cn.yuyingwai.springbootblog.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    // 响应码 200为成功
    private int resultCode;
    // 响应msg
    private String message;
    // 返回数据
    private T data;

    public Result() {
    }

    public Result(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public Result failure(String code) {
        return new Result(500, "服务错误");
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultCode=" + resultCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

}
