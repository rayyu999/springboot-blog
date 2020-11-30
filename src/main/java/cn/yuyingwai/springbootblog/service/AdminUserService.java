package cn.yuyingwai.springbootblog.service;

import cn.yuyingwai.springbootblog.util.PageResult;
import cn.yuyingwai.springbootblog.util.PageUtil;

public interface AdminUserService {

    /**
     * 分页功能
     */
    PageResult getAdminUserPage(PageUtil pageUtil);

    /**
     * 登陆功能
     */

}
