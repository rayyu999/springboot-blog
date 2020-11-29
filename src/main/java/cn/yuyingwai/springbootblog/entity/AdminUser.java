package cn.yuyingwai.springbootblog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AdminUser implements Serializable {

    private Long id;

    private String userName;

    private String password;

    private String userToken;

    private int isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

}
