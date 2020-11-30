package cn.yuyingwai.springbootblog.dao;

import cn.yuyingwai.springbootblog.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminUserDao {

    /**
     * 根据参数查询用户列表
     */
    List<AdminUser> findAdminUsers(Map param);

    /**
     * 查询用户总数
     */
    int getTotalAdminUser(Map param);

    /**
     * 根据用户名和密码获取登录记录
     */
    AdminUser getAdminUserByUserNameAndPassword(@Param("userName") String userName, @Param("passwordMD5") String passwordMD5);

}
