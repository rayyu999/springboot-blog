package cn.yuyingwai.springbootblog.common;

import java.io.Serializable;

public class Result<T> implements Serializable {

    // 响应码 200为成功
    private int resultCode;
    // 响应msg
    private String message;
    // 返回数据
    private T data;

}
