# Spring Boot 博客

-----

# 后台管理部分



## 登录模块



### 登录流程设计

登录的本质即身份验证和登录状态的保持，编码实现：

首先，在数据库中查询这条用户记录；

* 如果不存在这条记录则表示身份验证失败，登录流程终止；
* 如果存在这条记录，则表示身份验证成功，接下来则需要进行登录状态的存储和验证了。

用户登录成功后我们将用户信息放到 session 对象中，之后再实现一个拦截器，在访问项目时判断 session 中是否有用户信息，有则放行请求，没有就跳转到登录页面。



### AdminLTE3 模板整合

整合过程其实是把 AdminLTE3 代码压缩包中我们需要的样式文件、js 文件、图片等静态资源放入 Spring Boot 项目的静态资源目录下，比如 static 目录或者其他我们设置的静态资源目录。

这里将对应的文件放在 static 目录下的 admin 文件夹中：

![](http://images.yingwai.top/picgo/20201202222317.png)



### 实现

#### 前端页面

##### 登录页面

由于选用了 AdminLTE3 作为模板，就直接改造其登录页面即可，在 templates/admin 目录中新建 login.html 模板页面，模板引擎我们选择的是 Thymeleaf，代码如下：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>personal blog | Log in</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="shortcut icon" th:href="@{/admin/dist/img/favicon.png}" />
    <!-- Font Awesome -->
    <link rel="stylesheet" th:href="@{/admin/dist/css/font-awesome.min.css}" />
    <!-- Ionicons -->
    <link rel="stylesheet" th:href="@{/admin/dist/css/ionicons.min.css}" />
    <!-- Theme style -->
    <link rel="stylesheet" th:href="@{/admin/dist/css/adminlte.min.css}" />
    <style>
        canvas {
            display: block;
            vertical-align: bottom;
        }
        #particles {
            background-color: #f7fafc;
            position: absolute;
            top: 0;
            width: 100%;
            height: 100%;
            z-index: -1;
        }
    </style>
</head>
<body class="hold-transition login-page">
<div id="particles"></div>
<div class="login-box">
    <div class="login-logo" style="color: #007bff;">
        <h1>personal blog</h1>
    </div>
    <!-- /.login-logo -->
    <div class="card">
        <div class="card-body login-card-body">
            <p class="login-box-msg">your personal blog , enjoy it</p>
            <form th:action="@{/admin/login}" method="post">
                <div
                        th:if="${not #strings.isEmpty(session.errorMsg)}"
                        class="form-group"
                >
                    <div
                            class="alert alert-danger"
                            th:text="${session.errorMsg}"
                    ></div>
                </div>
                <div class="form-group has-feedback">
                    <span class="fa fa-user form-control-feedback"></span>
                    <input
                            type="text"
                            id="userName"
                            name="userName"
                            class="form-control"
                            placeholder="请输入账号"
                            required="true"
                    />
                </div>
                <div class="form-group has-feedback">
                    <span class="fa fa-lock form-control-feedback"></span>
                    <input
                            type="password"
                            id="password"
                            name="password"
                            class="form-control"
                            placeholder="请输入密码"
                            required="true"
                    />
                </div>
                <div class="row">
                    <div class="col-6">
                        <input
                                type="text"
                                class="form-control"
                                name="verifyCode"
                                placeholder="请输入验证码"
                                required="true"
                        />
                    </div>
                    <div class="col-6">
                        <img
                                alt="单击图片刷新！"
                                class="pointer"
                                th:src="@{/common/kaptcha}"
                                onclick="this.src='/common/kaptcha?d='+new Date()*1"
                        />
                    </div>
                </div>
                <div class="form-group has-feedback"></div>
                <div class="row">
                    <div class="col-8"></div>
                    <div class="col-4">
                        <button
                                type="submit"
                                class="btn btn-primary btn-block btn-flat"
                        >
                            登录
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<script th:src="@{/admin/plugins/jquery/jquery.min.js}"></script>
<!-- Bootstrap 4 -->
<script
        th:src="@{/admin/plugins/bootstrap/js/bootstrap.bundle.min.js}"
></script>
<script th:src="@{/admin/dist/js/plugins/particles.js}"></script>
<script th:src="@{/admin/dist/js/plugins/login-bg-particles.js}"></script>
</body>
</html>
```

该页面时直接修改的 AdminLTE3 模板的登录页，将文案修改为中文，并微调了一下页面布局，同时增加了验证码的设计。

用户在输入账号、密码和验证码后，点击登录按钮后将会向后端发送登录请求，请求地址为 admin/login，请求类型为 post，在 form 表单中已经定义了登陆的请求路径：

```html
<form th:action="@{/admin/login}" method="post"></form>
```



##### 登录后的跳转页面

在同目录下新建 index.html 文件：

###### index.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<header th:replace="admin/header::header-fragment"></header>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <!-- 引入页面头header-fragment -->
    <div th:replace="admin/header::header-nav"></div>
    <aside th:fragment="sidebar-fragment(path)" class="main-sidebar sidebar-dark-primary elevation-4">
        <!-- Brand Logo -->
        <a th:href="@{/admin/index}" class="brand-link">
            <img th:src="@{/admin/dist/img/logo.png}" alt="ssm-cluster Logo" class="brand-image img-circle elevation-3"
                 style="opacity: .8">
            <span class="brand-text font-weight-light">my blog</span>
        </a>
        <!-- Sidebar -->
        <div class="sidebar">
            <!-- Sidebar user panel (optional) -->
            <div class="user-panel mt-3 pb-3 mb-3 d-flex">
                <div class="image">
                    <img th:src="@{/admin/dist/img/avatar5.png}" class="img-circle elevation-2" alt="User Image">
                </div>
                <div class="info">
                    <a href="#" class="d-block" th:text="${session.loginUser}"></a>
                </div>
            </div>
            <!-- Sidebar Menu -->
            <nav class="mt-2">
                <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu"
                    data-accordion="false">
                    <!-- Add icons to the links using the .nav-icon class
                         with font-awesome or any other icon font library -->
                    <li class="nav-header">首页</li>
                    <li class="nav-item">
                        <a th:href="@{/admin/index}" class="nav-link active">
                            <i class="nav-icon fa fa-dashboard"></i>
                            <p>
                                首页
                            </p>
                        </a>
                    </li>
                    <li class="nav-header">系统管理</li>
                    </li>
                </ul>
            </nav>
            <!-- /.sidebar-menu -->
        </div>
        <!-- /.sidebar -->
    </aside>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <div class="content-header">
            <div class="container-fluid">
            </div><!-- /.container-fluid -->
        </div>
        <!-- Main content -->
        <div class="content">
            <div class="container-fluid">
                <div class="card-header">
                    <h3 class="card-title">管理首页</h3>
                </div> <!-- /.card-body -->
                <div class="row" style="margin-top: 40px;border-top:0px;">
                    My Blog 后台管理系统首页
                </div>
            </div><!-- /.container-fluid -->
        </div>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <!-- 引入页脚footer-fragment -->
    <div th:replace="admin/footer::footer-fragment"></div>
</div>
<!-- jQuery -->
<script th:src="@{/admin/plugins/jquery/jquery.min.js}"></script>
<!-- jQuery UI 1.11.4 -->
<script th:src="@{/admin/plugins/jQueryUI/jquery-ui.min.js}"></script>
<!-- Bootstrap 4 -->
<script th:src="@{/admin/plugins/bootstrap/js/bootstrap.bundle.min.js}"></script>
<!-- AdminLTE App -->
<script th:src="@{/admin/dist/js/adminlte.min.js}"></script>
</body>
</html>
```

这里引入的页头和页脚也要新建：

###### header.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head th:fragment="header-fragment">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>my personal blog | 后台管理系统</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Font Awesome -->
    <link rel="shortcut icon" th:href="@{/admin/dist/img/favicon.png}"/>
    <link rel="stylesheet" th:href="@{/admin/dist/css/font-awesome.min.css}">
    <!-- Ionicons -->
    <link rel="stylesheet" th:href="@{/admin/dist/css/ionicons.min.css}">
    <link rel="stylesheet" th:href="@{/admin/dist/css/main.css}">
    <link rel="stylesheet" th:href="@{/admin/plugins/bootstrap/css/bootstrap.css}"/>
    <link rel="stylesheet" th:href="@{/admin/plugins/sweetalert/sweetalert.css}"/>
    <link rel="stylesheet" th:href="@{/admin/plugins/jqgrid-5.3.0/ui.jqgrid-bootstrap4.css}"/>
    <!-- Theme style -->
    <link rel="stylesheet" th:href="@{/admin/dist/css/adminlte.min.css}">
</head>
<!-- Navbar -->
<nav class="main-header navbar navbar-expand bg-white navbar-light border-bottom" th:fragment="header-nav">
    <!-- Left navbar links -->
    <ul class="navbar-nav">
        <li class="nav-item">
            <a class="nav-link" data-widget="pushmenu" href="#"><i class="fa fa-bars"></i></a>
        </li>
        <li class="nav-item d-none d-sm-inline-block">
            <a th:href="@{/admin/index}" class="nav-link">Dashboard</a>
        </li>
    </ul>
    <!-- Right navbar links -->
    <ul class="navbar-nav ml-auto">
        <li class="nav-item dropdown">
            <a class="nav-link" data-toggle="dropdown" href="#">
                <i class="fa fa-user">&nbsp;&nbsp;作者</i>
            </a>
            <div class="dropdown-menu dropdown-menu-lg dropdown-menu-right">
                <div class="dropdown-divider"></div>
                <a href="#" class="dropdown-item">
                    <i class="fa fa-user-o mr-2"></i> 姓名
                    <span class="float-right text-muted text-sm">Ray</span>
                </a>
                <div class="dropdown-divider"></div>
                <a href="#" class="dropdown-item">
                    <i class="fa fa-user-secret mr-2"></i> 身份
                    <span class="float-right text-muted text-sm">Java菜鸡</span>
                </a>
                <div class="dropdown-divider"></div>
                <a href="#" class="dropdown-item">
                    <i class="fa fa-address-card mr-2"></i> 邮箱
                    <span class="float-right text-muted text-sm">yuyingwai@outlook.com</span>
                </a>
            </div>
        </li>
    </ul>
</nav>
<!-- /.navbar -->
</html>
```

###### footer.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<footer class="main-footer" th:fragment="footer-fragment">
    <strong>Copyright &copy; 2020 <a href="##">chefssalad</a>.</strong>
    All rights reserved.
    <div class="float-right d-none d-sm-inline-block">
        <b>my personal blog #Version</b> 1.0
    </div>
</footer>
</html>
```



#### 表结构设计

博客系统正式的用户模块表结构设计如下：

```sql
CREATE DATABASE /*!32312 IF NOT EXISTS*/`my_blog_db` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `my_blog_db`;
/*Table structure for table `tb_admin_user` */
DROP TABLE IF EXISTS `tb_admin_user`;
CREATE TABLE `tb_admin_user` (
  `admin_user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '管理员id',
  `login_user_name` varchar(50) NOT NULL COMMENT '管理员登陆名称',
  `login_password` varchar(50) NOT NULL COMMENT '管理员登陆密码',
  `nick_name` varchar(50) NOT NULL COMMENT '管理员显示昵称',
  `locked` tinyint(4) DEFAULT '0' COMMENT '是否锁定 0未锁定 1已锁定无法登陆',
  PRIMARY KEY (`admin_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*Data for the table `tb_admin_user` */
insert  into `tb_admin_user`(`admin_user_id`,`login_user_name`,`login_password`,`nick_name`,`locked`) values (1,'admin','e10adc3949ba59abbe56e057f20f883e','十三',0);
```

新增了一张表，并在表中新增了一条用户数据，之后我们在演示登陆功能时会用到。



#### 后端功能实现

##### 添加管理员用户实体类

```java
package cn.yuyingwai.springbootblog.entity;

import lombok.Data;

@Data
public class AdminUser {

    private Integer adminUserId;

    private String loginUserName;

    private String loginPassword;

    private String nickName;

    private Byte locked;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", adminUserId=").append(adminUserId);
        sb.append(", loginUserName=").append(loginUserName);
        sb.append(", loginPassword=").append(loginPassword);
        sb.append(", nickName=").append(nickName);
        sb.append(", locked=").append(locked);
        sb.append("]");
        return sb.toString();
    }

}
```



##### 添加mybatis以及kaptcha(用于生成验证码)的依赖

pom.xml：

```xml
<!-- mybatis -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.4</version>
</dependency>
<!-- 验证码 -->
<dependency>
    <groupId>com.github.penggle</groupId>
    <artifactId>kaptcha</artifactId>
    <version>2.3.2</version>
</dependency>
```



##### DAO层

AdminUserDao.java：

```java
package cn.yuyingwai.springbootblog.dao;

import cn.yuyingwai.springbootblog.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface AdminUserDao {

    int insert(AdminUser record);

    int insertSelective(AdminUser record);

    /**
     * 登陆方法
     * @param userName
     * @param password
     * @return
     */
    AdminUser login(@Param("userName") String userName, @Param("password") String password);

}
```

这里的 `login()` 方法就是根据从前端接受的用户名以及密码，到数据库中查找对比，下面是对应的 xml 映射文件：

AdminUserMapper.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.yuyingwai.springbootblog.dao.AdminUserDao">
    <resultMap id="BaseResultMap" type="cn.yuyingwai.springbootblog.entity.AdminUser">
        <id column="admin_user_id" jdbcType="INTEGER" property="adminUserId" />
        <result column="login_user_name" jdbcType="VARCHAR" property="loginUserName" />
        <result column="login_password" jdbcType="VARCHAR" property="loginPassword" />
        <result column="nick_name" jdbcType="VARCHAR" property="nickName" />
        <result column="locked" jdbcType="TINYINT" property="locked" />
    </resultMap>
    <sql id="Base_Column_List">
    admin_user_id, login_user_name, login_password, nick_name, locked
  </sql>

    <select id="login" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List" />
        from tb_admin_user
        where login_user_name = #{userName,jdbcType=VARCHAR} AND login_password=#{password,jdbcType=VARCHAR} AND locked = 0
    </select>
    
</mapper>
```



##### Service层

首先定义一个接口规定方法：

```java
package cn.yuyingwai.springbootblog.service;

import cn.yuyingwai.springbootblog.entity.AdminUser;

public interface AdminUserService {

    AdminUser login(String userName, String password);

}
```

然后是该接口的实现类：

```java
package cn.yuyingwai.springbootblog.service.impl;

import cn.yuyingwai.springbootblog.dao.AdminUserDao;
import cn.yuyingwai.springbootblog.entity.AdminUser;
import cn.yuyingwai.springbootblog.service.AdminUserService;
import cn.yuyingwai.springbootblog.util.MD5Util;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Resource
    private AdminUserDao adminUserDao;

    @Override
    public AdminUser login(String userName, String password) {
        String passwordMd5 = MD5Util.MD5Encode(password, "UTF-8");
        return adminUserDao.login(userName, passwordMd5);
    }
}
```

因为密码在数据库中是以密文的形式存储的，所以这里 `login()` 方法首先将收到的密码用工具类进行加密后再交给持久层进行比对。

**密文工具类** MD5Util.java：

```java
package cn.yuyingwai.springbootblog.util;

import java.security.MessageDigest;

public class MD5Util {

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

}
```



##### Controller层

```java
package cn.yuyingwai.springbootblog.controller.admin;

import cn.yuyingwai.springbootblog.entity.AdminUser;
import cn.yuyingwai.springbootblog.service.AdminUserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminUserService adminUserService;

    @GetMapping({"/login"})
    public String login() {
        return "admin/login";
    }

    @PostMapping(value = "/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,
                        HttpSession session) {
        if (StringUtils.isEmpty(verifyCode)) {
            session.setAttribute("errorMsg", "验证码不能为空");
            return "admin/login";
        }
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            session.setAttribute("errorMsg", "用户名或密码不能为空");
            return "admin/login";
        }
        String kaptchaCode = session.getAttribute("verifyCode") + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            session.setAttribute("errorMsg", "验证码错误");
            return "admin/login";
        }
        AdminUser adminUser = adminUserService.login(userName, password);
        if (adminUser != null) {
            session.setAttribute("loginUser", adminUser.getNickName());
            session.setAttribute("loginUserId", adminUser.getAdminUserId());
            // session过期时间设置为7200秒 即两小时
            session.setMaxInactiveInterval(60 * 60 * 2);
            return "redirect:/admin/index";
        } else {
            session.setAttribute("errorMsg", "登陆失败");
            return "admin/login";
        }
    }

    @GetMapping({"", "/", "/index", "/index.html"})
    public String index() {
        return "admin/index";
    }

    @GetMapping("/header")
    public String header() {
        return "admin/header";
    }

    @GetMapping("/footer")
    public String footer() {
        return "admin/footer";
    }

}
```



##### 使用 Kaptcha 生成验证码

###### Kaptcha 设置

在 `controller.common` 包下新建 KaptchaConfig.java：

```java
package cn.yuyingwai.springbootblog.controller.common;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha getDefaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 图片边框
        properties.put("kaptcha.border", "no");
        // 字体颜色
        properties.put("kaptcha.textproducer.font.color", "black");
        // 图片宽
        properties.put("kaptcha.image.width", "160");
        // 图片高
        properties.put("kaptcha.image.height", "40");
        // 字体大小
        properties.put("kaptcha.textproducer.font.size", "30");
        // 验证码长度
        properties.put("kaptcha.textproducer.char.space", "5");
        // 字体
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");

        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

}
```

这里主要是对于生成的验证码的一些设置。



###### 为前端页面提供验证码图片

在设置好后，还需要为前端页面提供验证码，需要与前端请求的路径对应：

```java
package cn.yuyingwai.springbootblog.controller.common;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Controller
public class CommonController {

    @Autowired
    private DefaultKaptcha captchaProducer;

    @GetMapping("/common/kaptcha")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        byte[] captchaOutputStream;
        ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
        try {
            // 生成验证码字符串并保存到session中
            String verifyCode = captchaProducer.createText();
            httpServletRequest.getSession().setAttribute("verifyCode", verifyCode);
            BufferedImage challenge = captchaProducer.createImage(verifyCode);
            ImageIO.write(challenge, "jpg", imgOutputStream);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        captchaOutputStream = imgOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);

        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();

        responseOutputStream.write(captchaOutputStream);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

}
```



### 登录拦截器

在上面实现了管理员的登陆功能，该功能已经完成，但是身份认证的整个流程并没有完善，该流程中应该包括登陆功能、身份认证、访问拦截、退出功能，我们仅仅完成了第一步，因此接下来将会对该流程进行完善。

#### 拦截器介绍

定义一个 Interceptor 非常简单方式也有几种，这里简单列举两种：

- 新建类要实现 Spring 的 HandlerInterceptor 接口
- 新建类继承实现了 HandlerInterceptor 接口的实现类，例如已经提供的实现了 HandlerInterceptor 接口的抽象类 HandlerInterceptorAdapter

HandlerInterceptor 方法介绍：

```java
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception;

    void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception;

    void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception;
```

- **preHandle**：在业务处理器处理请求之前被调用。预处理，可以进行编码、安全控制、权限校验等处理；
- **postHandle**：在业务处理器处理请求执行完成后，生成视图之前执行。
- **afterCompletion**：在 DispatcherServlet 完全处理完请求后被调用，可用于清理资源等，返回处理（已经渲染了页面）；



#### 定义拦截器

新建 intercepto 包，在包中新建 AdminLoginInterceptor 类，该类需要实现 HandlerInterceptor 接口，代码如下：

```java
package cn.yuyingwai.springbootblog.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 后台系统身份验证拦截器
 */
@Component
public class AdminLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (uri.startsWith("/admin") && null == request.getSession().getAttribute("loginUser")) {
            request.getSession().setAttribute("errorMsg", "请登录");
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return false;
        } else {
            request.getSession().removeAttribute("errorMsg");
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
```

`preHandle` 方法中读取当前 session 中是否存在 `loginUser` 对象，如果不存在则返回 false 并跳转至登录页面。



#### 配置拦截器

新建 config 包，之后新建 MyBlogWebMvcConfigurer 类并实现 WebMvcConfigurer 接口，代码如下：

```java
package cn.yuyingwai.springbootblog.config;

import cn.yuyingwai.springbootblog.interceptor.AdminLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyBlogWebMvcConfigurer implements WebMvcConfigurer {
    
    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;
    
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加一个拦截器，拦截以/admin为前缀的url路径
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login")
                .excludePathPatterns("/admin/dist/**")
                .excludePathPatterns("/admin/plugins/**");
    }
    
}
```

在该配置类中，添加刚刚新增的 AdminLoginInterceptor 登录拦截器，并对该拦截器所拦截的路径进行配置，由于后端管理系统的所有请求路径都以 /admin 开头，所以拦截的路径为 /admin/** ，但是登陆页面以及部分静态资源文件也是以 /admin 开头，所以需要将这些路径排除，配置如上。



## 用户模块

前面实现了登录功能，但用户模块不只是登录功能，还包括用户信息修改、安全退出的功能。

### 前端页面

#### 修改信息页面

在 resources/templates/admin/ 目录下新建 profile.html：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<header th:replace="admin/header::header-fragment"></header>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <!-- 引入页面头header-fragment -->
    <div th:replace="admin/header::header-nav"></div>
    <!-- 引入工具栏sidebar-fragment -->
    <aside th:fragment="sidebar-fragment(path)" class="main-sidebar sidebar-dark-primary elevation-4">
        <!-- Brand Logo -->
        <a th:href="@{/admin/index}" class="brand-link">
            <img th:src="@{/admin/dist/img/logo.png}" alt="ssm-cluster Logo" class="brand-image img-circle elevation-3"
                 style="opacity: .8">
            <span class="brand-text font-weight-light">my blog</span>
        </a>
        <!-- Sidebar -->
        <div class="sidebar">
            <!-- Sidebar user panel (optional) -->
            <div class="user-panel mt-3 pb-3 mb-3 d-flex">
                <div class="image">
                    <img th:src="@{/admin/dist/img/avatar5.png}" class="img-circle elevation-2" alt="User Image">
                </div>
                <div class="info">
                    <a href="#" class="d-block" th:text="${session.loginUser}"></a>
                </div>
            </div>
            <!-- Sidebar Menu -->
            <nav class="mt-2">
                <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu"
                    data-accordion="false">
                    <!-- Add icons to the links using the .nav-icon class
                         with font-awesome or any other icon font library -->
                    <li class="nav-header">首页</li>
                    <li class="nav-item">
                        <a th:href="@{/admin/index}" class="nav-link">
                            <i class="nav-icon fa fa-dashboard"></i>
                            <p>
                                首页
                            </p>
                        </a>
                    </li>
                    <li class="nav-header">系统管理</li>
                    <li class="nav-item">
                        <a th:href="@{/admin/profile}" class="nav-link active">
                            <i class="fa fa-user-secret nav-icon"></i>
                            <p>修改密码</p>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a th:href="@{/admin/logout}" class="nav-link">
                            <i class="fa fa-sign-out nav-icon"></i>
                            <p>安全退出</p>
                        </a>
                    </li>
                    </li>
                </ul>
            </nav>
            <!-- /.sidebar-menu -->
        </div>
        <!-- /.sidebar -->
    </aside>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <div class="content-header">
            <div class="container-fluid">
            </div><!-- /.container-fluid -->
        </div>
        <!-- Main content -->
        <section class="content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-md-6">
                        <div class="card card-primary card-outline">
                            <div class="card-header">
                                <h3 class="card-title">基本信息</h3>
                            </div> <!-- /.card-body -->
                            <div class="card-body">
                                <form role="form" id="userNameForm">
                                    <div class="form-group col-sm-8">
                                        <div class="alert alert-danger" id="updateUserName-info"
                                             style="display: none;"></div>
                                    </div>
                                    <!-- text input -->
                                    <div class="form-group">
                                        <label>登陆名称</label>
                                        <input type="text" class="form-control" id="loginUserName"
                                               name="loginUserName"
                                               placeholder="请输入登陆名称" required="true" th:value="${loginUserName}">
                                    </div>
                                    <div class="form-group">
                                        <label>昵称</label>
                                        <input type="text" class="form-control" id="nickName"
                                               name="nickName"
                                               placeholder="请输入昵称" required="true" th:value="${nickName}">
                                    </div>
                                    <div class="card-footer">
                                        <button type="button" id="updateUserNameButton" onsubmit="return false;"
                                                class="btn btn-danger float-right">确认修改
                                        </button>
                                    </div>
                                </form>
                            </div><!-- /.card-body -->
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card card-primary card-outline">
                            <div class="card-header">
                                <h3 class="card-title">修改密码</h3>
                            </div> <!-- /.card-body -->
                            <div class="card-body">
                                <form role="form" id="userPasswordForm">
                                    <div class="form-group col-sm-8">
                                        <div class="alert alert-danger updatePassword-info" id="updatePassword-info"
                                             style="display: none;"></div>
                                    </div>
                                    <!-- input states -->
                                    <div class="form-group">
                                        <label class="control-label"><i class="fa fa-key"></i> 原密码</label>
                                        <input type="text" class="form-control" id="originalPassword"
                                               name="originalPassword"
                                               placeholder="请输入原密码" required="true">
                                    </div>
                                    <div class="form-group">
                                        <label class="control-label"><i class="fa fa-key"></i> 新密码</label>
                                        <input type="text" class="form-control" id="newPassword" name="newPassword"
                                               placeholder="请输入新密码" required="true">
                                    </div>
                                    <div class="card-footer">
                                        <button type="button" id="updatePasswordButton" onsubmit="return false;"
                                                class="btn btn-danger float-right">确认修改
                                        </button>
                                    </div>
                                </form>
                            </div><!-- /.card-body -->
                        </div>
                    </div>
                </div>
            </div><!-- /.container-fluid -->
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <!-- 引入页脚footer-fragment -->
    <div th:replace="admin/footer::footer-fragment"></div>
</div>
<!-- jQuery -->
<script th:src="@{/admin/plugins/jquery/jquery.min.js}"></script>
<!-- jQuery UI 1.11.4 -->
<script th:src="@{/admin/plugins/jQueryUI/jquery-ui.min.js}"></script>
<!-- Bootstrap 4 -->
<script th:src="@{/admin/plugins/bootstrap/js/bootstrap.bundle.min.js}"></script>
<!-- AdminLTE App -->
<script th:src="@{/admin/dist/js/adminlte.min.js}"></script>
<!-- public.js -->
<script th:src="@{/admin/dist/js/public.js}"></script>
<!-- profile -->
<script th:src="@{/admin/dist/js/profile.js}"></script>
</body>
</html>
```

#### 登录后的跳转页面

同时要在 index.html 中的 `<li class="nav-header">系统管理</li>` 下面添加如下代码：

```html
<li class="nav-item">
    <a th:href="@{/admin/profile}" class="nav-link">
        <i class="fa fa-user-secret nav-icon"></i>
        <p>修改密码</p>
    </a>
</li>
<li class="nav-item">
    <a th:href="@{/admin/logout}" class="nav-link">
        <i class="fa fa-sign-out nav-icon"></i>
        <p>安全退出</p>
    </a>
</li>
```



### 后端实现

#### AdminController

* 用户主页面：

  ```java
  @GetMapping("/profile")
  public String profile(HttpServletRequest request) {
      Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
      AdminUser adminUser = adminUserService.getUserDetailById(loginUserId);
      if (adminUser == null) {
          return "admin/login";
      }
      request.setAttribute("path", "profile");
      request.setAttribute("loginUserName", adminUser.getLoginUserName());
      request.setAttribute("nickName", adminUser.getNickName());
      return "admin/profile";
  }
  ```

  上面的代码主要是将用户的信息返回给前端动态显示出来；

* 修改密码：

  ```java
  @PostMapping("/profile/password")
  @ResponseBody
  public String passwordUpdate(HttpServletRequest request,
                               @RequestParam("originalPassword") String originalPassword,
                               @RequestParam("newPassword") String newPassword) {
      if (StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)) {
          return "参数不能为空";
      }
      Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
      if (adminUserService.updatePassword(loginUserId, originalPassword, newPassword)) {
          // 修改成功后清空session中的数据，前端控制跳转至登录页
          request.getSession().removeAttribute("loginUserId");
          request.getSession().removeAttribute("loginUser");
          request.getSession().removeAttribute("errorMsg");
          return "success";
      } else {
          return "修改失败";
      }
  }
  ```

* 修改昵称：

  ```java
  @PostMapping("/profile/name")
  @ResponseBody
  public String nameUpdate(HttpServletRequest request,
                           @RequestParam("loginUserName") String loginUserName,
                           @RequestParam("nickName") String nickName) {
      if (StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)) {
          return "参数不能为空";
      }
      Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
      if (adminUserService.updateName(loginUserId, loginUserName, nickName)) {
          return "success";
      } else {
          return "修改失败";
      }
  }
  ```

* 登出：

  ```java
  @GetMapping("/logout")
      public String logout(HttpServletRequest request) {
          request.getSession().removeAttribute("loginUserId");
          request.getSession().removeAttribute("loginUser");
          request.getSession().removeAttribute("errorMsg");
          return "admin/login";
      }
  ```

#### AdminUserService

添加以下方法：

```java
/**
 * 获取用户信息
 * @param loginUserId
 * @return
 */
AdminUser getUserDetailById(Integer loginUserId);

/**
 * 修改当前登录用户密码
 * @param loginUserId
 * @param originalPassword
 * @param newPassword
 * @return
 */
Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword);

/**
 * 修改当前登录用户的名称信息
 * @param loginUserId
 * @param loginUserName
 * @param nickName
 * @return
 */
Boolean updateName(Integer loginUserId, String loginUserName, String nickName);
```

##### AdminUserServiceImpl

```java
@Override
public AdminUser getUserDetailById(Integer loginUserId) {
    return adminUserDao.selectByPrimaryKey(loginUserId);
}

@Override
public Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword) {
    AdminUser adminUser = adminUserDao.selectByPrimaryKey(loginUserId);
    // 当前用户非空才可以进行更改
    if (adminUser != null) {
        String originalPasswordMd5 = MD5Util.MD5Encode(originalPassword, "UTF-8");
        String newPasswordMd5 = MD5Util.MD5Encode(newPassword, "UTF-8");
        // 比较原密码是否正确
        if (originalPasswordMd5.equals(adminUser.getLoginPassword())) {
            // 设置新密码并修改
            adminUser.setLoginPassword(newPasswordMd5);
            if (adminUserDao.updateByPrimaryKeySelective(adminUser) > 0) {
                // 修改成功则返回true
                return true;
            }
        }
    }
    return false;
}

@Override
public Boolean updateName(Integer loginUserId, String loginUserName, String nickName) {
    AdminUser adminUser = adminUserDao.selectByPrimaryKey(loginUserId);
    // 当前用户非空才可以进行更改
    if (adminUser != null) {
        // 设置新密码并修改
        adminUser.setLoginUserName(loginUserName);
        adminUser.setNickName(nickName);
        if (adminUserDao.updateByPrimaryKeySelective(adminUser) > 0) {
            // 修改成功则返回true
            return true;
        }
    }
    return false;
}
```

#### AdminUserDao

添加以下方法：

```java
AdminUser selectByPrimaryKey(Integer adminUserId);

int updateByPrimaryKeySelective(AdminUser record);
```

#### AdminUserMapper

```java
<select id="selectByPrimaryKey" parameterType="java.lang.Integer" resultMap="BaseResultMap">
    select
    <include refid="Base_Column_List" />
    from tb_admin_user
    where admin_user_id = #{adminUserId,jdbcType=INTEGER}
</select>
<update id="updateByPrimaryKeySelective" parameterType="cn.yuyingwai.springbootblog.entity.AdminUser">
        update tb_admin_user
    <set>
    <if test="loginUserName != null">
        login_user_name = #{loginUserName,jdbcType=VARCHAR},
</if>
    <if test="loginPassword != null">
        login_password = #{loginPassword,jdbcType=VARCHAR},
</if>
    <if test="nickName != null">
        nick_name = #{nickName,jdbcType=VARCHAR},
</if>
    <if test="locked != null">
        locked = #{locked,jdbcType=TINYINT},
</if>
    </set>
    where admin_user_id = #{adminUserId,jdbcType=INTEGER}
</update>
```



## 分类模块

### 持久层相关

#### 表结构设计

在进行接口设计和具体的功能实现前，首先将表结构确定下来，每篇文章都会被归类到一个类别下，一个类别下会有多篇文章，分类实体与文章实体的关系是一对多的关系，因此在表结构设计时，在文章表中设置一个分类关联字段即可，分类表只需要将分类相关的字段定义好，分类实体与文章实体的关系交给文章表来维护即可（后续讲到文章表时再介绍），分类表的 SQL 设计如下，直接执行如下 SQL 语句即可：

```sql
USE `my_blog_db`;

/*Table structure for table `tb_blog_category` */

CREATE TABLE `tb_blog_category` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分类表主键',
  `category_name` varchar(50) NOT NULL COMMENT '分类的名称',
  `category_icon` varchar(50) NOT NULL COMMENT '分类的图标',
  `category_rank` int(11) NOT NULL DEFAULT '1' COMMENT '分类的排序值 被使用的越多数值越大',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0=否 1=是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```



#### BlogCategory 实体类

```java
package cn.yuyingwai.springbootblog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BlogCategory {

    private Integer categoryId;

    private String categoryName;

    private String categoryIcon;

    private Integer categoryRank;

    private Byte isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", categoryId=").append(categoryId);
        sb.append(", categoryName=").append(categoryName);
        sb.append(", categoryIcon=").append(categoryIcon);
        sb.append(", categoryRank=").append(categoryRank);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }

}
```



#### BlogCategoryDao.java

```java
package cn.yuyingwai.springbootblog.dao;

import cn.yuyingwai.springbootblog.entity.BlogCategory;
import cn.yuyingwai.springbootblog.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BlogCategoryDao {

    int deleteByPrimaryKey(Integer categoryId);

    int insert(BlogCategory record);

    int insertSelective(BlogCategory record);

    BlogCategory selectByPrimaryKey(Integer categoryId);

    BlogCategory selectByCategoryName(String categoryName);

    int updateByPrimaryKeySelective(BlogCategory record);

    int updateByPrimaryKey(BlogCategory record);

    List<BlogCategory> findCategoryList(PageQueryUtil pageUtil);

    List<BlogCategory> selectByCategoryIds(@Param("categoryIds") List<Integer> categoryIds);

    int getTotalCategories(PageQueryUtil pageUtil);

    int deleteBatch(Integer[] ids);

}
```



#### BlogCategoryMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.yuyingwai.springbootblog.dao.BlogCategoryDao">
    <resultMap id="BaseResultMap" type="cn.yuyingwai.springbootblog.entity.BlogCategory">
        <id column="category_id" jdbcType="INTEGER" property="categoryId"/>
        <result column="category_name" jdbcType="VARCHAR" property="categoryName"/>
        <result column="category_icon" jdbcType="VARCHAR" property="categoryIcon"/>
        <result column="category_rank" jdbcType="INTEGER" property="categoryRank"/>
        <result column="is_deleted" jdbcType="TINYINT" property="isDeleted"/>
        <result column="create_time" jdbcType="TIMESTAMP" property="createTime"/>
    </resultMap>
    <sql id="Base_Column_List">
        category_id, category_name, category_icon, category_rank, is_deleted, create_time
    </sql>
    <select id="findCategoryList" parameterType="Map" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_blog_category
        where is_deleted=0
        order by category_rank desc,create_time desc
        <if test="start!=null and limit!=null">
            limit #{start},#{limit}
        </if>
    </select>

    <select id="getTotalCategories" parameterType="Map" resultType="int">
        select count(*)  from tb_blog_category
        where is_deleted=0
    </select>

    <select id="selectByPrimaryKey" parameterType="java.lang.Integer" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_blog_category
        where category_id = #{categoryId,jdbcType=INTEGER} AND is_deleted = 0
    </select>
    <select id="selectByCategoryIds" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_blog_category
        where category_id IN
        <foreach collection="categoryIds" item="item" index="index"
                 open="(" separator="," close=")">#{item}
        </foreach>
        AND is_deleted = 0
    </select>

    <select id="selectByCategoryName" parameterType="java.lang.String" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_blog_category
        where category_name = #{categoryName,jdbcType=INTEGER} AND is_deleted = 0
    </select>
    <update id="deleteByPrimaryKey" parameterType="java.lang.Integer">
        UPDATE tb_blog_category SET  is_deleted = 1
        where category_id = #{categoryId,jdbcType=VARCHAR} AND is_deleted = 0
    </update>
    <update id="deleteBatch">
        update tb_blog_category
        set is_deleted=1 where category_id in
        <foreach item="id" collection="array" open="(" separator="," close=")">
            #{id}
        </foreach>
    </update>
    <insert id="insert" parameterType="cn.yuyingwai.springbootblog.entity.BlogCategory">
        insert into tb_blog_category (category_id, category_name, category_icon,
                                      category_rank, is_deleted, create_time
        )
        values (#{categoryId,jdbcType=INTEGER}, #{categoryName,jdbcType=VARCHAR}, #{categoryIcon,jdbcType=VARCHAR},
                #{categoryRank,jdbcType=INTEGER}, #{isDeleted,jdbcType=TINYINT}, #{createTime,jdbcType=TIMESTAMP}
               )
    </insert>
    <insert id="insertSelective" parameterType="cn.yuyingwai.springbootblog.entity.BlogCategory">
        insert into tb_blog_category
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="categoryId != null">
                category_id,
            </if>
            <if test="categoryName != null">
                category_name,
            </if>
            <if test="categoryIcon != null">
                category_icon,
            </if>
            <if test="categoryRank != null">
                category_rank,
            </if>
            <if test="isDeleted != null">
                is_deleted,
            </if>
            <if test="createTime != null">
                create_time,
            </if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="categoryId != null">
                #{categoryId,jdbcType=INTEGER},
            </if>
            <if test="categoryName != null">
                #{categoryName,jdbcType=VARCHAR},
            </if>
            <if test="categoryIcon != null">
                #{categoryIcon,jdbcType=VARCHAR},
            </if>
            <if test="categoryRank != null">
                #{categoryRank,jdbcType=INTEGER},
            </if>
            <if test="isDeleted != null">
                #{isDeleted,jdbcType=TINYINT},
            </if>
            <if test="createTime != null">
                #{createTime,jdbcType=TIMESTAMP},
            </if>
        </trim>
    </insert>
    <update id="updateByPrimaryKeySelective" parameterType="cn.yuyingwai.springbootblog.entity.BlogCategory">
        update tb_blog_category
        <set>
            <if test="categoryName != null">
                category_name = #{categoryName,jdbcType=VARCHAR},
            </if>
            <if test="categoryIcon != null">
                category_icon = #{categoryIcon,jdbcType=VARCHAR},
            </if>
            <if test="categoryRank != null">
                category_rank = #{categoryRank,jdbcType=INTEGER},
            </if>
            <if test="isDeleted != null">
                is_deleted = #{isDeleted,jdbcType=TINYINT},
            </if>
            <if test="createTime != null">
                create_time = #{createTime,jdbcType=TIMESTAMP},
            </if>
        </set>
        where category_id = #{categoryId,jdbcType=INTEGER}
    </update>
    <update id="updateByPrimaryKey" parameterType="com.site.blog.my.core.entity.BlogCategory">
        update tb_blog_category
        set category_name = #{categoryName,jdbcType=VARCHAR},
            category_icon = #{categoryIcon,jdbcType=VARCHAR},
            category_rank = #{categoryRank,jdbcType=INTEGER},
            is_deleted = #{isDeleted,jdbcType=TINYINT},
            create_time = #{createTime,jdbcType=TIMESTAMP}
        where category_id = #{categoryId,jdbcType=INTEGER}
    </update>
</mapper>
```



### 分页数据的格式及获取

分页使用到了 JqGrid 插件，在 JqGrid 整合中有如下代码：

```json
jsonReader: {
  root: "data.list", //数据列表模型
  page: "data.currPage", //当前页码
  total: "data.totalPage", //数据总页码
  records: "data.totalCount" //数据总记录数
  }
```

这里定义的是 jsonReader 对象如何对后端返回的 json 数据进行解析，比如数据列表为何读取 "data.list"，当前页码为何读取 "data.currPage"，这些都是由后端返回的数据格式所决定的，因此我们需要对后端返回的数据格式进行定义。

#### 定义后端响应结果的数据格式

```java
package cn.yuyingwai.springbootblog.util;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private int resultCode;
    private String message;
    private T data;

    public Result() {
    }

    public Result(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
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
```

#### 定义工具类生成响应结果

ResultGenerator.java 对各种生成响应结果的方法进行了封装：

```java
package cn.yuyingwai.springbootblog.util;

import org.springframework.util.StringUtils;

/**
 * 响应结果生成工具
 */
public class ResultGenerator {

    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";
    private static final String DEFAULT_FAIL_MESSAGE = "FAIL";
    private static final int RESULT_CODE_SUCCESS = 200;
    private static final int RESULT_CODE_SERVER_ERROR = 500;

    public static Result genSuccessResult() {
        Result result = new Result();
        result.setResultCode(RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        return result;
    }

    public static Result genSuccessResult(String message) {
        Result result = new Result();
        result.setResultCode(RESULT_CODE_SUCCESS);
        result.setMessage(message);
        return result;
    }

    public static Result genSuccessResult(Object data) {
        Result result = new Result();
        result.setResultCode(RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        result.setData(data);
        return result;
    }

    public static Result genFailResult(String message) {
        Result result = new Result();
        result.setResultCode(RESULT_CODE_SERVER_ERROR);
        if (StringUtils.isEmpty(message)) {
            result.setMessage(DEFAULT_FAIL_MESSAGE);
        } else {
            result.setMessage(message);
        }
        return result;
    }

    public static Result genErrorResult(int code, String message) {
        Result result = new Result();
        result.setResultCode(code);
        result.setMessage(message);
        return result;
    }
    
}
```

#### 定义分页结果集的数据格式

```java
package cn.yuyingwai.springbootblog.util;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult implements Serializable {

    //总记录数
    private int totalCount;
    //每页记录数
    private int pageSize;
    //总页数
    private int totalPage;
    //当前页数
    private int currPage;
    //列表数据
    private List<?> list;

    /**
     * 分页
     *
     * @param list       列表数据
     * @param totalCount 总记录数
     * @param pageSize   每页记录数
     * @param currPage   当前页数
     */
    public PageResult(List<?> list, int totalCount, int pageSize, int currPage) {
        this.list = list;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currPage = currPage;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }

}
```

#### 对前端的请求参数进行封装

PageQueryUtil.java 将前端的请求参数封装到一个 map 中，方便持久层进行查询：

```java
package cn.yuyingwai.springbootblog.util;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class PageQueryUtil extends LinkedHashMap<String, Object> {

    //当前页码
    private int page;
    //每页条数
    private int limit;

    public PageQueryUtil(Map<String, Object> params) {
        this.putAll(params);

        //分页参数
        this.page = Integer.parseInt(params.get("page").toString());
        this.limit = Integer.parseInt(params.get("limit").toString());
        this.put("start", (page - 1) * limit);
        this.put("page", page);
        this.put("limit", limit);
    }

    @Override
    public String toString() {
        return "PageUtil{" +
                "page=" + page +
                ", limit=" + limit +
                '}';
    }

}
```



### 分类模块接口设计及实现

为了让页面体验更加友好，就不采用传统的 MVC 跳转模式，一个功能一个页面，这种交互感觉有些浪费，翻页的时候，翻一页跳转一次也比较繁琐，添加或者新增的时候也要进行页面跳转，所以这些功能的实现就采用通过 Ajax 异步与后端交互数据，当使用者点击了页面上的元素，此时触发响应的 js 事件，进而通过 Ajax 的方式向后端请求数据，前端再根据后端返回的数据内容去进行响应的展示逻辑，在前面的个人信息修改中其实用到的就是这种方式。

分类模块在后台管理系统中有5个接口，分别是：

- 分类列表分页接口
- 添加分类接口
- 根据 id 获取单条分类记录接口
- 修改分类接口
- 删除分类接口



#### 分类列表分页接口

列表接口负责接收前端传来的分页参数，如 `page` 、`limit` 等参数，之后将数据总数和对应页面的数据列表查询出来并封装为分页数据返回给前端。

##### 控制层代码

```java
@Controller
@RequestMapping("/admin")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String categoryPage(HttpServletRequest request) {
        request.setAttribute("path", "categories");
        return "admin/category";
    }

    /**
     * 分类列表
     * @param params
     * @return
     */
    @RequestMapping(value = "/categories/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(categoryService.getBlogCategoryPage(pageUtil));
    }

}
```

##### 业务层代码

```java
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private BlogCategoryDao blogCategoryDao;

    /**
     * 利用PageQueryUtil封装的前端参数，获得当前页的分页数据，
     * 并将其封装为PageResult，方便前端的JqGrid使用
     * @param pageUtil
     * @return
     */
    @Override
    public PageResult getBlogCategoryPage(PageQueryUtil pageUtil) {
        List<BlogCategory> categoryList = blogCategoryDao.findCategoryList(pageUtil);
        int total = blogCategoryDao.getTotalCategories(pageUtil);
        PageResult pageResult = new PageResult(categoryList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
    
   /**
     * 获得总分类数
     * @return
     */
    @Override
    public int getTotalCategories() {
        return blogCategoryDao.getTotalCategories(null);
    }
}
```



#### 添加分类接口

添加接口负责接收前端的 POST 请求并处理其中的参数，接收的参数为 categoryName 字段和 categoryIcon 字段，categoryName 为分类名称，categoryIcon 字段为分类的图标字段。

##### 控制层代码

在 CategoryController.java 中添加如下方法：

```java
    /**
     * 分类添加
     * @param categoryName
     * @param categoryIcon
     * @return
     */
    @RequestMapping(value = "/categories/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestParam("categoryName") String categoryName,
                       @RequestParam("categoryIcon") String categoryIcon) {
        if (StringUtils.isEmpty(categoryName)) {
            return ResultGenerator.genFailResult("请输入分类名称！");
        }
        if (StringUtils.isEmpty(categoryIcon)) {
            return ResultGenerator.genFailResult("请选择分类图标！");
        }
        if (categoryService.saveCategory(categoryName, categoryIcon)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("分类名称重复");
        }
    }
```

##### 业务层代码

在 CategoryServiceImpl.java 中添加如下方法：

```java
    /**
     * 给定类名和类图标保存分类
     * @param categoryName
     * @param categoryIcon
     * @return
     */
    @Override
    public Boolean saveCategory(String categoryName, String categoryIcon) {
        BlogCategory temp = blogCategoryDao.selectByCategoryName(categoryName);
        if (temp == null) {
            BlogCategory blogCategory = new BlogCategory();
            blogCategory.setCategoryName(categoryName);
            blogCategory.setCategoryIcon(categoryIcon);
            return blogCategoryDao.insertSelective(blogCategory) > 0;
        }
        return false;
    }
```

添加接口中，首先会对参数进行校验，之后交给业务层代码进行操作，在 `saveCategory()` 方法中，首先会根据名称查询是否已经存在该分类，之后才会进行数据封装并进行数据库 insert 操作。



#### 删除分类接口

删除接口负责接收前端的分类删除请求，处理前端传输过来的数据后，将这些记录从数据库中删除，这里的“删除”功能并不是真正意义上的删除，而是逻辑删除，将接受的参数设置为一个数组，可以同时删除多条记录，只需要在前端将用户选择的记录 id 封装好再传参到后端即可。

##### 控制层代码

```java
    /**
     * 分类删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/categories/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (categoryService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }
```

##### 业务层代码

```java
    /**
     * 根据id删除分类数据
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除分类数据
        return blogCategoryDao.deleteBatch(ids) > 0;
    }
```

接口的请求路径为 /categories/delete，并使用 `@RequestBody` 将前端传过来的参数封装为 id 数组，参数验证通过后则调用 `deleteBatch()` 批量删除方法进行数据库操作，否则将向前端返回错误信息。



#### 其它

还有根据 id 获取详情的接口，路径为 categories/info/{id}，请求方法为 GET；分类修改接口，路径为 categories/update，请求方法为 POST。

##### 控制层代码

```java
    /**
     * 分类修改
     * @param categoryId
     * @param categoryName
     * @param categoryIcon
     * @return
     */
    @RequestMapping(value = "/categories/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestParam("categoryId") Integer categoryId,
                         @RequestParam("categoryName") String categoryName,
                         @RequestParam("categoryIcon") String categoryIcon) {
        if (categoryId == null || categoryId < 1) {
            return ResultGenerator.genFailResult("非法参数！");
        }
        if (StringUtils.isEmpty(categoryName)) {
            return ResultGenerator.genFailResult("请输入分类名称！");
        }
        if (StringUtils.isEmpty(categoryIcon)) {
            return ResultGenerator.genFailResult("请选择分类图标！");
        }
        if (categoryService.updateCategory(categoryId, categoryName, categoryIcon)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("分类名称重复");
        }
    }

    /**
     * 详情
     * @param id
     * @return
     */
    @GetMapping("/categories/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Integer id) {
        if (id == null || id < 1) {
            return ResultGenerator.genFailResult("非法参数！");
        }
        BlogCategory category = categoryService.selectById(id);
        return ResultGenerator.genSuccessResult(category);
    }
```

##### 业务层代码

```java
    /**
     * 根据id，给定类名和类图标更新类别信息
     * @param categoryId
     * @param categoryName
     * @param categoryIcon
     * @return
     */
    @Override
    @Transactional
    public Boolean updateCategory(Integer categoryId, String categoryName, String categoryIcon) {
        BlogCategory blogCategory = blogCategoryDao.selectByPrimaryKey(categoryId);
        if (blogCategory != null) {
            blogCategory.setCategoryIcon(categoryIcon);
            blogCategory.setCategoryName(categoryName);
            return blogCategoryDao.updateByPrimaryKeySelective(blogCategory) > 0;
        }
        return false;
    }

    @Override
    public BlogCategory selectById(Integer id) {
        return blogCategoryDao.selectByPrimaryKey(id);
    }
```



### 前端页面实现

#### category.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<header th:replace="admin/header::header-fragment">
</header>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <!-- 引入页面头header-fragment -->
    <div th:replace="admin/header::header-nav"></div>
    <!-- 引入工具栏sidebar-fragment -->
    <div th:replace="admin/sidebar::sidebar-fragment(${path})"></div>
    <!-- Content Wrapper. Contains 图标content -->
    <div class="content-wrapper">
        <!-- Content Header (图标header) -->
        <div class="content-header">
            <div class="container-fluid">
            </div><!-- /.container-fluid -->
        </div>
        <!-- Main content -->
        <div class="content">
            <div class="container-fluid">
                <div class="card card-primary card-outline">
                    <div class="card-header">
                        <h3 class="card-title">分类管理</h3>
                    </div> <!-- /.card-body -->
                    <div class="card-body">
                        <div class="grid-btn">
                            <button class="btn btn-info" onclick="categoryAdd()"><i
                                    class="fa fa-plus"></i>&nbsp;新增
                            </button>
                            <button class="btn btn-info" onclick="categoryEdit()"><i
                                    class="fa fa-pencil-square-o"></i>&nbsp;修改
                            </button>
                            <button class="btn btn-danger" onclick="deleteCagegory()"><i
                                    class="fa fa-trash-o"></i>&nbsp;删除
                            </button>
                        </div>
                        <br>
                        <table id="jqGrid" class="table table-bordered">
                        </table>
                        <div id="jqGridPager"></div>
                    </div><!-- /.card-body -->
                </div>
            </div><!-- /.container-fluid -->
        </div>
        <!-- /.content -->
        <div class="content">
            <!-- 模态框（Modal） -->
            <div class="modal fade" id="categoryModal" tabindex="-1" role="dialog" aria-labelledby="categoryModalLabel">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                    aria-hidden="true">&times;</span></button>
                            <h6 class="modal-title" id="categoryModalLabel">Modal</h6>
                        </div>
                        <div class="modal-body">
                            <form id="categoryForm" onsubmit="return false;">
                                <div class="form-group">
                                    <div class="alert alert-danger" id="edit-error-msg" style="display: none;">
                                        错误信息展示栏。
                                    </div>
                                </div>
                                <input type="hidden" class="form-control" id="categoryId" name="categoryId">
                                <div class="form-group">
                                    <label for="categoryName" class="control-label">分类名称:</label>
                                    <input type="text" class="form-control" id="categoryName" name="categoryName"
                                           placeholder="请输入分类名称" required="true">
                                </div>
                                <div class="form-group">
                                    <label for="categoryIcon" class="control-label">分类图标:</label>
                                    <input type="hidden" class="form-control" id="categoryIcon" name="categoryIcon">
                                    <div class="col-sm-4">
                                        <img id="categoryIconImg" src="/admin/dist/img/img-upload.png"
                                             style="height: 64px;width: 64px;">
                                        <button class="btn btn-secondary" style="margin-top: 5px;margin-bottom: 5px;"
                                                id="categoryIconButton"><i
                                                class="fa fa-random"></i>&nbsp;图标切换
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            <button type="button" class="btn btn-primary" id="saveButton">确认</button>
                        </div>
                    </div>
                </div>
            </div>
            <!-- /.modal -->
        </div>
    </div>
    <!-- /.content-wrapper -->
    <!-- 引入页脚footer-fragment -->
    <div th:replace="admin/footer::footer-fragment"></div>
</div>
<!-- jQuery -->
<script th:src="@{/admin/plugins/jquery/jquery.min.js}"></script>
<!-- jQuery UI 1.11.4 -->
<script th:src="@{/admin/plugins/jQueryUI/jquery-ui.min.js}"></script>
<!-- Bootstrap 4 -->
<script th:src="@{/admin/plugins/bootstrap/js/bootstrap.bundle.min.js}"></script>
<!-- AdminLTE App -->
<script th:src="@{/admin/dist/js/adminlte.min.js}"></script>
<!-- jqgrid -->
<script th:src="@{/admin/plugins/jqgrid-5.3.0/jquery.jqGrid.min.js}"></script>
<script th:src="@{/admin/plugins/jqgrid-5.3.0/grid.locale-cn.js}"></script>
<!-- sweetalert -->
<script th:src="@{/admin/plugins/sweetalert/sweetalert.min.js}"></script>
<script th:src="@{/admin/dist/js/public.js}"></script>
<script th:src="@{/admin/dist/js/category.js}"></script>
</body>
</html>
```

#### 功能按钮

分类管理模块也设计了常用的几个功能：分类信息增加、分类信息编辑、分类信息删除，因此在页面中添加对应的功能按钮以及触发事件，代码如下：

```html
<div class="grid-btn">
  <button class="btn btn-info" onclick="categoryAdd()">
    <i class="fa fa-plus"></i>&nbsp;新增
  </button>
  <button class="btn btn-info" onclick="categoryEdit()">
    <i class="fa fa-pencil-square-o"></i>&nbsp;修改
  </button>
  <button class="btn btn-danger" onclick="deleteCagegory()">
    <i class="fa fa-trash-o"></i>&nbsp;删除
  </button>
</div>
```

分别是添加按钮，对应的触发事件是 `categoryAdd()` 方法，修改按钮，对应的触发事件是 `categoryEdit()` 方法，删除按钮，对应的触发事件是 `deleteCagegory()` 方法。

#### 分页信息展示区域

页面中已经引入 JqGrid 的相关静态资源文件，需要在页面中展示分页数据的区域增加如下代码：

```html
<table id="jqGrid" class="table table-bordered"></table>
<div id="jqGridPager"></div>
```

此时只是静态效果展示，并没有与后端进行数据交互，接下来将结合 Ajax 和后端接口实现具体的功能。



### 分页模块前端功能实现

#### 分页功能

在 resources/static/admin/dist/js 目录下新增 category.js 文件，并添加如下代码：

```javascript
$(function () {
  $('#jqGrid').jqGrid({
    url: '/admin/categories/list',
    datatype: 'json',
    colModel: [
      {
        label: 'id',
        name: 'categoryId',
        index: 'categoryId',
        width: 50,
        key: true,
        hidden: true,
      },
      {
        label: '分类名称',
        name: 'categoryName',
        index: 'categoryName',
        width: 240,
      },
      {
        label: '分类图标',
        name: 'categoryIcon',
        index: 'categoryIcon',
        width: 120,
        formatter: imgFormatter,
      },
      {
        label: '添加时间',
        name: 'createTime',
        index: 'createTime',
        width: 120,
      },
    ],
    height: 560,
    rowNum: 10,
    rowList: [10, 20, 50],
    styleUI: 'Bootstrap',
    loadtext: '信息读取中...',
    rownumbers: false,
    rownumWidth: 20,
    autowidth: true,
    multiselect: true,
    pager: '#jqGridPager',
    jsonReader: {
      root: 'data.list',
      page: 'data.currPage',
      total: 'data.totalPage',
      records: 'data.totalCount',
    },
    prmNames: {
      page: 'page',
      rows: 'limit',
      order: 'order',
    },
    gridComplete: function () {
      //隐藏grid底部滚动条
      $('#jqGrid').closest('.ui-jqgrid-bdiv').css({ 'overflow-x': 'hidden' });
    },
  });

  $(window).resize(function () {
    $('#jqGrid').setGridWidth($('.card-body').width());
  });
});
```

以上代码的主要功能为分页数据展示、字段格式化 jqGrid DOM 宽度的自适应，在页面加载时，调用 JqGrid 的初始化方法，将页面中 id 为 jqGrid 的 DOM 渲染为分页表格，并向后端发送请求，之后按照后端返回的 json 数据填充表格以及表格下方的分页按钮。

#### 按钮事件及 Modal 框实现

添加和修改两个按钮分别绑定了触发事件，需要在 category.js 文件中新增 `categoryAdd()` 方法和 `categoryEdit()` 方法，两个方法中的实现为打开信息编辑框，下面实现信息编辑框和两个触发事件，代码如下：

```html
<div class="content">
  <!-- 模态框（Modal） -->
  <div
    class="modal fade"
    id="categoryModal"
    tabindex="-1"
    role="dialog"
    aria-labelledby="categoryModalLabel"
  >
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button
            type="button"
            class="close"
            data-dismiss="modal"
            aria-label="Close"
          >
            <span aria-hidden="true">&times;</span>
          </button>
          <h6 class="modal-title" id="categoryModalLabel">Modal</h6>
        </div>
        <div class="modal-body">
          <form id="categoryForm" onsubmit="return false;">
            <div class="form-group">
              <div
                class="alert alert-danger"
                id="edit-error-msg"
                style="display: none;"
              >
                错误信息展示栏。
              </div>
            </div>
            <input
              type="hidden"
              class="form-control"
              id="categoryId"
              name="categoryId"
            />
            <div class="form-group">
              <label for="categoryName" class="control-label">分类名称:</label>
              <input
                type="text"
                class="form-control"
                id="categoryName"
                name="categoryName"
                placeholder="请输入分类名称"
                required="true"
              />
            </div>
            <div class="form-group">
              <label for="categoryIcon" class="control-label">分类图标:</label>
              <input
                type="hidden"
                class="form-control"
                id="categoryIcon"
                name="categoryIcon"
              />
              <div class="col-sm-4">
                <img
                  id="categoryIconImg"
                  src="/admin/dist/img/img-upload.png"
                  style="height: 64px;width: 64px;"
                />
                <button
                  class="btn btn-secondary"
                  style="margin-top: 5px;margin-bottom: 5px;"
                  id="categoryIconButton"
                >
                  <i class="fa fa-random"></i>&nbsp;图标切换
                </button>
              </div>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">
            取消
          </button>
          <button type="button" class="btn btn-primary" id="saveButton">
            确认
          </button>
        </div>
      </div>
    </div>
  </div>
</div>
```

`categoryAdd()` 方法和 `categoryEdit()` 方法实现如下：

```javascript
function categoryAdd() {
    reset();
    $('.modal-title').html('分类添加');
    $('#categoryModal').modal('show');
}

function categoryEdit() {
    reset();
    var id = getSelectedRow();
    if (id == null) {
        return;
    }
    $('.modal-title').html('分类编辑');
    $('#categoryModal').modal('show');
    //请求数据
    $.get("/admin/categories/info/" + id, function (r) {
        if (r.resultCode == 200 && r.data != null) {
            //填充数据至modal
            $("#categoryIconImg").attr("src", r.data.categoryIcon);
            $("#categoryIconImg").attr("style", "width:64px ;height: 64px;display:block;");
            $("#categoryIcon").val(r.data.categoryIcon);
            $("#categoryName").val(r.data.categoryName);
        }
    });
    $("#categoryId").val(id);
}
```

添加方法仅仅是将 Modal 框显示，修改功能则多了一个步骤，需要将选择的记录回显到编辑框中以供修改，因此需要请求 categories/info/{id} 详情接口获取被修改的分类数据信息。

#### 添加功能和编辑功能

在信息录入完成后可以点击信息编辑框下方的**确认**按钮，此时会进行数据的交互，js 实现代码如下：

```javascript
//绑定modal上的保存按钮
$('#saveButton').click(function () {
    var categoryName = $("#categoryName").val();
    if (!validCN_ENString2_18(categoryName)) {
        $('#edit-error-msg').css("display", "block");
        $('#edit-error-msg').html("请输入符合规范的分类名称！");
    } else {
        var params = $("#categoryForm").serialize();
        var url = '/admin/categories/save';
        var id = getSelectedRowWithoutAlert();
        if (id != null) {
            url = '/admin/categories/update';
        }
        $.ajax({
            type: 'POST',//方法类型
            url: url,
            data: params,
            success: function (result) {
                if (result.resultCode == 200) {
                    $('#categoryModal').modal('hide');
                    swal("保存成功", {
                        icon: "success",
                    });
                    reload();
                }
                else {
                    $('#categoryModal').modal('hide');
                    swal(result.message, {
                        icon: "error",
                    });
                }
                ;
            },
            error: function () {
                swal("操作失败", {
                    icon: "error",
                });
            }
        });
    }
});
```

由于传参和后续处理逻辑类似，为了避免太多重复代码因此将两个方法写在一起了，通过 id 是否大于 0 来确定是修改操作还是添加操作，方法步骤如下：

1. 前端对用户输入的数据进行简单的正则验证
2. 封装数据
3. 向对应的后端接口发送 Ajax 请求
4. 请求成功后提醒用户请求成功并隐藏当前的信息编辑框，同时刷新列表数据
5. 请求失败则提醒对应的错误信息

`getSelectedRowWithoutAlert()` 方法（在 `public.js` 中）：

```javascript
/**
 * 获取jqGrid选中的一条记录(不出现弹框)
 * @returns {*}
 */
function getSelectedRowWithoutAlert() {
    var grid = $("#jqGrid");
    var rowKey = grid.getGridParam("selrow");
    if (!rowKey) {
        return;
    }
    var selectedIDs = grid.getGridParam("selarrrow");
    if (selectedIDs.length > 1) {
        return;
    }
    return selectedIDs[0];
}
```

#### 删除功能

删除按钮的点击触发事件为 `deleteCagegory()`，在 category.js 文件中新增如下代码：

```javascript
function deleteCagegory() {
  var ids = getSelectedRows();
  if (ids == null) {
    return;
  }
  swal({
    title: '确认弹框',
    text: '确认要删除数据吗?',
    icon: 'warning',
    buttons: true,
    dangerMode: true,
  }).then((flag) => {
    if (flag) {
      $.ajax({
        type: 'POST',
        url: '/admin/categories/delete',
        contentType: 'application/json',
        data: JSON.stringify(ids),
        success: function (r) {
          if (r.resultCode == 200) {
            swal('删除成功', {
              icon: 'success',
            });
            $('#jqGrid').trigger('reloadGrid');
          } else {
            swal(r.message, {
              icon: 'error',
            });
          }
        },
      });
    }
  });
}
```

`getSelectedRows()` 方法（`public.js`）：

```javascript
/**
 * 获取jqGrid选中的多条记录
 * @returns {*}
 */
function getSelectedRows() {
    var grid = $("#jqGrid");
    var rowKey = grid.getGridParam("selrow");
    if (!rowKey) {
        swal("请选择一条记录", {
            icon: "warning",
        });
        return;
    }
    return grid.getGridParam("selarrrow");
}
```

获取用户在 jqgrid 表格中选择的需要删除的所有记录的 id，之后将参数封装并向后端发送 Ajax 请求，请求地址为 categories/delete。



## 侧边栏抽取

sidebar.html 这个模板文件是抽取出来的左侧导航栏文件，由于每个页面都需要加上侧边导航栏的代码，为了精简代码就将这部分代码提取出来作为公共代码，代码简化的同时，也方便维护和修改，代码如下：

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<aside th:fragment="sidebar-fragment(path)" class="main-sidebar sidebar-dark-primary elevation-4">
    <!-- Brand Logo -->
    <a th:href="@{/admin/index}" class="brand-link">
        <img th:src="@{/admin/dist/img/logo.png}" alt="ssm-cluster Logo" class="brand-image img-circle elevation-3"
             style="opacity: .8">
        <span class="brand-text font-weight-light">my blog</span>
    </a>
    <!-- Sidebar -->
    <div class="sidebar">
        <!-- Sidebar user panel (optional) -->
        <div class="user-panel mt-3 pb-3 mb-3 d-flex">
            <div class="image">
                <img th:src="@{/admin/dist/img/avatar5.png}" class="img-circle elevation-2" alt="User Image">
            </div>
            <div class="info">
                <a href="#" class="d-block" th:text="${session.loginUser}"></a>
            </div>
        </div>
        <!-- Sidebar Menu -->
        <nav class="mt-2">
            <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu"
                data-accordion="false">
                <!-- Add icons to the links using the .nav-icon class
                     with font-awesome or any other icon font library -->
                <li class="nav-header">Dashboard</li>
                <li class="nav-item">
                    <a th:href="@{/admin/index}" th:class="${path}=='index'?'nav-link active':'nav-link'">
                        <i class="nav-icon fa fa-dashboard"></i>
                        <p>
                            Dashboard
                        </p>
                    </a>
                </li>
                <li class="nav-header">管理模块</li>
                <li class="nav-item">
                    <a th:href="@{/admin/categories}" th:class="${path}=='categories'?'nav-link active':'nav-link'">
                        <i class="fa fa-bookmark nav-icon" aria-hidden="true"></i>
                        <p>
                            分类管理
                        </p>
                    </a>
                </li>
                <li class="nav-item">
                    <a th:href="@{/admin/tags}" th:class="${path}=='tags'?'nav-link active':'nav-link'">
                        <i class="fa fa-tags nav-icon" aria-hidden="true"></i>
                        <p>
                            标签管理
                        </p>
                    </a>
                </li>
                <li class="nav-header">系统管理</li>
                <li class="nav-item">
                    <a th:href="@{/admin/profile}"
                       th:class="${path}=='profile'?'nav-link active':'nav-link'">
                        <i class="fa fa-user-secret nav-icon"></i>
                        <p>修改密码</p>
                    </a>
                </li>
                <li class="nav-item">
                    <a th:href="@{/admin/logout}" class="nav-link">
                        <i class="fa fa-sign-out nav-icon"></i>
                        <p>安全退出</p>
                    </a>
                </li>
                </li>
            </ul>
        </nav>
        <!-- /.sidebar-menu -->
    </div>
    <!-- /.sidebar -->
</aside>
</html>
```

接下来解释一下具体的实现逻辑，首先，以上这部分代码如果不进行抽取的话，在添加其他模块的时候需要在每一个模块的页面代码中添加一遍，但是基本上所有的代码都是重复的，只有一处不同，那就是导航栏中当前模块的选中状态，比如在分类管理页面中，左侧导航栏中的“分类管理”即为选中状态，其他页面与此相同。

这里的实现方式是通过添加一个 path 变量来控制当前导航栏中的选中状态，在模板文件中的 Thymeleaf 判断语句中通过 path 字段来确定是哪个功能模块，并对应的将左侧导航栏上当前模块的 css 样式给修改掉，判断语句如下：

```html
th:class="${path}=='categories'?'nav-link active':'nav-link'"
```

如果当前的 path 字段值为 'categories'，那么“分类管理”这个选项的 css 样式就修改为选中状态，如果当前的 path 字段值为 'tags'，那么“标签管理”这个选项的 css 样式就修改为选中状态，其他模块依次类推，path 字段的值是在哪里进行赋值的呢？答案是 admin 包下的 Controller 类中，在进行页面跳转时，会分别将对应的 path 字段进行赋值，代码如下：

- TagController：

```java
    @GetMapping("/tags")
    public String tagPage(HttpServletRequest request) {
        request.setAttribute("path", "tags");
        return "admin/tag";
    }
```

- CategoryController：

```java
    @GetMapping("/categories")
    public String categoryPage(HttpServletRequest request) {
        request.setAttribute("path", "categories");
        return "admin/category";
    }
```

通过这种方式，以后如果需要在系统中新增一个模块，就可以对应的增加一个导航栏按钮在 sidebar.html 文件中，并在后端的控制器方法中赋值对应的 path 字段即可，比如博客管理、配置管理等之后的功能模块。



## 标签管理模块

### 标签模块简介

标签是一种更为灵活、更有趣的分类方式，在书写博客时可以为每篇文章添加一个或多个标签，在博客系统中，文章的标签设计被广泛应用，我们可以看到大部分的博客网站中都会有标签设计，因此，在设计 personal-blog 这个项目时，也将标签运用了进来。

标签最明显的作用有如下两点：

1. 传统意义上分类的作用，类似分类名称

2. 对文章内容进行一定程度的描述，类似于关键词

虽然与分类设计类似，但是标签和分类还有一些细区别：

- 同一篇文章标签可以用多个，但通常只能属于一个分类
- 标签一般是在写作完成后，根据文章内容自行添加的内容
- 标签可以把文章中重点词语提炼出来，有关键词的意义，但是分类没有
- 标签通常更为主观，其内容相较于分类来说更加具体一些

与分类的功能和设计思想类似，但是又有一定的不同，标签可以算是分类的细化版本，同时，一篇博客的分类最好只有一个，但是在设计的时候，一篇博客的标签是可以有多个的，标签设计的介绍就到这里，接下来是功能开发的讲解。



### 持久层相关

#### 标签与文章的关系

一篇文章可以有多个标签字段，一个标签字段也可以被标注在多个文章中，这个情况与分类设计是有一些差别的，标签实体与文章实体的关系是多对多的关系，因此在表结构设计时不仅仅需要标签实体和文章实体的字段映射，还需要存储二者之间的关系数据，本系统采用的方式是新增一张关系表来维护二者多对多的关联关系。

#### 表结构设计

标签表以及标签文章关系表的 SQL 设计如下：

```sql
USE `my_blog_db`;

DROP TABLE IF EXISTS `tb_blog_tag`;

CREATE TABLE `tb_blog_tag` (
  `tag_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '标签表主键id',
  `tag_name` varchar(100) NOT NULL COMMENT '标签名称',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0=否 1=是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tb_blog_tag_relation`;

CREATE TABLE `tb_blog_tag_relation` (
  `relation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关系表id',
  `blog_id` bigint(20) NOT NULL COMMENT '博客id',
  `tag_id` int(11) NOT NULL COMMENT '标签id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`relation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

在关系表中有一个 blog_id 字段，是文章表的主键 id，这张表存储的就是标签记录对应的文章记录，以多对多的方式进行记录的，把表结构导入到数据库中即可；另外标签表以及关系表的大部分的实现逻辑是在后续管理模块中进行调用和实现的。

#### BlogTag 实体类

```java
package cn.yuyingwai.springbootblog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BlogTag {

    private Integer tagId;

    private String tagName;

    private Byte isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", tagId=").append(tagId);
        sb.append(", tagName=").append(tagName);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }

}
```

#### BlogTagRelation 实体类

```java
package cn.yuyingwai.springbootblog.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BlogTagRelation {

    private Long relationId;

    private Long blogId;

    private Integer tagId;

    private Date createTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", relationId=").append(relationId);
        sb.append(", blogId=").append(blogId);
        sb.append(", tagId=").append(tagId);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }

}
```

#### BlogTagDao.java

```java
package cn.yuyingwai.springbootblog.dao;

import cn.yuyingwai.springbootblog.entity.BlogTag;
import cn.yuyingwai.springbootblog.util.PageQueryUtil;

import java.util.List;

public interface BlogTagDao {

    int deleteByPrimaryKey(Integer tagId);

    int insert(BlogTag record);

    int insertSelective(BlogTag record);

    BlogTag selectByPrimaryKey(Integer tagId);

    BlogTag selectByTagName(String tagName);

    int updateByPrimaryKeySelective(BlogTag record);

    int updateByPrimaryKey(BlogTag record);

    List<BlogTag> findTagList(PageQueryUtil pageUtil);

    int getTotalTags(PageQueryUtil pageUtil);

    int deleteBatch(Integer[] ids);

}
```

#### BlogTagMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.yuyingwai.springbootblog.dao.BlogTagDao">
    <resultMap id="BaseResultMap" type="cn.yuyingwai.springbootblog.entity.BlogTag">
        <id column="tag_id" jdbcType="INTEGER" property="tagId"/>
        <result column="tag_name" jdbcType="VARCHAR" property="tagName"/>
        <result column="is_deleted" jdbcType="TINYINT" property="isDeleted"/>
        <result column="create_time" jdbcType="TIMESTAMP" property="createTime"/>
    </resultMap>

    <sql id="Base_Column_List">
        tag_id, tag_name, is_deleted, create_time
    </sql>

    <select id="findTagList" parameterType="Map" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_blog_tag
        where is_deleted=0
        order by tag_id desc
        <if test="start!=null and limit!=null">
            limit #{start},#{limit}
        </if>
    </select>

    <select id="getTotalTags" parameterType="Map" resultType="int">
        select count(*)  from tb_blog_tag
        where is_deleted=0
    </select>

    <select id="selectByPrimaryKey" parameterType="java.lang.Integer" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_blog_tag
        where tag_id = #{tagId,jdbcType=INTEGER} AND is_deleted = 0
    </select>

    <select id="selectByTagName" parameterType="java.lang.String" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_blog_tag
        where tag_name = #{tagName,jdbcType=VARCHAR} AND is_deleted = 0
    </select>

    <update id="deleteByPrimaryKey" parameterType="java.lang.Integer">
        update tb_blog_tag set is_deleted = 1
        where tag_id = #{tagId,jdbcType=INTEGER}
    </update>

    <update id="deleteBatch">
        update tb_blog_tag
        set is_deleted=1 where tag_id in
        <foreach item="id" collection="array" open="(" separator="," close=")">
            #{id}
        </foreach>
    </update>

    <insert id="insert" parameterType="cn.yuyingwai.springbootblog.entity.BlogTag">
        insert into tb_blog_tag (tag_id, tag_name, is_deleted,
                                 create_time)
        values (#{tagId,jdbcType=INTEGER}, #{tagName,jdbcType=VARCHAR}, #{isDeleted,jdbcType=TINYINT},
                #{createTime,jdbcType=TIMESTAMP})
    </insert>
    <insert id="insertSelective" parameterType="cn.yuyingwai.springbootblog.entity.BlogTag">
        insert into tb_blog_tag
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="tagId != null">
                tag_id,
            </if>
            <if test="tagName != null">
                tag_name,
            </if>
            <if test="isDeleted != null">
                is_deleted,
            </if>
            <if test="createTime != null">
                create_time,
            </if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="tagId != null">
                #{tagId,jdbcType=INTEGER},
            </if>
            <if test="tagName != null">
                #{tagName,jdbcType=VARCHAR},
            </if>
            <if test="isDeleted != null">
                #{isDeleted,jdbcType=TINYINT},
            </if>
            <if test="createTime != null">
                #{createTime,jdbcType=TIMESTAMP},
            </if>
        </trim>
    </insert>
    <update id="updateByPrimaryKeySelective" parameterType="cn.yuyingwai.springbootblog.entity.BlogTag">
        update tb_blog_tag
        <set>
            <if test="tagName != null">
                tag_name = #{tagName,jdbcType=VARCHAR},
            </if>
            <if test="isDeleted != null">
                is_deleted = #{isDeleted,jdbcType=TINYINT},
            </if>
            <if test="createTime != null">
                create_time = #{createTime,jdbcType=TIMESTAMP},
            </if>
        </set>
        where tag_id = #{tagId,jdbcType=INTEGER}
    </update>
    <update id="updateByPrimaryKey" parameterType="cn.yuyingwai.springbootblog.entity.BlogTag">
        update tb_blog_tag
        set tag_name = #{tagName,jdbcType=VARCHAR},
            is_deleted = #{isDeleted,jdbcType=TINYINT},
            create_time = #{createTime,jdbcType=TIMESTAMP}
        where tag_id = #{tagId,jdbcType=INTEGER}
    </update>
</mapper>
```

#### BlogTagRelationDao.java

```java
package cn.yuyingwai.springbootblog.dao;

import cn.yuyingwai.springbootblog.entity.BlogTagRelation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogTagRelationDao {

    int deleteByPrimaryKey(Long relationId);

    int insert(BlogTagRelation record);

    int insertSelective(BlogTagRelation record);

    BlogTagRelation selectByPrimaryKey(Long relationId);

    List<Long> selectDistinctTagIds(Integer[] tagIds);

    int updateByPrimaryKeySelective(BlogTagRelation record);

    int updateByPrimaryKey(BlogTagRelation record);
    
}
```

#### BlogTagRelationMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.yuyingwai.springbootblog.dao.BlogTagRelationDao">
    <resultMap id="BaseResultMap" type="cn.yuyingwai.springbootblog.entity.BlogTagRelation">
        <id column="relation_id" jdbcType="BIGINT" property="relationId"/>
        <result column="blog_id" jdbcType="BIGINT" property="blogId"/>
        <result column="tag_id" jdbcType="INTEGER" property="tagId"/>
        <result column="create_time" jdbcType="TIMESTAMP" property="createTime"/>
    </resultMap>
    <sql id="Base_Column_List">
        relation_id, blog_id, tag_id, create_time
    </sql>
    <select id="selectByPrimaryKey" parameterType="java.lang.Long" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_blog_tag_relation
        where relation_id = #{relationId,jdbcType=BIGINT}
    </select>

    <select id="selectDistinctTagIds" resultType="java.lang.Long">
        select
        DISTINCT(tag_id)
        from tb_blog_tag_relation
        where tag_id in
        <foreach item="id" collection="array" open="(" separator="," close=")">
            #{id}
        </foreach>
    </select>

    <delete id="deleteByPrimaryKey" parameterType="java.lang.Long">
        delete from tb_blog_tag_relation
        where relation_id = #{relationId,jdbcType=BIGINT}
    </delete>

    <insert id="insert" parameterType="cn.yuyingwai.springbootblog.entity.BlogTagRelation">
        insert into tb_blog_tag_relation (relation_id, blog_id, tag_id,
                                          create_time)
        values (#{relationId,jdbcType=BIGINT}, #{blogId,jdbcType=BIGINT}, #{tagId,jdbcType=INTEGER},
                #{createTime,jdbcType=TIMESTAMP})
    </insert>
    <insert id="insertSelective" parameterType="cn.yuyingwai.springbootblog.entity.BlogTagRelation">
        insert into tb_blog_tag_relation
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="relationId != null">
                relation_id,
            </if>
            <if test="blogId != null">
                blog_id,
            </if>
            <if test="tagId != null">
                tag_id,
            </if>
            <if test="createTime != null">
                create_time,
            </if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="relationId != null">
                #{relationId,jdbcType=BIGINT},
            </if>
            <if test="blogId != null">
                #{blogId,jdbcType=BIGINT},
            </if>
            <if test="tagId != null">
                #{tagId,jdbcType=INTEGER},
            </if>
            <if test="createTime != null">
                #{createTime,jdbcType=TIMESTAMP},
            </if>
        </trim>
    </insert>

    <update id="updateByPrimaryKeySelective" parameterType="cn.yuyingwai.springbootblog.entity.BlogTagRelation">
        update tb_blog_tag_relation
        <set>
            <if test="blogId != null">
                blog_id = #{blogId,jdbcType=BIGINT},
            </if>
            <if test="tagId != null">
                tag_id = #{tagId,jdbcType=INTEGER},
            </if>
            <if test="createTime != null">
                create_time = #{createTime,jdbcType=TIMESTAMP},
            </if>
        </set>
        where relation_id = #{relationId,jdbcType=BIGINT}
    </update>
    <update id="updateByPrimaryKey" parameterType="cn.yuyingwai.springbootblog.entity.BlogTagRelation">
        update tb_blog_tag_relation
        set blog_id = #{blogId,jdbcType=BIGINT},
            tag_id = #{tagId,jdbcType=INTEGER},
            create_time = #{createTime,jdbcType=TIMESTAMP}
        where relation_id = #{relationId,jdbcType=BIGINT}
    </update>
</mapper>
```



### 标签模块接口设计及实现

#### 标签列表分页接口

列表接口负责接收前端传来的分页参数，如 page 、limit 等参数，之后将数据总数和对应页面的数据列表查询出来并封装为分页数据返回给前端。

##### 控制层

TagController.java：接口的映射地址为 /tags/list，请求方法为 GET，代码如下：

```java
package cn.yuyingwai.springbootblog.controller.admin;

import cn.yuyingwai.springbootblog.service.TagService;
import cn.yuyingwai.springbootblog.util.PageQueryUtil;
import cn.yuyingwai.springbootblog.util.Result;
import cn.yuyingwai.springbootblog.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class TagController {

    @Resource
    private TagService tagService;

    @GetMapping("/tags")
    public String tagPage(HttpServletRequest request) {
        request.setAttribute("path", "tags");
        return "admin/tag";
    }

    @GetMapping("/tags/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(tagService.getBlogTagPage(pageUtil));
    }

}
```

##### 业务层

TagServiceImpl.java：

```java
package cn.yuyingwai.springbootblog.service.impl;

import cn.yuyingwai.springbootblog.dao.BlogTagDao;
import cn.yuyingwai.springbootblog.dao.BlogTagRelationDao;
import cn.yuyingwai.springbootblog.entity.BlogTag;
import cn.yuyingwai.springbootblog.service.TagService;
import cn.yuyingwai.springbootblog.util.PageQueryUtil;
import cn.yuyingwai.springbootblog.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private BlogTagDao blogTagDao;

    @Autowired
    private BlogTagRelationDao relationDao;

    /**
     * 查询标签的分页数据
     * @param pageUtil
     * @return
     */
    @Override
    public PageResult getBlogTagPage(PageQueryUtil pageUtil) {
        List<BlogTag> tags = blogTagDao.findTagList(pageUtil);
        int total = blogTagDao.getTotalTags(pageUtil);
        PageResult pageResult = new PageResult(tags, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
```



#### 添加标签接口

添加接口负责接收前端的 POST 请求并处理其中的参数，接收的参数为 tagName 字段，tagName 为标签名称。

##### 控制层

```java
    @PostMapping("/tags/save")
    @ResponseBody
    public Result save(@RequestParam("tagName") String tagName) {
        if (StringUtils.isEmpty(tagName)) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (tagService.saveTag(tagName)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("标签名称重复");
        }
    }
```

##### 业务层

```java
    /**
     * 添加标签
     * @param tagName
     * @return
     */
    @Override
    public Boolean saveTag(String tagName) {
        BlogTag temp = blogTagDao.selectByTagName(tagName);
        if (temp == null) {
            BlogTag blogTag = new BlogTag();
            blogTag.setTagName(tagName);
            return blogTagDao.insertSelective(blogTag) > 0;
        }
        return false;
    }
```

#### 删除标签接口

删除接口负责接收前端的标签删除请求，处理前端传输过来的数据后，将这些记录从数据库中删除，这里的“删除”功能并不是真正意义上的删除，而是逻辑删除，我们将接受的参数设置为一个数组，可以同时删除多条记录，只需要在前端将用户选择的记录 id 封装好再传参到后端即可。

接口的请求路径为 /tags/delete，并使用 @RequestBody 将前端传过来的参数封装为 id 数组，参数验证通过后则调用 deleteBatch() 批量删除方法进行数据库操作，否则将向前端返回错误信息。

##### 控制层

```java
    @PostMapping("/tags/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (tagService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("有关联数据请勿强行删除");
        }
    }
```

##### 业务层

```java
    /**
     * 删除没有关联关系的标签
     * @param ids
     * @return
     */
    @Override
    public Boolean deleteBatch(Integer[] ids) {
        // 已存在关联关系不删除
        List<Long> relations = relationDao.selectDistinctTagIds(ids);
        if (!CollectionUtils.isEmpty(relations)) {
            return false;
        }
        // 删除tag
        return blogTagDao.deleteBatch(ids) > 0;
    }
```

在业务方法实现中，需要判断该标签是否已经与文章表中的数据进行了关联，如果已经存在关联关系，就不进行删除操作，这是其中的一种处理方式，因为在添加文章数据时，也会对应的向数据库中新增标签数据和关系数据，因此在数据删除时需要进行确认以免造成数据混乱，当然也可以使用另外一种处理方法，就是在删除标签记录时，将标签记录以及对应的关系表中所有与此标签有关联的记录删除掉，这样也是可以的，本系统选择的是第一种方式。



### 前端页面实现

#### tag.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<header th:replace="admin/header::header-fragment"></header>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <!-- 引入页面头header-fragment -->
    <div th:replace="admin/header::header-nav"></div>
    <!-- 引入工具栏sidebar-fragment -->
    <div th:replace="admin/sidebar::sidebar-fragment(${path})"></div>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <div class="content-header">
            <div class="container-fluid">
            </div><!-- /.container-fluid -->
        </div>
        <!-- Main content -->
        <div class="content">
            <div class="container-fluid">
                <div class="card card-primary card-outline">
                    <div class="card-header">
                        <h3 class="card-title">标签管理</h3>
                    </div> <!-- /.card-body -->
                    <div class="card-body">
                        <div class="grid-btn">
                            <input type="text" class="form-control col-1" id="tagName" name="tagName"
                                   placeholder="标签名称" required="true">&nbsp;&nbsp;&nbsp;
                            <button class="btn btn-info" onclick="tagAdd()"><i
                                    class="fa fa-plus"></i>&nbsp;新增
                            </button>
                            <button class="btn btn-danger" onclick="deleteTag()"><i
                                    class="fa fa-trash-o"></i>&nbsp;删除
                            </button>
                        </div>
                        <table id="jqGrid" class="table table-bordered">
                        </table>
                        <div id="jqGridPager"></div>
                    </div><!-- /.card-body -->
                </div>
            </div><!-- /.container-fluid -->
        </div>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <!-- 引入页脚footer-fragment -->
    <div th:replace="admin/footer::footer-fragment"></div>
</div>
<!-- jQuery -->
<script th:src="@{/admin/plugins/jquery/jquery.min.js}"></script>
<!-- jQuery UI 1.11.4 -->
<script th:src="@{/admin/plugins/jQueryUI/jquery-ui.min.js}"></script>
<!-- Bootstrap 4 -->
<script th:src="@{/admin/plugins/bootstrap/js/bootstrap.bundle.min.js}"></script>
<!-- AdminLTE App -->
<script th:src="@{/admin/dist/js/adminlte.min.js}"></script>
<!-- jqgrid -->
<script th:src="@{/admin/plugins/jqgrid-5.3.0/jquery.jqGrid.min.js}"></script>
<script th:src="@{/admin/plugins/jqgrid-5.3.0/grid.locale-cn.js}"></script>
<!-- sweetalert -->
<script th:src="@{/admin/plugins/sweetalert/sweetalert.min.js}"></script>
<script th:src="@{/admin/dist/js/public.js}"></script>
<script th:src="@{/admin/dist/js/tag.js}"></script>
</body>
</html>
```

##### 功能按钮及信息编辑框

在页面中添加对应的功能按钮以及触发事件，由于标签信息的字段并不多，因此在按钮区新增了一个标签名称的输入框，代码如下：

```html
<div class="grid-btn">
  <input
    type="text"
    class="form-control col-1"
    id="tagName"
    name="tagName"
    placeholder="标签名称"
    required="true"
  />&nbsp;&nbsp;&nbsp;
  <button class="btn btn-info" onclick="tagAdd()">
    <i class="fa fa-plus"></i>&nbsp;新增
  </button>
  <button class="btn btn-danger" onclick="deleteTag()">
    <i class="fa fa-trash-o"></i>&nbsp;删除
  </button>
</div>
```

##### 分页信息展示区域

页面中已经引入 JqGrid 的相关静态资源文件，需要在页面中展示分页数据的区域增加如下代码：

```html
<table id="jqGrid" class="table table-bordered"></table>
<div id="jqGridPager"></div>
```

#### sidebar.html

在菜单中添加以下代码：

```html
<li class="nav-item">
    <a th:href="@{/admin/tags}" th:class="${path}=='tags'?'nav-link active':'nav-link'">
        <i class="fa fa-tags nav-icon" aria-hidden="true"></i>
        <p>
            标签管理
        </p>
    </a>
</li>
```



### 前端功能实现

#### 分页功能

在 resources/static/admin/dist/js 目录下新增 tag.js 文件，并添加如下代码：

```javascript
$(function () {
  $('#jqGrid').jqGrid({
    url: '/admin/tags/list',
    datatype: 'json',
    colModel: [
      {
        label: 'id',
        name: 'tagId',
        index: 'tagId',
        width: 50,
        key: true,
        hidden: true,
      },
      { label: '标签名称', name: 'tagName', index: 'tagName', width: 240 },
      {
        label: '添加时间',
        name: 'createTime',
        index: 'createTime',
        width: 120,
      },
    ],
    height: 560,
    rowNum: 10,
    rowList: [10, 20, 50],
    styleUI: 'Bootstrap',
    loadtext: '信息读取中...',
    rownumbers: false,
    rownumWidth: 20,
    autowidth: true,
    multiselect: true,
    pager: '#jqGridPager',
    jsonReader: {
      root: 'data.list',
      page: 'data.currPage',
      total: 'data.totalPage',
      records: 'data.totalCount',
    },
    prmNames: {
      page: 'page',
      rows: 'limit',
      order: 'order',
    },
    gridComplete: function () {
      //隐藏grid底部滚动条
      $('#jqGrid').closest('.ui-jqgrid-bdiv').css({ 'overflow-x': 'hidden' });
    },
  });
  $(window).resize(function () {
    $('#jqGrid').setGridWidth($('.card-body').width());
  });
});

/**
 * jqGrid重新加载
 */
function reload() {
    var page = $("#jqGrid").jqGrid('getGridParam', 'page');
    $("#jqGrid").jqGrid('setGridParam', {
        page: page
    }).trigger("reloadGrid");
}
```

以上代码的主要功能为分页数据展示、字段格式化 jqGrid DOM 宽度的自适应，在页面加载时，调用 JqGrid 的初始化方法，将页面中 id 为 jqGrid 的 DOM 渲染为分页表格，并向后端发送请求，之后按照后端返回的 json 数据填充表格以及表格下方的分页按钮。

#### 添加功能

在标签名称输入框中输入完成后可以点击右侧的**添加**按钮，此时会触发 `tagAdd()` 方法进行数据的交互，js 实现代码如下：

```javascript
function tagAdd() {
  var tagName = $('#tagName').val();
  if (!validCN_ENString2_18(tagName)) {
    swal('标签名称不规范', {
      icon: 'error',
    });
  } else {
    var url = '/admin/tags/save?tagName=' + tagName;
    $.ajax({
      type: 'POST', //方法类型
      url: url,
      success: function (result) {
        if (result.resultCode == 200) {
          $('#tagName').val('');
          swal('保存成功', {
            icon: 'success',
          });
          reload();
        } else {
          $('#tagName').val('');
          swal(result.message, {
            icon: 'error',
          });
        }
      },
      error: function () {
        swal('操作失败', {
          icon: 'error',
        });
      },
    });
  }
}
```

按钮点击后会触发对应的 js 方法，在该方法中首先会对用户输入的数据进行简单的正则验证，之后会封装数据并向对应的后端接口发送 Ajax 请求添加标签数据，之后根据后端返回的结果进行提示。

#### 删除功能

删除按钮的点击触发事件为 `deleteTag()`，在 tag.js 文件中新增如下代码：

```js
function deleteTag() {
  var ids = getSelectedRows();
  if (ids == null) {
    return;
  }
  swal({
    title: '确认弹框',
    text: '确认要删除数据吗?',
    icon: 'warning',
    buttons: true,
    dangerMode: true,
  }).then((flag) => {
    if (flag) {
      $.ajax({
        type: 'POST',
        url: '/admin/tags/delete',
        contentType: 'application/json',
        data: JSON.stringify(ids),
        success: function (r) {
          if (r.resultCode == 200) {
            swal('删除成功', {
              icon: 'success',
            });
            $('#jqGrid').trigger('reloadGrid');
          } else {
            swal(r.message, {
              icon: 'error',
            });
          }
        },
      });
    }
  });
}
```

获取用户在 jqgrid 表格中选择的需要删除的所有记录的 id，之后将参数封装并向后端发送 Ajax 请求，请求地址为 tags/delete。



## 文章编辑模块

### 富文本编译器

在 form 表单中通常会用 input 标签和 textarea 标签，简单的如登录信息的获取可能使用 input 标签即可，字数多一些的会用 textarea 标签来获取用户输入的内容，而博客文章排版比较丰富，各种内容和元素都会出现，此时就出现了问题，需要复杂排版的图文混合的内容或者更多内容录入的时候，这两个标签显然就无法满足需求。

#### 什么是富文本编译器？

> 富文本编辑器，是一种可内嵌于浏览器，所见即所得的文本编辑器。 富文本编辑器不同于文本编辑器(如 textarea 标签、input 标签)，也可以叫做图文编辑器，在富文本编辑器里可以编辑非常丰富的内容，如文字、图片、表情、代码……应有尽有，满足你的大部分需求。 像一些新闻排版，基本是以图文排版为主，而淘宝京东这些电商的商品详情页，基本都是多张已经排版好的设计图拼接而来的，富文本编辑器可以很完美的支持者两种需求。

目前的富文本编辑器主要有 markdown 版本和非 markdown 版本的编辑器，一般企业开发中使用非 markdown 版本比较多，常见的有 UEditor 和 KindEditor 等，因为运营人员可能不太懂 markdown 语法，而博客文章的编辑通常是使用 markdown 编辑器（即 md 编辑器），因为这部分人员掌握 markdown 语法也很快，所以大部分博客网站都会默认使用 markdown 编辑器作为用户的文章编辑器。

#### 为什么要使用富文本编辑器

以下是使用富文本编辑器的原因，也是富文本编辑器的优点：

- 需求变更导致，业务方提出的编辑需求越来越复杂
- 编辑的内容变得越来越复杂、越来越丰富
- 比起编辑 html，富文本编辑器更灵活
- 富文本编辑器功能丰富，满足大部分需求



### 文章编辑页面制作

#### 导航栏

首先在左侧导航栏中新增编辑页的导航按钮，在 sidebar.html 文件中新增如下代码（管理模块上面）：

```html
<li class="nav-item">
  <a
    th:href="@{/admin/blogs/edit}"
    th:class="${path}=='edit'?'nav-link active':'nav-link'"
  >
    <i class="nav-icon fa fa fa-pencil-square-o"></i>
    <p>
      发布博客
    </p>
  </a>
</li>
```

点击后的跳转路径为 /admin/blogs/edit，之后新建 Controller 来处理该路径并跳转到对应的页面。

#### Controller 处理跳转

首先在 controller/admin 包下新建 BlogController.java，之后新增如下代码：

```java
package cn.yuyingwai.springbootblog.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class BlogController {

    @GetMapping("/blogs/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        return "admin/edit";
    }

}
```

该方法用于处理 /admin/blogs/edit 请求，并设置 path 字段，之后跳转到 admin 目录下的 edit.html 中。



#### edit.html 页面制作

接下来就是博客编辑页面的模板文件制作了，在 resources/templates/admin 目录下新建 edit.html，并引入对应的 js 文件和 css 样式文件，代码如下：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <header th:replace="admin/header::header-fragment"></header>
  <body class="hold-transition sidebar-mini">
    <div class="wrapper">
      <!-- 引入页面头header-fragment -->
      <div th:replace="admin/header::header-nav"></div>
      <!-- 引入工具栏sidebar-fragment -->
      <div th:replace="admin/sidebar::sidebar-fragment(${path})"></div>
      <!-- Content Wrapper. Contains page content -->
      <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <div class="content-header">
          <div class="container-fluid"></div>
          <!-- /.container-fluid -->
        </div>
        <!-- Main content -->
        <div class="content">
          <div class="container-fluid">
            <div class="card card-primary card-outline">
              <div class="card-header">
                <h3 class="card-title">发布文章</h3>
              </div>
              <div class="card-body">
                编辑页面
              </div>
            </div>
          </div>
          <!-- /.container-fluid -->
        </div>
      </div>
      <!-- /.content-wrapper -->
      <!-- 引入页脚footer-fragment -->
      <div th:replace="admin/footer::footer-fragment"></div>
    </div>
    <!-- jQuery -->
    <script th:src="@{/admin/plugins/jquery/jquery.min.js}"></script>
    <!-- jQuery UI 1.11.4 -->
    <script th:src="@{/admin/plugins/jQueryUI/jquery-ui.min.js}"></script>
    <!-- Bootstrap 4 -->
    <script
      th:src="@{/admin/plugins/bootstrap/js/bootstrap.bundle.min.js}"
    ></script>
  </body>
</html>
```



#### Editor.md 编辑器整合

##### 整合步骤

1. 下载 Editor.md 插件代码并放进项目的 plugins 目录

2. 在 html 代码中引入 Editor.md 相关文件

   代码如下，首先是 css 文件：

   ```html
   <link
     th:href="@{/admin/plugins/editormd/css/editormd.css}"
     rel="stylesheet"
   />
   ```

   之后是引入编辑器的 js 文件：

   ```html
   <!-- editor.md -->
   <script th:src="@{/admin/plugins/editormd/editormd.min.js}"></script>
   ```

3. 添加编辑框 DOM 元素

   ```html
   <div class="card-body">
     <form id="blogForm" onsubmit="return false;">
       <div class="form-group" id="blog-editormd">
         <textarea style="display:none;"></textarea>
       </div>
       <div class="form-group">
         <!-- 按钮 -->
         &nbsp;<button
           class="btn btn-info float-right"
           style="margin-left: 5px;"
           id="confirmButton"
         >
           保存文章
         </button>
       </div>
     </form>
   </div>
   ```

   我们会在这里初始化 Editor.md 编辑器，这里首先定义将要初始化时的 id 名称为 blog-editormd，之后调用 Editor.md 插件的方法在这里将编辑器生成出来。

4. 初始化 Editor.md 对象

   添加如下 js 代码：

   ```html
   <script type="text/javascript">
     var blogEditor;
     $(function () {
       blogEditor = editormd('blog-editormd', {
         width: '100%',
         height: 640,
         syncScrolling: 'single',
         path: '/admin/plugins/editormd/lib/',
         toolbarModes: 'full',
       });
     });
   </script>
   ```

   通过调用 `editormd()` 方法并传入前文中定义的 DOM id，之后再次重启项目就能够看到编辑器的效果了。

##### 获取文档内容

在输入完成后，我们需要将 Editor.md 编辑器中输入的文字内容取出来，并传给后端以进行逻辑处理，提供了 `getMarkdown()` 方法来获取其中的内容，添加如下代码：

```javascript
$('#confirmButton').bind('click', function () {
  console.log(blogEditor.getMarkdown());
  alert(blogEditor.getMarkdown());
});
```

这里是添加了“保存文章”按钮的点击事件，点击该按钮后，会将编辑器中的内容给打印或者 alert 出来。

##### Editor.md 编辑器图片上传功能完善

在整合之后，默认是不可以上传图片的，需要略作配置修改，在 Editor.md 编辑器初始化时新增如下配置项：

```js
    /**图片上传配置*/
    imageUpload: true,//开启图片上传
    imageFormats: ["jpg", "jpeg", "gif", "png", "bmp", "webp"], //图片上传格式
    imageUploadURL: "/admin/blogs/md/uploadfile",//图片上传的后端路径
    onload: function (obj) { //上传成功之后的回调
    }
```

配置项的相关参数及参数释义已经给出，之后需要在后台 Controller 代码中新增一个方法用于接收图片上传请求并返回图片路径给 Editor.md 编辑器。

在 BlogController.java 中新增如下代码用于文件上传：

```java
    /**
     * 接收图片上传请求并返回图片路径给 Editor.md 编辑器
     * @param request
     * @param response
     * @param file
     * @throws IOException
     * @throws URISyntaxException
     */
    @PostMapping("/blogs/md/uploadfile")
    public void uploadFileByEditormd(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam(name = "editormd-image-file", required = true) MultipartFile file) throws IOException, URISyntaxException {
        String FILE_UPLOAD_DIC = "D:\\upload\\";   // 上传文件的默认url前缀，根据部署设置自行修改
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 生成文件名称通用方法
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
        String newFileName = tempName.toString();
        // 创建文件
        File destFile = new File(FILE_UPLOAD_DIC + newFileName);
        String fileUrl = MyBlogUtils.getHost(new URI(request.getRequestURI() + "")) + "/upload/" + newFileName;
        File fileDirectory = new File(FILE_UPLOAD_DIC);
        try {
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdir()) {
                    throw new IOException("文件夹创建失败，路径为：" + fileDirectory);
                }
            }
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            request.setCharacterEncoding("utf-8");
            response.setHeader("Content-Type", "text/html");
            response.getWriter().write("{\"success\": 1, \"message\":\"success\",\"url\":\"" + fileUrl + "\"}");
        } catch (UnsupportedEncodingException e) {
            response.getWriter().write("{\"success\":0}");
        } catch (IOException e) {
            response.getWriter().write("{\"success\":0}");
        }
    }
```

**MyBlogUtils.java：**

```java
package cn.yuyingwai.springbootblog.util;

import java.net.URI;

public class MyBlogUtils {

    public static URI getHost(URI uri) {
        URI effectiveURI = null;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (Throwable var4) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

}
```

之后在 MyBlogWebMvcConfigurer.java 中新增拦截器，代码如下：

```java
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:D:\\upload\\");
    }
```



### 持久层相关

#### 表结构设计

这里以 CSDN 平台的文章编辑模块为例，来确定一下文章表的字段设计，编辑模块如下图所示：

![](http://images.yingwai.top/picgo/20201214211057.jpg)

通过上图可以得出以下字段：

- 文章标题
- 文章内容
- 文章标签
- 文章分类
- 发布状态

以上是字段是博客文章实体应该具有的基础字段，不管是哪个博客平台都会存在这些字段，本博客系统上在此基础上增加了几个字段：

- 文章封面图(为了页面美观)
- 阅读量(博客文章的基本字段)
- 是否允许评论(有评论模块，可以控制评论模块的开放和关闭)

文章表的 SQL 设计如下，直接执行如下 SQL 语句即可：

```sql
USE `my_blog_db`;

DROP TABLE IF EXISTS `tb_blog`;

CREATE TABLE `tb_blog` (
  `blog_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '博客表主键id',
  `blog_title` varchar(200) NOT NULL COMMENT '博客标题',
  `blog_sub_url` varchar(200) NOT NULL COMMENT '博客自定义路径url',
  `blog_cover_image` varchar(200) NOT NULL COMMENT '博客封面图',
  `blog_content` mediumtext NOT NULL COMMENT '博客内容',
  `blog_category_id` int(11) NOT NULL COMMENT '博客分类id',
  `blog_category_name` varchar(50) NOT NULL COMMENT '博客分类(冗余字段)',
  `blog_tags` varchar(200) NOT NULL COMMENT '博客标签',
  `blog_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0-草稿 1-发布',
  `blog_views` bigint(20) NOT NULL DEFAULT '0' COMMENT '阅读量',
  `enable_comment` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0-允许评论 1-不允许评论',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0=否 1=是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`blog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

在 tb_blog 表中，设计了一个 is_deleted 字段，用于逻辑删除的标志位，由于 is_deleted 的字段设计，对表中数据的删除都是软删除，因为是个人博客，这么做的目的主要也是为了防止误删。

#### Blog 实体类

```java
package cn.yuyingwai.springbootblog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Blog {

    private Long blogId;

    private String blogTitle;

    private String blogSubUrl;

    private String blogCoverImage;

    private Integer blogCategoryId;

    private String blogCategoryName;

    private String blogTags;

    private Byte blogStatus;

    private Long blogViews;

    private Byte enableComment;

    private Byte isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Date updateTime;

    private String blogContent;

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle == null ? null : blogTitle.trim();
    }

    public void setBlogSubUrl(String blogSubUrl) {
        this.blogSubUrl = blogSubUrl == null ? null : blogSubUrl.trim();
    }

    public void setBlogCoverImage(String blogCoverImage) {
        this.blogCoverImage = blogCoverImage == null ? null : blogCoverImage.trim();
    }

    public void setBlogCategoryName(String blogCategoryName) {
        this.blogCategoryName = blogCategoryName == null ? null : blogCategoryName.trim();
    }

    public void setBlogTags(String blogTags) {
        this.blogTags = blogTags == null ? null : blogTags.trim();
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent == null ? null : blogContent.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", blogId=").append(blogId);
        sb.append(", blogTitle=").append(blogTitle);
        sb.append(", blogSubUrl=").append(blogSubUrl);
        sb.append(", blogCoverImage=").append(blogCoverImage);
        sb.append(", blogCategoryId=").append(blogCategoryId);
        sb.append(", blogCategoryName=").append(blogCategoryName);
        sb.append(", blogTags=").append(blogTags);
        sb.append(", blogStatus=").append(blogStatus);
        sb.append(", blogViews=").append(blogViews);
        sb.append(", enableComment=").append(enableComment);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", blogContent=").append(blogContent);
        sb.append("]");
        return sb.toString();
    }

}
```

#### BlogDao.java

```java
package cn.yuyingwai.springbootblog.dao;

import cn.yuyingwai.springbootblog.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogDao {

    int deleteByPrimaryKey(Long blogId);

    int insert(Blog record);

    int insertSelective(Blog record);

    Blog selectByPrimaryKey(Long blogId);

    int updateByPrimaryKeySelective(Blog record);

    int updateByPrimaryKeyWithBLOBs(Blog record);

    int updateByPrimaryKey(Blog record);

}
```

#### BlogMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.yuyingwai.springbootblog.dao.BlogDao">
    <resultMap id="BaseResultMap" type="cn.yuyingwai.springbootblog.entity.Blog">
        <id column="blog_id" jdbcType="BIGINT" property="blogId"/>
        <result column="blog_title" jdbcType="VARCHAR" property="blogTitle"/>
        <result column="blog_sub_url" jdbcType="VARCHAR" property="blogSubUrl"/>
        <result column="blog_cover_image" jdbcType="VARCHAR" property="blogCoverImage"/>
        <result column="blog_category_id" jdbcType="INTEGER" property="blogCategoryId"/>
        <result column="blog_category_name" jdbcType="VARCHAR" property="blogCategoryName"/>
        <result column="blog_tags" jdbcType="VARCHAR" property="blogTags"/>
        <result column="blog_status" jdbcType="TINYINT" property="blogStatus"/>
        <result column="blog_views" jdbcType="BIGINT" property="blogViews"/>
        <result column="enable_comment" jdbcType="TINYINT" property="enableComment"/>
        <result column="is_deleted" jdbcType="TINYINT" property="isDeleted"/>
        <result column="create_time" jdbcType="TIMESTAMP" property="createTime"/>
        <result column="update_time" jdbcType="TIMESTAMP" property="updateTime"/>
    </resultMap>
    <resultMap extends="BaseResultMap" id="ResultMapWithBLOBs" type="cn.yuyingwai.springbootblog.entity.Blog">
        <result column="blog_content" jdbcType="LONGVARCHAR" property="blogContent"/>
    </resultMap>
    <sql id="Base_Column_List">
        blog_id, blog_title, blog_sub_url, blog_cover_image, blog_category_id, blog_category_name, 
    blog_tags, blog_status, blog_views, enable_comment, is_deleted, create_time, update_time
    </sql>
    <sql id="Blob_Column_List">
        blog_content
    </sql>
    <select id="selectByPrimaryKey" parameterType="java.lang.Long" resultMap="ResultMapWithBLOBs">
        select
        <include refid="Base_Column_List"/>
        ,
        <include refid="Blob_Column_List"/>
        from tb_blog
        where blog_id = #{blogId,jdbcType=BIGINT} and is_deleted = 0
    </select>
    <insert id="insert" parameterType="cn.yuyingwai.springbootblog.entity.Blog">
        insert into tb_blog (blog_id, blog_title, blog_sub_url,
                             blog_cover_image, blog_category_id, blog_category_name,
                             blog_tags, blog_status, blog_views,
                             enable_comment, is_deleted, create_time,
                             update_time, blog_content)
        values (#{blogId,jdbcType=BIGINT}, #{blogTitle,jdbcType=VARCHAR}, #{blogSubUrl,jdbcType=VARCHAR},
                #{blogCoverImage,jdbcType=VARCHAR}, #{blogCategoryId,jdbcType=INTEGER}, #{blogCategoryName,jdbcType=VARCHAR},
                #{blogTags,jdbcType=VARCHAR}, #{blogStatus,jdbcType=TINYINT}, #{blogViews,jdbcType=BIGINT},
                #{enableComment,jdbcType=TINYINT}, #{isDeleted,jdbcType=TINYINT}, #{createTime,jdbcType=TIMESTAMP},
                #{updateTime,jdbcType=TIMESTAMP}, #{blogContent,jdbcType=LONGVARCHAR})
    </insert>
    <insert id="insertSelective" useGeneratedKeys="true" keyProperty="blogId"
            parameterType="cn.yuyingwai.springbootblog.entity.Blog">
        insert into tb_blog
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="blogId != null">
                blog_id,
            </if>
            <if test="blogTitle != null">
                blog_title,
            </if>
            <if test="blogSubUrl != null">
                blog_sub_url,
            </if>
            <if test="blogCoverImage != null">
                blog_cover_image,
            </if>
            <if test="blogCategoryId != null">
                blog_category_id,
            </if>
            <if test="blogCategoryName != null">
                blog_category_name,
            </if>
            <if test="blogTags != null">
                blog_tags,
            </if>
            <if test="blogStatus != null">
                blog_status,
            </if>
            <if test="blogViews != null">
                blog_views,
            </if>
            <if test="enableComment != null">
                enable_comment,
            </if>
            <if test="isDeleted != null">
                is_deleted,
            </if>
            <if test="createTime != null">
                create_time,
            </if>
            <if test="updateTime != null">
                update_time,
            </if>
            <if test="blogContent != null">
                blog_content,
            </if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="blogId != null">
                #{blogId,jdbcType=BIGINT},
            </if>
            <if test="blogTitle != null">
                #{blogTitle,jdbcType=VARCHAR},
            </if>
            <if test="blogSubUrl != null">
                #{blogSubUrl,jdbcType=VARCHAR},
            </if>
            <if test="blogCoverImage != null">
                #{blogCoverImage,jdbcType=VARCHAR},
            </if>
            <if test="blogCategoryId != null">
                #{blogCategoryId,jdbcType=INTEGER},
            </if>
            <if test="blogCategoryName != null">
                #{blogCategoryName,jdbcType=VARCHAR},
            </if>
            <if test="blogTags != null">
                #{blogTags,jdbcType=VARCHAR},
            </if>
            <if test="blogStatus != null">
                #{blogStatus,jdbcType=TINYINT},
            </if>
            <if test="blogViews != null">
                #{blogViews,jdbcType=BIGINT},
            </if>
            <if test="enableComment != null">
                #{enableComment,jdbcType=TINYINT},
            </if>
            <if test="isDeleted != null">
                #{isDeleted,jdbcType=TINYINT},
            </if>
            <if test="createTime != null">
                #{createTime,jdbcType=TIMESTAMP},
            </if>
            <if test="updateTime != null">
                #{updateTime,jdbcType=TIMESTAMP},
            </if>
            <if test="blogContent != null">
                #{blogContent,jdbcType=LONGVARCHAR},
            </if>
        </trim>
    </insert>
    <update id="updateByPrimaryKeySelective" parameterType="cn.yuyingwai.springbootblog.entity.Blog">
        update tb_blog
        <set>
            <if test="blogTitle != null">
                blog_title = #{blogTitle,jdbcType=VARCHAR},
            </if>
            <if test="blogSubUrl != null">
                blog_sub_url = #{blogSubUrl,jdbcType=VARCHAR},
            </if>
            <if test="blogCoverImage != null">
                blog_cover_image = #{blogCoverImage,jdbcType=VARCHAR},
            </if>
            <if test="blogContent != null">
                blog_content = #{blogContent,jdbcType=LONGVARCHAR},
            </if>
            <if test="blogCategoryId != null">
                blog_category_id = #{blogCategoryId,jdbcType=INTEGER},
            </if>
            <if test="blogCategoryName != null">
                blog_category_name = #{blogCategoryName,jdbcType=VARCHAR},
            </if>
            <if test="blogTags != null">
                blog_tags = #{blogTags,jdbcType=VARCHAR},
            </if>
            <if test="blogStatus != null">
                blog_status = #{blogStatus,jdbcType=TINYINT},
            </if>
            <if test="blogViews != null">
                blog_views = #{blogViews,jdbcType=BIGINT},
            </if>
            <if test="enableComment != null">
                enable_comment = #{enableComment,jdbcType=TINYINT},
            </if>
            <if test="isDeleted != null">
                is_deleted = #{isDeleted,jdbcType=TINYINT},
            </if>
            <if test="createTime != null">
                create_time = #{createTime,jdbcType=TIMESTAMP},
            </if>
            <if test="updateTime != null">
                update_time = #{updateTime,jdbcType=TIMESTAMP},
            </if>
            <if test="blogContent != null">
                blog_content = #{blogContent,jdbcType=LONGVARCHAR},
            </if>
        </set>
        where blog_id = #{blogId,jdbcType=BIGINT}
    </update>
    <update id="updateByPrimaryKeyWithBLOBs" parameterType="cn.yuyingwai.springbootblog.entity.Blog">
        update tb_blog
        set blog_title = #{blogTitle,jdbcType=VARCHAR},
            blog_sub_url = #{blogSubUrl,jdbcType=VARCHAR},
            blog_cover_image = #{blogCoverImage,jdbcType=VARCHAR},
            blog_category_id = #{blogCategoryId,jdbcType=INTEGER},
            blog_category_name = #{blogCategoryName,jdbcType=VARCHAR},
            blog_tags = #{blogTags,jdbcType=VARCHAR},
            blog_status = #{blogStatus,jdbcType=TINYINT},
            blog_views = #{blogViews,jdbcType=BIGINT},
            enable_comment = #{enableComment,jdbcType=TINYINT},
            is_deleted = #{isDeleted,jdbcType=TINYINT},
            create_time = #{createTime,jdbcType=TIMESTAMP},
            update_time = #{updateTime,jdbcType=TIMESTAMP},
            blog_content = #{blogContent,jdbcType=LONGVARCHAR}
        where blog_id = #{blogId,jdbcType=BIGINT}
    </update>
    <update id="updateByPrimaryKey" parameterType="cn.yuyingwai.springbootblog.entity.Blog">
        update tb_blog
        set blog_title = #{blogTitle,jdbcType=VARCHAR},
            blog_sub_url = #{blogSubUrl,jdbcType=VARCHAR},
            blog_cover_image = #{blogCoverImage,jdbcType=VARCHAR},
            blog_category_id = #{blogCategoryId,jdbcType=INTEGER},
            blog_category_name = #{blogCategoryName,jdbcType=VARCHAR},
            blog_tags = #{blogTags,jdbcType=VARCHAR},
            blog_status = #{blogStatus,jdbcType=TINYINT},
            blog_views = #{blogViews,jdbcType=BIGINT},
            enable_comment = #{enableComment,jdbcType=TINYINT},
            is_deleted = #{isDeleted,jdbcType=TINYINT},
            create_time = #{createTime,jdbcType=TIMESTAMP},
            update_time = #{updateTime,jdbcType=TIMESTAMP}
        where blog_id = #{blogId,jdbcType=BIGINT}
    </update>

    <update id="deleteByPrimaryKey" parameterType="java.lang.Long">
        UPDATE tb_blog SET is_deleted = 1
        where blog_id = #{blogId,jdbcType=BIGINT} and is_deleted = 0
    </update>
</mapper>
```

通过以上代码可以看出，在删除操作时并不是执行 delete 语句，而是将需要删除的文章记录的 is_deleted 字段修改为 1，这样就表示该文章已经被执行了删除操作，那么其他的 select 查询语句就需要在查询条件中添加 is_deleted = 0 将“被删除”的记录给过滤出去。



### 编辑页面完善

接下来，把编辑页面按照字段来完善一下，将其他需要输入内容的字段填充到页面 DOM 中，目前编辑页面只有一个编辑框来输入文章字段。某些字段只需要一个 input 框即可，比如文章标题字段，而其他一些字段的输入则需要一些前端插件来完成，比如标签、博客封面图，仅仅是 input 框肯定是无法满足需求的，比如标签字段和分类字段。

#### 引入相关依赖

编辑页面中有如下字段需要使用插件来完善交互：

- 标签字段
- 分类字段
- 文章内容字段(已实现)
- 封面图字段

以上字段所需要的插件也是使用的比较常用的开源插件，插件如下：

- tagsinput（标签）
- select2（分类）
- Editor.md（文章内容）
- ajaxupload（图片上传）

引入插件首先需要把这些依赖文件放到 resources/static/admin/plugins 目录下，目录结构如下：

![](http://images.yingwai.top/picgo/20201215162827.jpg)

之后在 edit.html 文件中引到页面中，代码如下：

* CSS 文件

```html
<link th:href="@{/admin/plugins/tagsinput/jquery.tagsinput.css}" rel="stylesheet"/>
<link th:href="@{/admin/plugins/select2/select2.css}" rel="stylesheet"/>
```

* JS 文件

```html
<!-- tagsinput -->
<script th:src="@{/admin/plugins/tagsinput/jquery.tagsinput.min.js}"></script>
<!-- Select2 -->
<script th:src="@{/admin/plugins/select2/select2.full.min.js}"></script>
<!-- sweetalert -->
<script th:src="@{/admin/plugins/sweetalert/sweetalert.min.js}"></script>
<!-- ajaxupload -->
<script th:src="@{/admin/plugins/ajaxupload/ajaxupload.js}"></script>
```

#### 编辑页面代码

根据字段新增对应的输入框以及 DOM 组件，代码如下：

```html
<div class="card-body">
  <!-- 几个基础的输入框，名称、分类等输入框 -->
  <form id="blogForm" onsubmit="return false;">
    <div class="form-group" style="display:flex;">
      <input
        type="text"
        class="form-control col-sm-6"
        id="blogName"
        name="blogName"
        placeholder="*请输入文章标题(必填)"
      />
      &nbsp;&nbsp;
      <input
        type="text"
        class="form-control"
        id="blogTags"
        name="blogTags"
        placeholder="请输入文章标签"
        style="width: 100%;"
      />
    </div>
    <div class="form-group" style="display:flex;">
      <input
        type="text"
        class="form-control col-sm-6"
        id="blogSubUrl"
        name="blogSubUrl"
        placeholder="请输入自定义路径,如:springboot-mybatis,默认为id"
      />
      &nbsp;&nbsp;
      <select
        class="form-control select2"
        style="width: 100%;"
        id="blogCategoryId"
        data-placeholder="请选择分类..."
      >
        <th:block th:if="${null == categories}">
          <option value="0" selected="selected">默认分类</option>
        </th:block>
        <th:block th:unless="${null == categories}">
          <th:block th:each="c : ${categories}">
            <option th:value="${c.categoryId}" th:text="${c.categoryName}">
            </option>
          </th:block>
        </th:block>
      </select>
    </div>
    <div class="form-group" id="blog-editormd">
      <textarea style="display:none;"></textarea>
    </div>
    <div class="form-group">
      <div class="col-sm-4">
        <img
          id="blogCoverImage"
          src="/admin/dist/img/img-upload.png"
          style="height: 64px;width: 64px;"
        />
      </div>
    </div>
    <br />
    <div class="form-group">
      <div class="col-sm-4">
        <button
          class="btn btn-info"
          style="margin-bottom: 5px;"
          id="uploadCoverImage"
        >
          <i class="fa fa-picture-o"></i>&nbsp;上传封面
        </button>
        <button
          class="btn btn-secondary"
          style="margin-bottom: 5px;"
          id="randomCoverImage"
        >
          <i class="fa fa-random"></i>&nbsp;随机封面
        </button>
      </div>
    </div>
    <div class="form-group">
      <label class="control-label">文章状态:&nbsp;</label>
      <input
        name="blogStatus"
        type="radio"
        id="publish"
        checked="true"
        value="1"
      />&nbsp;发布&nbsp;
      <input
        name="blogStatus"
        type="radio"
        id="draft"
        value="0"
      />&nbsp;草稿&nbsp;&nbsp;&nbsp;
      <label class="control-label">是否允许评论:&nbsp;</label>
      <input
        name="enableComment"
        type="radio"
        id="enableCommentTrue"
        checked="true"
        value="0"
      />&nbsp;是&nbsp;
      <input
        name="enableComment"
        type="radio"
        id="enableCommentFalse"
        value="1"
      />&nbsp;否&nbsp;
    </div>
    <div class="form-group">
      <!-- 按钮 -->
      &nbsp;<button
        class="btn btn-info float-right"
        style="margin-left: 5px;"
        id="confirmButton"
      >
        保存文章</button
      >&nbsp; &nbsp;<button
        class="btn btn-secondary float-right"
        style="margin-left: 5px;"
        id="cancelButton"
      >
        返回文章列表</button
      >&nbsp;
    </div>
  </form>
</div>
```

其中文章标题字段和自定义路径字段是直接使用的 input 框，标签字段会使用 tagsinput 插件，分类字段会使用 select2 下来选择框插件，博客封面图则是使用图片上传插件，文章内容的输入使用的是 Editor.md 编辑器，文章状态字段和评论开关字段使用的是 radio 选择框，最下面是两个功能按钮，文章保存和返回按钮。

#### 初始化插件

在 resources/static/admin/dist/js 目录下新增 edit.js 文件，把原来在 edit.html 文件中写的 Editor.md 初始化 js 代码也移到 edit.js 文件中，并添加如下代码：

```javascript
var blogEditor;
// Tags Input
$('#blogTags').tagsInput({
  width: '100%',
  height: '38px',
  defaultText: '文章标签',
});

//Initialize Select2 Elements
$('.select2').select2();

$(function () {
  blogEditor = editormd('blog-editormd', {
    width: '100%',
    height: 640,
    syncScrolling: 'single',
    path: '/admin/plugins/editormd/lib/',
    toolbarModes: 'full',
    /**图片上传配置*/
    imageUpload: true,
    imageFormats: ['jpg', 'jpeg', 'gif', 'png', 'bmp', 'webp'], //图片上传格式
    imageUploadURL: '/admin/blogs/md/uploadfile',
    onload: function (obj) {
      //上传成功之后的回调
    },
  });

  new AjaxUpload('#uploadCoverImage', {
    action: '/admin/upload/file',
    name: 'file',
    autoSubmit: true,
    responseType: 'json',
    onSubmit: function (file, extension) {
      if (
        !(extension && /^(jpg|jpeg|png|gif)$/.test(extension.toLowerCase()))
      ) {
        alert('只支持jpg、png、gif格式的文件！');
        return false;
      }
    },
    onComplete: function (file, r) {
      if (r != null && r.resultCode == 200) {
        $('#blogCoverImage').attr('src', r.data);
        $('#blogCoverImage').attr(
          'style',
          'width: 128px;height: 128px;display:block;'
        );
        return false;
      } else {
        alert('error');
      }
    },
  });
});

/**
 * 随机封面功能
 */
$('#randomCoverImage').click(function () {
  var rand = parseInt(Math.random() * 40 + 1);
  $('#blogCoverImage').attr('src', '/admin/dist/img/rand/' + rand + '.jpg');
  $('#blogCoverImage').attr(
    'style',
    'width:160px ;height: 120px;display:block;'
  );
});
```

以上代码中初始化了四个字段的页面 DOM 属性，分别是标签、分类、编辑框、图片上传框。



### 文章添加功能实现

#### 文章添加接口

添加接口负责接收前端的 POST 请求并处理其中的参数，接收的参数为用户在博客编辑页面输入的所有字段内容，字段名称与对应的含义如下：

1. "**blogTitle**": 文章标题
2. "**blogSubUrl**": 自定义路径
3. "**blogCategoryId**": 分类 id (下拉框中选择)
4. "**blogTags**": 标签字段(以逗号分隔)
5. "**blogContent**": 文章内容(编辑器中的 md 文档)
6. "**blogCoverImage**": 封面图(上传图片或者随机图片的路径)
7. "**blogStatus**": 文章状态
8. "**enableComment**": 评论开关

#### 控制层

在 BlogController 中新增 save() 方法，接口的映射地址为 /blogs/save，请求方法为 POST，代码如下：

```java
    /**
     * 验证博客信息并保存
     * @param blogTitle
     * @param blogSubUrl
     * @param blogCategoryId
     * @param blogTags
     * @param blogContent
     * @param blogCoverImage
     * @param blogStatus
     * @param enableComment
     * @return
     */
    @PostMapping("/blogs/save")
    @ResponseBody
    public Result save(@RequestParam("blogTitle") String blogTitle,
                       @RequestParam(name = "blogSubUrl", required = false) String blogSubUrl,
                       @RequestParam("blogCategoryId") Integer blogCategoryId,
                       @RequestParam("blogTags") String blogTags,
                       @RequestParam("blogContent") String blogContent,
                       @RequestParam("blogCoverImage") String blogCoverImage,
                       @RequestParam("blogStatus") Byte blogStatus,
                       @RequestParam("enableComment") Byte enableComment) {
        if (StringUtils.isEmpty(blogTitle)) {
            return ResultGenerator.genFailResult("请输入文章标题");
        }
        if (blogTitle.trim().length() > 150) {
            return ResultGenerator.genFailResult("标题过长");
        }
        if (StringUtils.isEmpty(blogTags)) {
            return ResultGenerator.genFailResult("请输入文章标签");
        }
        if (blogTags.trim().length() > 150) {
            return ResultGenerator.genFailResult("标签过长");
        }
        if (blogSubUrl.trim().length() > 150) {
            return ResultGenerator.genFailResult("路径过长");
        }
        if (StringUtils.isEmpty(blogContent)) {
            return ResultGenerator.genFailResult("请输入文章内容");
        }
        if (blogTags.trim().length() > 100000) {
            return ResultGenerator.genFailResult("文章内容过长");
        }
        if (StringUtils.isEmpty(blogCoverImage)) {
            return ResultGenerator.genFailResult("封面图不能为空");
        }
        Blog blog = new Blog();
        blog.setBlogTitle(blogTitle);
        blog.setBlogSubUrl(blogSubUrl);
        blog.setBlogCategoryId(blogCategoryId);
        blog.setBlogTags(blogTags);
        blog.setBlogContent(blogContent);
        blog.setBlogCoverImage(blogCoverImage);
        blog.setBlogStatus(blogStatus);
        blog.setEnableComment(enableComment);
        String saveBlogResult = blogService.saveBlog(blog);
        if ("success".equals(saveBlogResult)) {
            return ResultGenerator.genSuccessResult("添加成功");
        } else {
            return ResultGenerator.genFailResult(saveBlogResult);
        }
    }
```

添加接口中，首先会对参数进行校验，之后交给业务层代码进行操作。

#### 业务层

在 service 包中新建 BlogService 并定义接口方法 `saveBlog()`，下面为具体的实现方法代码：

```java
package cn.yuyingwai.springbootblog.service.impl;

import cn.yuyingwai.springbootblog.dao.BlogCategoryDao;
import cn.yuyingwai.springbootblog.dao.BlogDao;
import cn.yuyingwai.springbootblog.dao.BlogTagDao;
import cn.yuyingwai.springbootblog.dao.BlogTagRelationDao;
import cn.yuyingwai.springbootblog.entity.Blog;
import cn.yuyingwai.springbootblog.entity.BlogCategory;
import cn.yuyingwai.springbootblog.entity.BlogTag;
import cn.yuyingwai.springbootblog.entity.BlogTagRelation;
import cn.yuyingwai.springbootblog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogDao blogDao;
    @Autowired
    private BlogCategoryDao categoryDao;
    @Autowired
    private BlogTagDao tagDao;
    @Autowired
    private BlogTagRelationDao blogTagRelationDao;

    @Override
    @Transactional  // 开启事务
    public String saveBlog(Blog blog) {
        BlogCategory blogCategory = categoryDao.selectByPrimaryKey(blog.getBlogCategoryId());
        if (blogCategory == null) {
            blog.setBlogCategoryId(0);
            blog.setBlogCategoryName("默认分类");
        } else {
            // 设置博客分类名称
            blog.setBlogCategoryName(blogCategory.getCategoryName());
            // 分类的排序值加1
            blogCategory.setCategoryRank(blogCategory.getCategoryRank() + 1);
        }
        // 处理标签数据
        String[] tags = blog.getBlogTags().split(",");
        if (tags.length > 6) {
            return "标签数量限制为6";
        }
        // 保存文章
        if (blogDao.insertSelective(blog) > 0) {
            // 新增的tag对象
            List<BlogTag> tagListForInsert = new ArrayList<>();
            // 所有的tag对象，用于建立关系数据库
            List<BlogTag> allTagsList = new ArrayList<>();
            for (int i = 0; i < tags.length; i++) {
                BlogTag tag = tagDao.selectByTagName(tags[i]);
                if (tag == null) {
                    // 不存在就新增
                    BlogTag tempTag = new BlogTag();
                    tempTag.setTagName(tags[i]);
                    tagListForInsert.add(tempTag);
                } else {
                    allTagsList.add(tag);
                }
            }
            // 新增标签数据并修改分类排序值
            if (!CollectionUtils.isEmpty(tagListForInsert)) {
                tagDao.batchInsertBlogTag(tagListForInsert);
            }
            categoryDao.updateByPrimaryKeySelective(blogCategory);
            List<BlogTagRelation> blogTagRelations = new ArrayList<>();
            // 新增关系数据
            allTagsList.addAll(tagListForInsert);
            for (BlogTag tag: allTagsList) {
                BlogTagRelation blogTagRelation = new BlogTagRelation();
                blogTagRelation.setBlogId(blog.getBlogId());
                blogTagRelation.setTagId(tag.getTagId());
                blogTagRelations.add(blogTagRelation);
            }
            if (blogTagRelationDao.batchInsert(blogTagRelations) > 0) {
                return "success";
            }
        }
        return "保存失败";
    }

}
```

文章实体的新增与前文中的新增方法比较起来是略微复杂了一些，因为前面的都是单表操作，并不涉及关系表的操作，而文章表由于与分类表、标签表有关联关系，因此在新增文章内容时需要对其它表进行查询和修改操作，对于分类表只是查询和验证，对于标签表则需要查询和新增操作，因为标签是在文章编辑页面输入的，如果某些标签内容是标签表中没有的则需要新增，之后会操作文章标签关系表，将文章与标签关联起来并新增至关系表中，相关逻辑已经在以上代码中，关键注释也已经给出。

#### 关键 SQL

根据标签名称查询标签以及批量新增标签（BlogTagMapper.xml）：

```xml
    <select id="selectByTagName" parameterType="java.lang.String" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_blog_tag
        where tag_name = #{tagName,jdbcType=VARCHAR} AND is_deleted = 0
    </select>

   <insert id="batchInsertBlogTag" parameterType="java.util.List" useGeneratedKeys="true"
            keyProperty="tagId">
        INSERT into tb_blog_tag(tag_name)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.tagName,jdbcType=VARCHAR})
        </foreach>
    </insert>
```

批量新增文章标签关系数据（BlogTagRelationMapper.xml）：

```xml
    <insert id="batchInsert" parameterType="java.util.List">
        INSERT into tb_blog_tag_relation(blog_id,tag_id)
        VALUES
        <foreach collection="relationList" item="item" separator=",">
            (#{item.blogId,jdbcType=BIGINT},#{item.tagId,jdbcType=INTEGER})
        </foreach>
    </insert>
```

#### 对应的 Dao 类

BlogTagDao.java 添加：

```java
int batchInsertBlogTag(List<BlogTag> tagList);
```

BlogTagRelationDao 添加：

```java
int batchInsert(@Param("relationList") List<BlogTagRelation> blogTagRelationList);
```

#### Ajax 调用添加接口

在信息录入完成后可以点击信息编辑框下方的**保存文章**按钮，此时会调用后端接口并进行数据的交互，js 实现代码如下（edit.js）：

```javascript
$('#confirmButton').click(function () {
  var blogTitle = $('#blogName').val();
  var blogSubUrl = $('#blogSubUrl').val();
  var blogCategoryId = $('#blogCategoryId').val();
  var blogTags = $('#blogTags').val();
  var blogContent = blogEditor.getMarkdown();
  var blogCoverImage = $('#blogCoverImage')[0].src;
  var blogStatus = $("input[name='blogStatus']:checked").val();
  var enableComment = $("input[name='enableComment']:checked").val();
  if (isNull(blogTitle)) {
    swal('请输入文章标题', {
      icon: 'error',
    });
    return;
  }
  if (!validLength(blogTitle, 150)) {
    swal('标题过长', {
      icon: 'error',
    });
    return;
  }
  if (!validLength(blogSubUrl, 150)) {
    swal('路径过长', {
      icon: 'error',
    });
    return;
  }
  if (isNull(blogCategoryId)) {
    swal('请选择文章分类', {
      icon: 'error',
    });
    return;
  }
  if (isNull(blogTags)) {
    swal('请输入文章标签', {
      icon: 'error',
    });
    return;
  }
  if (!validLength(blogTags, 150)) {
    swal('标签过长', {
      icon: 'error',
    });
    return;
  }
  if (isNull(blogContent)) {
    swal('请输入文章内容', {
      icon: 'error',
    });
    return;
  }
  if (!validLength(blogTags, 100000)) {
    swal('文章内容过长', {
      icon: 'error',
    });
    return;
  }
  if (isNull(blogCoverImage) || blogCoverImage.indexOf('img-upload') != -1) {
    swal('封面图片不能为空', {
      icon: 'error',
    });
    return;
  }
  var url = '/admin/blogs/save';
  var data = {
    blogTitle: blogTitle,
    blogSubUrl: blogSubUrl,
    blogCategoryId: blogCategoryId,
    blogTags: blogTags,
    blogContent: blogContent,
    blogCoverImage: blogCoverImage,
    blogStatus: blogStatus,
    enableComment: enableComment,
  };
  console.log(data);
  $.ajax({
    type: 'POST', //方法类型
    url: url,
    data: data,
    success: function (result) {
      if (result.resultCode == 200) {
        swal('保存成功', {
          icon: 'success',
        });
      } else {
        swal(result.message, {
          icon: 'error',
        });
      }
    },
    error: function () {
      swal('操作失败', {
        icon: 'error',
      });
    },
  });
});
```

首先绑定 `#confirmButton` 的点击事件，点击后会获取所有的输入内容并进行验证，之后封装数据并向后端发送 Ajax 请求添加文章。



### 文章修改功能

想要修改一篇文章，首先需要获取这篇文章的所有属性，之后再回显到编辑页面中，用户根据需要来修改页面上的内容，点击保存按钮后会想后端发送文章修改请求，后端接口接收到请求后会进行参数验证以及相应的逻辑操作，之后进行数据的入库操作，整个文章修改流程完成。

#### 文章详情

根据流程，首先需要获取文章详情，但是文章编辑页面已经有了，所以就没有做成接口形式，而是采用与添加文章时相同的方式，将请求转发到编辑页即可，因为要获取文章详情所以需要根据一个字段来查询，这里就选择 id 作为传参了，在 BlogController 中新增如下代码：

```java
    @GetMapping("/blogs/edit/{blogId}")
    public String edit(HttpServletRequest request, @PathVariable("blogId") Long blogId) {
        request.setAttribute("path", "edit");
        Blog blog = blogService.getBlogById(blogId);
        if (blog == null) {
            return "error/error_400";
        }
        request.setAttribute("blog", blog);
        request.setAttribute("categories", categoryService.getAllCategories());
        return "admin/edit";
    }
```

在访问 /blogs/edit/{blogId} 时，会把文章编辑页所需的文章详情内容查询出来并转发到 edit 页面。

#### 页面回显

改造文章编辑页面 edit.html 的代码，通过 Thymeleaf 语法将前一个请求携带的 blog 对象进行读取并显示在编辑页面对应的 DOM 中，修改代码如下：

```html
<!-- 几个基础的输入框，名称、分类等输入框 -->
<form id="blogForm" onsubmit="return false;">
    <div class="form-group" style="display:flex;">
        <input type="hidden" id="blogId" name="blogId"
               th:value="${blog!=null and blog.blogId!=null }?${blog.blogId}: 0">
        <input type="text" class="form-control col-sm-6" id="blogName" name="blogName"
               placeholder="*请输入文章标题(必填)"
               th:value="${blog!=null and blog.blogTitle!=null }?${blog.blogTitle}: ''"
               required="true">
        &nbsp;&nbsp;
        <input type="text" class="form-control" id="blogTags" name="blogTags"
               placeholder="请输入文章标签"
               th:value="${blog!=null and blog.blogTags!=null }?${blog.blogTags}: ''"
               style="width: 100%;">
    </div>
    <div class="form-group" style="display:flex;">
        <input type="text" class="form-control col-sm-6" id="blogSubUrl"
               name="blogSubUrl"
               th:value="${blog!=null and blog.blogSubUrl!=null }?${blog.blogSubUrl}: ''"
               placeholder="请输入自定义路径,如:springboot-mybatis,默认为id"> &nbsp;&nbsp;
        <select class="form-control select2" style="width: 100%;" id="blogCategoryId"
                data-placeholder="请选择分类...">
            <th:block th:if="${null == categories}">
                <option value="0" selected="selected">默认分类</option>
            </th:block>
            <th:block th:unless="${null == categories}">
                <th:block th:each="c : ${categories}">
                    <option th:value="${c.categoryId}" th:text="${c.categoryName}"
                            th:selected="${null !=blog and null !=blog.blogCategoryId and blog.blogCategoryId==c.categoryId} ?true:false">
                        >
                    </option>
                </th:block>
            </th:block>
        </select>
    </div>
    <div class="form-group" id="blog-editormd">
        <textarea style="display:none;"
                  th:utext="${blog!=null and blog.blogContent !=null}?${blog.blogContent}: ''"></textarea>
    </div>
    <div class="form-group">
        <div class="col-sm-4">
            <th:block th:if="${null == blog}">
                <img id="blogCoverImage" src="/admin/dist/img/img-upload.png"
                     style="height: 64px;width: 64px;">
            </th:block>
            <th:block th:unless="${null == blog}">
                <img id="blogCoverImage" th:src="${blog.blogCoverImage}"
                     style="width:160px ;height: 120px;display:block;">
            </th:block>
        </div>
    </div>
    <br>
    <div class="form-group">
        <div class="col-sm-4">
            <button class="btn btn-info" style="margin-bottom: 5px;" id="uploadCoverImage">
                <i class="fa fa-picture-o"></i>&nbsp;上传封面
            </button>
            <button class="btn btn-secondary"
                    style="margin-bottom: 5px;"
                    id="randomCoverImage"><i
                                             class="fa fa-random"></i>&nbsp;随机封面
            </button>
        </div>
    </div>
    <div class="form-group">
        <label class="control-label">文章状态:&nbsp;</label>
        <input name="blogStatus" type="radio" id="publish"
               checked=true
               th:checked="${null==blog||(null !=blog and null !=blog.blogStatus and blog.blogStatus==1)} ?true:false"
               value="1"/>&nbsp;发布&nbsp;
        <input name="blogStatus" type="radio" id="draft" value="0"
               th:checked="${null !=blog and null !=blog.blogStatus and blog.blogStatus==0} ?true:false"/>&nbsp;草稿&nbsp;&nbsp;&nbsp;
        <label class="control-label">是否允许评论:&nbsp;</label>
        <input name="enableComment" type="radio" id="enableCommentTrue" checked=true
               th:checked="${null==blog||(null !=blog and null !=blog.enableComment and blog.enableComment==0)} ?true:false"
               value="0"/>&nbsp;是&nbsp;
        <input name="enableComment" type="radio" id="enableCommentFalse" value="1"
               th:checked="${null !=blog and null !=blog.enableComment and blog.enableComment==1} ?true:false"/>&nbsp;否&nbsp;
    </div>
    <div class="form-group">
        <!-- 按钮 -->
        &nbsp;<button class="btn btn-info float-right" style="margin-left: 5px;"
                      id="confirmButton">保存文章
        </button>&nbsp;
        &nbsp;<button class="btn btn-secondary float-right" style="margin-left: 5px;"
                      id="cancelButton">返回文章列表
        </button>&nbsp;
    </div>
</form>
```

只是在原来编辑 DOM 的基础上加上 Thymeleaf 读取的语法，这样改造之后就完成了编辑功能的前两步，获取详情并回显到编辑页面中。

#### 文章修改接口实现

大部分功能模块的修改接口，都与添加接口类似，唯一的不同点就是修改接口需要知道修改的是哪一条，因此可以模仿添加接口来实现文章修改接口，修改接口负责接收前端的 POST 请求并处理其中的参数，接收的参数为用户在博客编辑页面输入的所有字段内容以及文章的主键 id，字段名称与对应的含义如下：

1. "**blogId**": 文章主键
2. "**blogTitle**": 文章标题
3. "**blogSubUrl**": 自定义路径
4. "**blogCategoryId**": 分类 id (下拉框中选择)
5. "**blogTags**": 标签字段(以逗号分隔)
6. "**blogContent**": 文章内容(编辑器中的 md 文档)
7. "**blogCoverImage**": 封面图(上传图片或者随机图片的路径)
8. "**blogStatus**": 文章状态
9. "**enableComment**": 评论开关

##### 控制层

在 BlogController 中新增 update() 方法，接口的映射地址为 /blogs/update，请求方法为 POST，代码如下：

```java
    @PostMapping("/blogs/update")
    @ResponseBody
    public Result update(@RequestParam("blogId") Long blogId,
                         @RequestParam("blogTitle") String blogTitle,
                         @RequestParam(name = "blogSubUrl", required = false) String blogSubUrl,
                         @RequestParam("blogCategoryId") Integer blogCategoryId,
                         @RequestParam("blogTags") String blogTags,
                         @RequestParam("blogContent") String blogContent,
                         @RequestParam("blogCoverImage") String blogCoverImage,
                         @RequestParam("blogStatus") Byte blogStatus,
                         @RequestParam("enableComment") Byte enableComment) {
        if (StringUtils.isEmpty(blogTitle)) {
            return ResultGenerator.genFailResult("请输入文章标题");
        }
        if (blogTitle.trim().length() > 150) {
            return ResultGenerator.genFailResult("标题过长");
        }
        if (StringUtils.isEmpty(blogTags)) {
            return ResultGenerator.genFailResult("请输入文章标签");
        }
        if (blogTags.trim().length() > 150) {
            return ResultGenerator.genFailResult("标签过长");
        }
        if (blogSubUrl.trim().length() > 150) {
            return ResultGenerator.genFailResult("路径过长");
        }
        if (StringUtils.isEmpty(blogContent)) {
            return ResultGenerator.genFailResult("请输入文章内容");
        }
        if (blogTags.trim().length() > 100000) {
            return ResultGenerator.genFailResult("文章内容过长");
        }
        if (StringUtils.isEmpty(blogCoverImage)) {
            return ResultGenerator.genFailResult("封面图不能为空");
        }
        Blog blog = new Blog();
        blog.setBlogId(blogId);
        blog.setBlogTitle(blogTitle);
        blog.setBlogSubUrl(blogSubUrl);
        blog.setBlogCategoryId(blogCategoryId);
        blog.setBlogTags(blogTags);
        blog.setBlogContent(blogContent);
        blog.setBlogCoverImage(blogCoverImage);
        blog.setBlogStatus(blogStatus);
        blog.setEnableComment(enableComment);
        String updateBlogResult = blogService.updateBlog(blog);
        if ("success".equals(updateBlogResult)) {
            return ResultGenerator.genSuccessResult("修改成功");
        } else {
            return ResultGenerator.genFailResult(updateBlogResult);
        }
    }
```

首先会对参数进行校验，之后交给业务层代码进行操作，与添加接口不同的是传参，多了主键 id，我们需要知道要修改的哪一条数据。

##### 业务层

在 service 包中新建 BlogService 并定义接口方法 `updateBlog()`，下面为具体的实现方法代码：

```java
    /**
     * 更新博客信息
     * @param blog
     * @return
     */
    @Override
    @Transactional
    public String updateBlog(Blog blog) {
        Blog blogForUpdate = blogDao.selectByPrimaryKey(blog.getBlogId());
        if (blogForUpdate == null) {
            return "数据不存在";
        }
        blogForUpdate.setBlogTitle(blog.getBlogTitle());
        blogForUpdate.setBlogSubUrl(blog.getBlogSubUrl());
        blogForUpdate.setBlogContent(blog.getBlogContent());
        blogForUpdate.setBlogCoverImage(blog.getBlogCoverImage());
        blogForUpdate.setBlogStatus(blog.getBlogStatus());
        blogForUpdate.setEnableComment(blog.getEnableComment());
        BlogCategory blogCategory = categoryDao.selectByPrimaryKey(blog.getBlogCategoryId());
        if (blogCategory == null) {
            blogForUpdate.setBlogCategoryId(0);
            blogForUpdate.setBlogCategoryName("默认分类");
        } else {
            // 设置博客分类名称
            blogForUpdate.setBlogCategoryName(blogCategory.getCategoryName());
            blogForUpdate.setBlogCategoryId(blogCategory.getCategoryId());
            // 分类的排序值加1
            blogCategory.setCategoryRank(blogCategory.getCategoryRank() + 1);
        }
        // 处理标签数据
        String[] tags = blog.getBlogTags().split(",");
        if (tags.length > 6) {
            return "标签数量限制为6";
        }
        blogForUpdate.setBlogTags(blog.getBlogTags());
        // 新增的tag对象
        List<BlogTag> tagListForInsert = new ArrayList<>();
        // 所有的tag对象，用于建立关系数据
        List<BlogTag> allTagsList = new ArrayList<>();
        for (int i = 0; i < tags.length; i++) {
            BlogTag tag = tagDao.selectByTagName(tags[i]);
            if (tag == null) {
                // 不存在就新增
                BlogTag tempTag = new BlogTag();
                tempTag.setTagName(tags[i]);
                tagListForInsert.add(tempTag);
            } else {
                allTagsList.add(tag);
            }
        }
        // 新增标签数据不为空->新增标签数据
        if (!CollectionUtils.isEmpty(tagListForInsert)) {
            tagDao.batchInsertBlogTag(tagListForInsert);
        }
        List<BlogTagRelation> blogTagRelations = new ArrayList<>();
        // 新增关系数据
        allTagsList.addAll(tagListForInsert);
        for (BlogTag tag : allTagsList) {
            BlogTagRelation blogTagRelation = new BlogTagRelation();
            blogTagRelation.setBlogId(blog.getBlogId());
            blogTagRelation.setTagId(tag.getTagId());
            blogTagRelations.add(blogTagRelation);
        }
        // 修改blog信息->修改分类排序值->删除原关系数据->保存新的关系数据
        categoryDao.updateByPrimaryKeySelective(blogCategory);
        // 删除原关系数据
        blogTagRelationDao.deleteByBlogId(blog.getBlogId());
        blogTagRelationDao.batchInsert(blogTagRelations);
        if (blogDao.updateByPrimaryKeySelective(blogForUpdate) > 0) {
            return "success";
        }
        return "修改失败";
    }
```

首先，`updateBlog()` 方法会判断是否存在当前想要修改的记录，之后的标签及标签关系处理逻辑与 `saveBlog()` 方法一样，不同点是关系数据的保存，`saveBlog()` 方法是直接保存关系数据，因为是全新的关系数据，而 `updateBlog()` 方法的处理则需要修改，因为在修改前就可能已经存在关系数据了，所以需要先把关系数据删掉再保存新的关系数据。

##### 关键 SQL

根据文章 id 删除原来的关系数据（BlogTagRelationMapper.xml，对应Dao类也要添加方法）：

```xml
    <delete id="deleteByBlogId" parameterType="java.lang.Long">
        delete from tb_blog_tag_relation
        where blog_id = #{blogId,jdbcType=BIGINT}
    </delete>
```

##### Ajax 调用修改接口

对文章数据进行修改之后可以点击信息编辑框下方的**保存文章**按钮，此时会调用后端接口并进行数据的交互，js 实现代码如下（在 `#confirmButton` 绑定的点击事件中添加）：

```javascript
    //blogId大于0则为修改操作
    if (blogId > 0) {
        url = '/admin/blogs/update';
        swlMessage = '修改成功';
        data = {
            blogId: blogId,
            blogTitle: blogTitle,
            blogSubUrl: blogSubUrl,
            blogCategoryId: blogCategoryId,
            blogTags: blogTags,
            blogContent: blogContent,
            blogCoverImage: blogCoverImage,
            blogStatus: blogStatus,
            enableComment: enableComment,
        };
    }
```

这个方法就是直接改造前一个实验中的方法，在保存按钮的点击事件处理函数中，首先判断 blogId 是否大于 0，如果大于 0 则证明这是一个修改请求，之后封装数据并向后端发送 Ajax 请求修改文章。

#### 文章管理页面制作

##### 导航栏

首先在左侧导航栏中新增文章管理页的导航按钮，在 sidebar.html 文件中新增如下代码：

```html
<li class="nav-item">
    <a th:href="@{/admin/blogs}" th:class="${path}=='blogs'?'nav-link active':'nav-link'">
        <i class="fa fa-list-alt nav-icon" aria-hidden="true"></i>
        <p>
            博客管理
        </p>
    </a>
</li>
```

点击后的跳转路径为 /admin/blogs，之后新建 Controller 来处理该路径并跳转到对应的页面。

##### Controller 处理跳转

在 BlogController.java 中新增如下代码：

```java
    @GetMapping("/blogs")
    public String list(HttpServletRequest request) {
        request.setAttribute("path", "blogs");
        return "admin/blog";
    }
```

##### blog.html 页面制作

在 resources/templates/admin 目录下新建 blog.html，并引入对应的 js 文件和 css 样式文件，代码如下：

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<header th:replace="admin/header::header-fragment"></header>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <!-- 引入页面头header-fragment -->
    <div th:replace="admin/header::header-nav"></div>
    <!-- 引入工具栏sidebar-fragment -->
    <div th:replace="admin/sidebar::sidebar-fragment(${path})"></div>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <div class="content-header">
            <div class="container-fluid"></div>
            <!-- /.container-fluid -->
        </div>
        <!-- Main content -->
        <div class="content">
            <div class="container-fluid">
                <div class="card card-primary card-outline">
                    <div class="card-header">
                        <h3 class="card-title">博客管理</h3>
                    </div>
                    <!-- /.card-body -->
                    <div class="card-body"></div>
                    <!-- /.card-body -->
                </div>
            </div>
            <!-- /.container-fluid -->
        </div>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <!-- 引入页脚footer-fragment -->
    <div th:replace="admin/footer::footer-fragment"></div>
</div>
<!-- jQuery -->
<script th:src="@{/admin/plugins/jquery/jquery.min.js}"></script>
<!-- jQuery UI 1.11.4 -->
<script th:src="@{/admin/plugins/jQueryUI/jquery-ui.min.js}"></script>
<!-- Bootstrap 4 -->
<script
        th:src="@{/admin/plugins/bootstrap/js/bootstrap.bundle.min.js}"
></script>
<!-- AdminLTE App -->
<script th:src="@{/admin/dist/js/adminlte.min.js}"></script>
<!-- jqgrid -->
<script th:src="@{/admin/plugins/jqgrid-5.3.0/jquery.jqGrid.min.js}"></script>
<script th:src="@{/admin/plugins/jqgrid-5.3.0/grid.locale-cn.js}"></script>
<!-- sweetalert -->
<script th:src="@{/admin/plugins/sweetalert/sweetalert.min.js}"></script>
<script th:src="@{/admin/dist/js/public.js}"></script>
</body>
</html>
```



#### 博客管理模块接口设计及实现

##### 文章列表分页接口

列表接口负责接收前端传来的分页参数，如 page 、limit 等参数，之后将数据总数和对应页面的数据列表查询出来并封装为分页数据返回给前端。

###### 控制层

接口的映射地址为 /blogs/list，请求方法为 GET，代码如下：

```java
    @GetMapping("/blogs/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(blogService.getBlogsPage(pageUtil));
    }
```

###### 业务层

```java
    @Override
    public PageResult getBlogsPage(PageQueryUtil pageUtil) {
        List<Blog> blogList = blogDao.findBlogList(pageUtil);
        int total = blogDao.getTotalBlogs(pageUtil);
        PageResult pageResult = new PageResult(blogList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
```

###### BlogMapper.xml

```xml
	<select id="findBlogList" parameterType="Map" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_blog
        where is_deleted=0
        order by blog_id desc
        <if test="start!=null and limit!=null">
            limit #{start},#{limit}
        </if>
    </select>

    <select id="getTotalBlogs" parameterType="Map" resultType="int">
        select count(*) from tb_blog
        where is_deleted=0
    </select>
```

SQL 语句在 BlogMapper.xml 文件中，一般的分页也就是使用 limit 关键字实现，获取响应条数的记录和总数之后再进行数据封装，这个接口就是根据前端传的分页参数进行查询并返回分页数据以供前端页面进行数据渲染。

##### 删除接口

删除接口负责接收前端的文章删除请求，处理前端传输过来的数据后，将这些记录从数据库中删除，这里的“删除”功能并不是真正意义上的删除，而是逻辑删除，将接受的参数设置为一个数组，可以同时删除多条记录，只需要在前端将用户选择的记录 id 封装好再传参到后端即可。

###### 控制层

接口的映射地址为 /blogs/delete，请求方法为 POST，代码如下：

```java
    @PostMapping("/blogs/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (blogService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }
```

###### 业务层

```java
	@Override
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        return blogDao.deleteBatch(ids) > 0;
    }
```

###### BlogMapper.xml

```xml
    <update id="deleteBatch">
        update tb_blog
        set is_deleted=1 where blog_id in
        <foreach item="id" collection="array" open="(" separator="," close=")">
            #{id}
        </foreach>
    </update>
```

接口的请求路径为 /blogs/delete，并使用 `@RequestBody` 将前端传过来的参数封装为 id 数组，参数验证通过后则调用 `deleteBatch()` 批量删除方法进行数据库操作，否则将向前端返回错误信息。

#### 前端页面实现

##### 功能按钮

管理页面中设计了常用的几个功能按钮：文章添加按钮、文章修改按钮、文章删除按钮，因此在 **blog.html** 页面中添加对应的功能按钮以及触发事件，代码如下：

```html
<div class="grid-btn">
  <button class="btn btn-success" onclick="addBlog()">
    <i class="fa fa-plus"></i>&nbsp;新增
  </button>
  <button class="btn btn-info" onclick="editBlog()">
    <i class="fa fa-edit"></i>&nbsp;修改
  </button>
  <button class="btn btn-danger" onclick="deleteBlog()">
    <i class="fa fa-trash-o"></i>&nbsp;删除
  </button>
</div>
```

分别是添加按钮对应的触发事件是 `addBlog()` 方法，修改按钮对应的触发事件是 `editBlog()` 方法，删除按钮对应的触发事件是 `deleteBlog()` 方法。

##### 分页信息展示区域

页面中已经引入 JqGrid 的相关静态资源文件，需要在页面中展示分页数据的区域增加如下代码：

```html
<!-- JqGrid必要DOM,用于创建表格展示列表数据 -->
<table id="jqGrid" class="table table-bordered"></table>
<!-- JqGrid必要DOM,分页信息区域 -->
<div id="jqGridPager"></div>
```



#### 文章管理模块前端功能实现

##### 分页功能

在 resources/static/admin/dist/js 目录下新增 blog.js 文件，并添加如下代码：

```javascript
$(function () {
  $('#jqGrid').jqGrid({
    url: '/admin/blogs/list',
    datatype: 'json',
    colModel: [
      {
        label: 'id',
        name: 'blogId',
        index: 'blogId',
        width: 50,
        key: true,
        hidden: true,
      },
      { label: '标题', name: 'blogTitle', index: 'blogTitle', width: 140 },
      {
        label: '预览图',
        name: 'blogCoverImage',
        index: 'blogCoverImage',
        width: 120,
        formatter: coverImageFormatter,
      },
      { label: '浏览量', name: 'blogViews', index: 'blogViews', width: 60 },
      {
        label: '状态',
        name: 'blogStatus',
        index: 'blogStatus',
        width: 60,
        formatter: statusFormatter,
      },
      {
        label: '博客分类',
        name: 'blogCategoryName',
        index: 'blogCategoryName',
        width: 60,
      },
      { label: '添加时间', name: 'createTime', index: 'createTime', width: 90 },
    ],
    height: 700,
    rowNum: 10,
    rowList: [10, 20, 50],
    styleUI: 'Bootstrap',
    loadtext: '信息读取中...',
    rownumbers: false,
    rownumWidth: 20,
    autowidth: true,
    multiselect: true,
    pager: '#jqGridPager',
    jsonReader: {
      root: 'data.list',
      page: 'data.currPage',
      total: 'data.totalPage',
      records: 'data.totalCount',
    },
    prmNames: {
      page: 'page',
      rows: 'limit',
      order: 'order',
    },
    gridComplete: function () {
      //隐藏grid底部滚动条
      $('#jqGrid').closest('.ui-jqgrid-bdiv').css({ 'overflow-x': 'hidden' });
    },
  });

  $(window).resize(function () {
    $('#jqGrid').setGridWidth($('.card-body').width());
  });

  function coverImageFormatter(cellvalue) {
    return (
      "<img src='" +
      cellvalue +
      '\' height="120" width="160" alt=\'coverImage\'/>'
    );
  }

  function statusFormatter(cellvalue) {
    if (cellvalue == 0) {
      return '<button type="button" class="btn btn-block btn-secondary btn-sm" style="width: 50%;">草稿</button>';
    } else if (cellvalue == 1) {
      return '<button type="button" class="btn btn-block btn-success btn-sm" style="width: 50%;">发布</button>';
    }
  }
});
```

以上代码的主要功能为分页数据展示、字段格式化 jqGrid DOM 宽度的自适应，在页面加载时，调用 JqGrid 的初始化方法，将页面中 id 为 jqGrid 的 DOM 渲染为分页表格，并向后端发送请求，之后按照后端返回的 json 数据填充表格以及表格下方的分页按钮。

##### 按钮事件

添加和修改两个按钮分别绑定了触发事件，我们需要在 blog.js 文件中新增 addBlog() 方法和 editBlog() 方法，两个方法中的实现均为跳转至文章编辑页面，触发事件代码如下：

```javascript
function addBlog() {
  window.location.href = '/admin/blogs/edit';
}

function editBlog() {
  var id = getSelectedRow();
  if (id == null) {
    return;
  }
  window.location.href = '/admin/blogs/edit/' + id;
}
```

点击添加按钮时是直接跳转到文章编辑页，点击修改时首先要获取当前选择的需要修改的文章 id，之后跳转至文章编辑页，添加和修改操作则是在编辑页面完成，这里就只是跳转。

##### 删除功能

删除按钮的点击触发事件为 `deleteBlog()`，在 blog.js 文件中新增如下代码：

```javascript
function deleteBlog() {
  var ids = getSelectedRows();
  if (ids == null) {
    return;
  }
  swal({
    title: '确认弹框',
    text: '确认要删除数据吗?',
    icon: 'warning',
    buttons: true,
    dangerMode: true,
  }).then((flag) => {
    if (flag) {
      $.ajax({
        type: 'POST',
        url: '/admin/blogs/delete',
        contentType: 'application/json',
        data: JSON.stringify(ids),
        success: function (r) {
          if (r.resultCode == 200) {
            swal('删除成功', {
              icon: 'success',
            });
            $('#jqGrid').trigger('reloadGrid');
          } else {
            swal(r.message, {
              icon: 'error',
            });
          }
        },
      });
    }
  });
}
```

获取用户在 jqgrid 表格中选择的需要删除的所有记录的 id，之后将参数封装并向后端发送 Ajax 请求，请求地址为 blogs/delete。



## 友链模块

友情链接是具有一定资源互补优势的网站之间的简单合作形式，即分别在自己的网站上放置对方网站的 LOGO 图片或文字的网站名称，并设置对方网站的超链接使得用户可以从合作网站中发现自己的网站，达到互相推广的目的，因此常作为一种网站推广基本手段。友情链接是指互相在自己的网站上放对方网站的链接。必须要能在网页代码中找到网址和网站名称，而且浏览网页的时候能显示网站名称，这样才叫友情链接。

通常来说，友情链接交换的意义主要体现在如下几方面：

- **提升网站流量**
- **完善用户体验**
- **增加网站外链**
- **提高关键字排名**
- **提高网站权重**
- **提高知名度**

自建的技术博客网站毕竟是私人网站，流量和关注度肯定不会特别高，通过友情链接的设置可以增加一些流量，这也是大部分技术博客中不可缺少的一个元素，基本上每个博主都会设置友情链接。



### 持久层相关

#### 表结构设计

在进行接口设计和具体的功能实现前，首先将表结构确定下来，根据前文中几张友情链接模块的图片我们可以发现，该模块也只是用作展示使用，其中有三个字段是非常重要的，依次为：

- 友情链接的名称
- 友情链接的跳转链接
- 友情链接的简单描述

基于此友情链接表的 SQL 设计如下，直接执行如下 SQL 语句即可：

```sql
USE `my_blog_db`;
DROP TABLE IF EXISTS `tb_link`;
CREATE TABLE `tb_link` (
  `link_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '友链表主键id',
  `link_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '友链类别 0-友链 1-推荐 2-个人网站',
  `link_name` varchar(50) NOT NULL COMMENT '网站名称',
  `link_url` varchar(100) NOT NULL COMMENT '网站链接',
  `link_description` varchar(100) NOT NULL COMMENT '网站描述',
  `link_rank` int(11) NOT NULL DEFAULT '0' COMMENT '用于列表排序',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0-未删除 1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`link_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

友情链接表的字段以及每个字段对应的含义都在上面的 SQL 中有介绍，在三个基础字段的基础上又添加了友链类别 link_type 字段，因为有些链接是个人网站，为了更好的区分友链就加了此字段，把表结构导入到数据库中即可，接下来进行编码工作。

#### BlogLink 实体类

```java
package cn.yuyingwai.springbootblog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BlogLink {

    private Integer linkId;

    private Byte linkType;

    private String linkName;

    private String linkUrl;

    private String linkDescription;

    private Integer linkRank;

    private Byte isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public void setLinkName(String linkName) {
        this.linkName = linkName == null ? null : linkName.trim();
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl == null ? null : linkUrl.trim();
    }

    public void setLinkDescription(String linkDescription) {
        this.linkDescription = linkDescription == null ? null : linkDescription.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", linkId=").append(linkId);
        sb.append(", linkType=").append(linkType);
        sb.append(", linkName=").append(linkName);
        sb.append(", linkUrl=").append(linkUrl);
        sb.append(", linkDescription=").append(linkDescription);
        sb.append(", linkRank=").append(linkRank);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }

}
```

#### BlogLinkDao.java

```java
package cn.yuyingwai.springbootblog.dao;

import cn.yuyingwai.springbootblog.entity.BlogLink;
import cn.yuyingwai.springbootblog.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogLinkDao {

    int deleteByPrimaryKey(Integer linkId);

    int insert(BlogLink record);

    int insertSelective(BlogLink record);

    BlogLink selectByPrimaryKey(Integer linkId);

    int updateByPrimaryKeySelective(BlogLink record);

    int updateByPrimaryKey(BlogLink record);

    List<BlogLink> findLinkList(PageQueryUtil pageUtil);

    int getTotalLinks(PageQueryUtil pageUtil);

    int deleteBatch(Integer[] ids);

}
```

#### BlogLinkMapper.xml

```java
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.yuyingwai.springbootblog.dao.BlogLinkDao">
    <resultMap id="BaseResultMap" type="cn.yuyingwai.springbootblog.entity.BlogLink">
        <id column="link_id" jdbcType="INTEGER" property="linkId"/>
        <result column="link_type" jdbcType="TINYINT" property="linkType"/>
        <result column="link_name" jdbcType="VARCHAR" property="linkName"/>
        <result column="link_url" jdbcType="VARCHAR" property="linkUrl"/>
        <result column="link_description" jdbcType="VARCHAR" property="linkDescription"/>
        <result column="link_rank" jdbcType="INTEGER" property="linkRank"/>
        <result column="is_deleted" jdbcType="TINYINT" property="isDeleted"/>
        <result column="create_time" jdbcType="TIMESTAMP" property="createTime"/>
    </resultMap>
    <sql id="Base_Column_List">
        link_id, link_type, link_name, link_url, link_description, link_rank, is_deleted, 
    create_time
    </sql>
    <select id="selectByPrimaryKey" parameterType="java.lang.Integer" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_link
        where link_id = #{linkId,jdbcType=INTEGER} AND is_deleted = 0
    </select>

    <update id="deleteByPrimaryKey" parameterType="java.lang.Integer">
        UPDATE tb_link SET is_deleted = 1
        where link_id = #{linkId,jdbcType=INTEGER} AND is_deleted = 0
    </update>

    <update id="deleteBatch">
        update tb_link
        set is_deleted=1 where link_id in
        <foreach item="id" collection="array" open="(" separator="," close=")">
            #{id}
        </foreach>
    </update>

    <select id="findLinkList" parameterType="Map" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_link
        where is_deleted=0
        order by link_id desc
        <if test="start!=null and limit!=null">
            limit #{start},#{limit}
        </if>
    </select>

    <select id="getTotalLinks" parameterType="Map" resultType="int">
        select count(*)  from tb_link
        where is_deleted=0
    </select>

    <insert id="insert" parameterType="cn.yuyingwai.springbootblog.entity.BlogLink">
        insert into tb_link (link_id, link_type, link_name,
                             link_url, link_description, link_rank,
                             is_deleted, create_time)
        values (#{linkId,jdbcType=INTEGER}, #{linkType,jdbcType=TINYINT}, #{linkName,jdbcType=VARCHAR},
                #{linkUrl,jdbcType=VARCHAR}, #{linkDescription,jdbcType=VARCHAR}, #{linkRank,jdbcType=INTEGER},
                #{isDeleted,jdbcType=TINYINT}, #{createTime,jdbcType=TIMESTAMP})
    </insert>
    <insert id="insertSelective" parameterType="cn.yuyingwai.springbootblog.entity.BlogLink">
        insert into tb_link
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="linkId != null">
                link_id,
            </if>
            <if test="linkType != null">
                link_type,
            </if>
            <if test="linkName != null">
                link_name,
            </if>
            <if test="linkUrl != null">
                link_url,
            </if>
            <if test="linkDescription != null">
                link_description,
            </if>
            <if test="linkRank != null">
                link_rank,
            </if>
            <if test="isDeleted != null">
                is_deleted,
            </if>
            <if test="createTime != null">
                create_time,
            </if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="linkId != null">
                #{linkId,jdbcType=INTEGER},
            </if>
            <if test="linkType != null">
                #{linkType,jdbcType=TINYINT},
            </if>
            <if test="linkName != null">
                #{linkName,jdbcType=VARCHAR},
            </if>
            <if test="linkUrl != null">
                #{linkUrl,jdbcType=VARCHAR},
            </if>
            <if test="linkDescription != null">
                #{linkDescription,jdbcType=VARCHAR},
            </if>
            <if test="linkRank != null">
                #{linkRank,jdbcType=INTEGER},
            </if>
            <if test="isDeleted != null">
                #{isDeleted,jdbcType=TINYINT},
            </if>
            <if test="createTime != null">
                #{createTime,jdbcType=TIMESTAMP},
            </if>
        </trim>
    </insert>
    <update id="updateByPrimaryKeySelective" parameterType="cn.yuyingwai.springbootblog.entity.BlogLink">
        update tb_link
        <set>
            <if test="linkType != null">
                link_type = #{linkType,jdbcType=TINYINT},
            </if>
            <if test="linkName != null">
                link_name = #{linkName,jdbcType=VARCHAR},
            </if>
            <if test="linkUrl != null">
                link_url = #{linkUrl,jdbcType=VARCHAR},
            </if>
            <if test="linkDescription != null">
                link_description = #{linkDescription,jdbcType=VARCHAR},
            </if>
            <if test="linkRank != null">
                link_rank = #{linkRank,jdbcType=INTEGER},
            </if>
            <if test="isDeleted != null">
                is_deleted = #{isDeleted,jdbcType=TINYINT},
            </if>
            <if test="createTime != null">
                create_time = #{createTime,jdbcType=TIMESTAMP},
            </if>
        </set>
        where link_id = #{linkId,jdbcType=INTEGER}
    </update>
    <update id="updateByPrimaryKey" parameterType="cn.yuyingwai.springbootblog.entity.BlogLink">
        update tb_link
        set link_type = #{linkType,jdbcType=TINYINT},
            link_name = #{linkName,jdbcType=VARCHAR},
            link_url = #{linkUrl,jdbcType=VARCHAR},
            link_description = #{linkDescription,jdbcType=VARCHAR},
            link_rank = #{linkRank,jdbcType=INTEGER},
            is_deleted = #{isDeleted,jdbcType=TINYINT},
            create_time = #{createTime,jdbcType=TIMESTAMP}
        where link_id = #{linkId,jdbcType=INTEGER}
    </update>
</mapper>
```



### 友情链接管理页面制作

#### 导航栏

首先在左侧导航栏中新增友情链接管理页的导航按钮，在 sidebar.html 文件中新增如下代码：

```html
<li class="nav-item">
    <a
       th:href="@{/admin/links}"
       th:class="${path}=='links'?'nav-link active':'nav-link'"
       >
        <i class="fa fa-heart nav-icon" aria-hidden="true"></i>
        <p>
            友情链接
        </p>
    </a>
</li>
```

点击后的跳转路径为 /admin/links，之后新建 Controller 来处理该路径并跳转到对应的页面。

#### Controller 处理跳转

首先在 controller/admin 包下新建 LinkController.java，之后新增如下代码：

```java
package cn.yuyingwai.springbootblog.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class LinkController {

    @GetMapping("/links")
    public String linkPage(HttpServletRequest request) {
        request.setAttribute("path", "links");
        return "admin/link";
    }

}
```

`linkPage` 方法用于处理 /admin/links 请求，并设置 path 字段，之后跳转到 admin 目录下的 link.html 中。

#### link.html 页面制作

接下来就是博客编辑页面的模板文件制作了，在 resources/templates/admin 目录下新建 link.html，并引入对应的 js 文件和 css 样式文件，代码如下：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<header th:replace="admin/header::header-fragment"></header>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <!-- 引入页面头header-fragment -->
    <div th:replace="admin/header::header-nav"></div>
    <!-- 引入工具栏sidebar-fragment -->
    <div th:replace="admin/sidebar::sidebar-fragment(${path})"></div>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <div class="content-header">
            <div class="container-fluid">
            </div><!-- /.container-fluid -->
        </div>
        <!-- Main content -->
        <div class="content">
            <div class="container-fluid">
                <div class="card card-primary card-outline">
                    <div class="card-header">
                        <h3 class="card-title">友情链接管理</h3>
                    </div> <!-- /.card-body -->
                </div>
            </div><!-- /.container-fluid -->
        </div>
        
    </div>
    <!-- /.content-wrapper -->
    <!-- 引入页脚footer-fragment -->
    <div th:replace="admin/footer::footer-fragment"></div>
</div>
<!-- jQuery -->
<script th:src="@{/admin/plugins/jquery/jquery.min.js}"></script>
<!-- jQuery UI 1.11.4 -->
<script th:src="@{/admin/plugins/jQueryUI/jquery-ui.min.js}"></script>
<!-- Bootstrap 4 -->
<script th:src="@{/admin/plugins/bootstrap/js/bootstrap.bundle.min.js}"></script>
<!-- AdminLTE App -->
<script th:src="@{/admin/dist/js/adminlte.min.js}"></script>
<script th:src="@{/admin/plugins/jqgrid-5.3.0/jquery.jqGrid.min.js}"></script>
<script th:src="@{/admin/plugins/jqgrid-5.3.0/grid.locale-cn.js}"></script>
<!-- sweetalert -->
<script th:src="@{/admin/plugins/sweetalert/sweetalert.min.js}"></script>
<script th:src="@{/admin/dist/js/public.js}"></script>
</body>
</html>
```



### 友情链接模块接口设计及实现

#### 接口介绍

友链模块在后台管理系统中有 5 个接口，分别是：

- 友链列表分页接口
- 添加友链接口
- 根据 id 获取单条友链记录接口
- 修改友链接口
- 删除友链接口

接下来讲解每个接口具体的实现代码，首先在 controller/admin 包下新建 LinkController.java，并在 service 包下新建业务层代码 LinkService.java 及实现类，之后参照接口分别进行功能实现。

#### 列表分页接口

列表接口负责接收前端传来的分页参数，如 page 、limit 等参数，之后将数据总数和对应页面的数据列表查询出来并封装为分页数据返回给前端。

##### 控制层

```java
    /**
     * 友链列表
     * @param params
     * @return
     */
	@GetMapping("/links/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(linkService.getBlogLinkPage(pageUtil));
    }
```

##### 业务层

```java
    @Override
    public PageResult getBlogLinkPage(PageQueryUtil pageUtil) {
        List<BlogLink> links = blogLinkDao.findLinkList(pageUtil);
        int total = blogLinkDao.getTotalLinks(pageUtil);
        PageResult pageResult = new PageResult(links, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
```

#### 添加友链接口

添加接口负责接收前端的 POST 请求并处理其中的参数，接收的参数依次为：

1. linkType 字段(友链类型)
2. linkName 字段(友链名称)
3. linkUrl 字段(友链的跳转链接)
4. linkRank 字段(排序值)
5. linkDescription 字段(友链简介)

##### 控制层

接口的映射地址为 /links/save，请求方法为 POST，代码如下：

```java
    /**
     * 友链添加
     * @param linkType
     * @param linkName
     * @param linkUrl
     * @param linkRank
     * @param linkDescription
     * @return
     */
	@PostMapping("/links/save")
    @ResponseBody
    public Result save(@RequestParam("linkType") Integer linkType,
                       @RequestParam("linkName") String linkName,
                       @RequestParam("linkUrl") String linkUrl,
                       @RequestParam("linkRank") Integer linkRank,
                       @RequestParam("linkDescription") String linkDescription) {
        if (linkType == null || linkType < 0 || linkRank == null || linkRank < 0 || StringUtils.isEmpty(linkName) || StringUtils.isEmpty(linkName) || StringUtils.isEmpty(linkUrl) || StringUtils.isEmpty(linkDescription)) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        BlogLink link = new BlogLink();
        link.setLinkType(linkType.byteValue());
        link.setLinkRank(linkRank);
        link.setLinkName(linkName);
        link.setLinkUrl(linkUrl);
        link.setLinkDescription(linkDescription);
        return ResultGenerator.genSuccessResult(linkService.saveLink(link));
    }
```

##### 业务层

```java
    @Override
    public Boolean saveLink(BlogLink link) {
        return blogLinkDao.insertSelective(link) > 0;
    }
```

#### 删除友链接口

删除接口负责接收前端的友情链接删除请求，处理前端传输过来的数据后，将这些记录从数据库中删除，这里的“删除”功能并不是真正意义上的删除，而是逻辑删除，将接受的参数设置为一个数组，可以同时删除多条记录，只需要在前端将用户选择的记录 id 封装好再传参到后端即可。

接口的请求路径为 /links/delete，并使用 @RequestBody 将前端传过来的参数封装为 id 数组，参数验证通过后则调用 deleteBatch() 批量删除方法进行数据库操作，否则将向前端返回错误信息。

##### 控制层

接口的映射地址为 /links/delete，请求方法为 POST，代码如下：

```java
    /**
     * 友链删除
     * @param ids
     * @return
     */
    @PostMapping("/links/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (linkService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }
```

##### 业务层

```java
    @Override
    public Boolean deleteBatch(Integer[] ids) {
        return blogLinkDao.deleteBatch(ids) > 0;
    }
```

#### 详情

##### 控制层

```java
    /**
     * 详情
     * @param id
     * @return
     */
    @GetMapping("/links/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Integer id) {
        BlogLink link = linkService.selectById(id);
        return ResultGenerator.genSuccessResult(link);
    }
```

##### 业务层

```java
    @Override
    public BlogLink selectById(Integer id) {
        return blogLinkDao.selectByPrimaryKey(id);
    }
```

#### 修改接口

##### 控制层

```java
    /**
     * 友链修改
     * @param linkId
     * @param linkType
     * @param linkName
     * @param linkUrl
     * @param linkRank
     * @param linkDescription
     * @return
     */
    @PostMapping("/links/update")
    @ResponseBody
    public Result update(@RequestParam("linkId") Integer linkId,
                         @RequestParam("linkType") Integer linkType,
                         @RequestParam("LinkName") String linkName,
                         @RequestParam("linkUrl") String linkUrl,
                         @RequestParam("linkRank") Integer linkRank,
                         @RequestParam("linkDescription") String linkDescription) {
        BlogLink tempLink = linkService.selectById(linkId);
        if (tempLink == null) {
            return ResultGenerator.genFailResult("无数据！");
        }
        if (linkType == null || linkType < 0 || linkRank == null || linkRank < 0 || StringUtils.isEmpty(linkName) || StringUtils.isEmpty(linkName) || StringUtils.isEmpty(linkUrl) || StringUtils.isEmpty(linkDescription)) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        tempLink.setLinkType(linkType.byteValue());
        tempLink.setLinkRank(linkRank);
        tempLink.setLinkName(linkName);
        tempLink.setLinkUrl(linkUrl);
        tempLink.setLinkDescription(linkDescription);
        return ResultGenerator.genSuccessResult(linkService.updateLink(tempLink));
    }
```

##### 业务层

```java
    @Override
    public Boolean updateLink(BlogLink tempLink) {
        return blogLinkDao.updateByPrimaryKeySelective(tempLink) > 0;
    }
```



### 前端页面实现

#### 功能按钮

友情链接管理模块我们也设计了常用的几个功能：友链信息增加、友链信息编辑、友链信息删除，因此在页面中添加对应的功能按钮以及触发事件，代码如下：

```html
<div class="grid-btn">
  <button class="btn btn-info" onclick="linkAdd()">
    <i class="fa fa-plus"></i>&nbsp;新增
  </button>
  <button class="btn btn-info" onclick="linkEdit()">
    <i class="fa fa-pencil-square-o"></i>&nbsp;修改
  </button>
  <button class="btn btn-danger" onclick="deleteLink()">
    <i class="fa fa-trash-o"></i>&nbsp;删除
  </button>
</div>
```

分别是添加按钮对应的触发事件是 `linkAdd()` 方法，修改按钮对应的触发事件是 `linkEdit()` 方法，删除按钮对应的触发事件是 `deleteLink()` 方法。

#### 分页信息展示区域

页面中已经引入 JqGrid 的相关静态资源文件，需要在页面中展示分页数据的区域增加如下代码：

```html
<table id="jqGrid" class="table table-bordered"></table>
<div id="jqGridPager"></div>
```



### 前端功能实现

#### 分页功能

在 resources/static/admin/dist/js 目录下新增 link.js 文件，并添加如下代码：

```js
$(function () {
  $('#jqGrid').jqGrid({
    url: '/admin/links/list',
    datatype: 'json',
    colModel: [
      {
        label: 'id',
        name: 'linkId',
        index: 'linkId',
        width: 50,
        key: true,
        hidden: true,
      },
      { label: '网站名称', name: 'linkName', index: 'linkName', width: 100 },
      { label: '网站链接', name: 'linkUrl', index: 'linkUrl', width: 120 },
      {
        label: '网站描述',
        name: 'linkDescription',
        index: 'linkDescription',
        width: 120,
      },
      { label: '排序值', name: 'linkRank', index: 'linkRank', width: 30 },
      {
        label: '添加时间',
        name: 'createTime',
        index: 'createTime',
        width: 100,
      },
    ],
    height: 560,
    rowNum: 10,
    rowList: [10, 20, 50],
    styleUI: 'Bootstrap',
    loadtext: '信息读取中...',
    rownumbers: false,
    rownumWidth: 20,
    autowidth: true,
    multiselect: true,
    pager: '#jqGridPager',
    jsonReader: {
      root: 'data.list',
      page: 'data.currPage',
      total: 'data.totalPage',
      records: 'data.totalCount',
    },
    prmNames: {
      page: 'page',
      rows: 'limit',
      order: 'order',
    },
    gridComplete: function () {
      //隐藏grid底部滚动条
      $('#jqGrid').closest('.ui-jqgrid-bdiv').css({ 'overflow-x': 'hidden' });
    },
  });
  $(window).resize(function () {
    $('#jqGrid').setGridWidth($('.card-body').width());
  });
});
```

以上代码的主要功能为分页数据展示、字段格式化 jqGrid DOM 宽度的自适应，在页面加载时，调用 JqGrid 的初始化方法，将页面中 id 为 jqGrid 的 DOM 渲染为分页表格，并向后端发送请求，请求路径为 **/admin/links/list**，该路径即友链分页列表接口，之后按照后端返回的 json 数据填充表格以及表格下方的分页按钮。

#### 按钮事件及 Modal 框实现

添加和修改两个按钮分别绑定了触发事件，需要在 link.js 文件中新增 `linkAdd()` 方法和 `linkEdit()` 方法，两个方法中的实现为打开信息编辑框，下面实现信息编辑框和两个触发事件，代码如下：

```html
<div class="content">
  <!-- 模态框（Modal） -->
  <div
    class="modal fade"
    id="linkModal"
    tabindex="-1"
    role="dialog"
    aria-labelledby="linkModalLabel"
  >
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button
            type="button"
            class="close"
            data-dismiss="modal"
            aria-label="Close"
          >
            <span aria-hidden="true">&times;</span>
          </button>
          <h6 class="modal-title" id="linkModalLabel">Modal</h6>
        </div>
        <div class="modal-body">
          <form id="linkForm">
            <div class="form-group">
              <div
                class="alert alert-danger"
                id="edit-error-msg"
                style="display: none;"
              >
                错误信息展示栏。
              </div>
            </div>
            <input
              type="hidden"
              class="form-control"
              id="linkId"
              name="linkId"
            />
            <div class="form-group">
              <label for="linkType" class="control-label">友链类型:</label>
              <select class="form-control" id="linkType" name="linkType">
                <option selected="selected" value="0">友链</option>
                <option value="1">推荐网站</option>
                <option value="2">个人链接</option>
              </select>
            </div>
            <div class="form-group">
              <label for="linkName" class="control-label">网站名称:</label>
              <input
                type="text"
                class="form-control"
                id="linkName"
                name="linkName"
                placeholder="请输入网站名称"
                required="true"
              />
            </div>
            <div class="form-group">
              <label for="linkUrl" class="control-label">网站链接:</label>
              <input
                type="url"
                class="form-control"
                id="linkUrl"
                name="linkUrl"
                placeholder="请输入网站链接"
                required="true"
              />
            </div>
            <div class="form-group">
              <label for="linkDescription" class="control-label"
                >网站描述:</label
              >
              <input
                type="url"
                class="form-control"
                id="linkDescription"
                name="linkDescription"
                placeholder="请输入网站描述"
                required="true"
              />
            </div>
            <div class="form-group">
              <label for="linkRank" class="control-label">排序值:</label>
              <input
                type="number"
                class="form-control"
                id="linkRank"
                name="linkRank"
                placeholder="请输入排序值"
                required="true"
              />
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">
            取消
          </button>
          <button type="button" class="btn btn-primary" id="saveButton">
            确认
          </button>
        </div>
      </div>
    </div>
  </div>
  <!-- /.modal -->
</div>
```

`linkAdd()` 方法和 `linkEdit()` 方法实现如下：

```js
function linkAdd() {
  reset();
  $('.modal-title').html('友链添加');
  $('#linkModal').modal('show');
}

function linkEdit() {
  var id = getSelectedRow();
  if (id == null) {
    return;
  }
  reset();
  //请求数据
  $.get('/admin/links/info/' + id, function (r) {
    if (r.resultCode == 200 && r.data != null) {
      //填充数据至modal
      $('#linkName').val(r.data.linkName);
      $('#linkUrl').val(r.data.linkUrl);
      $('#linkDescription').val(r.data.linkDescription);
      $('#linkRank').val(r.data.linkRank);
      //根据原linkType值设置select选择器为选中状态
      if (r.data.linkType == 1) {
        $('#linkType option:eq(1)').prop('selected', 'selected');
      }
      if (r.data.linkType == 2) {
        $('#linkType option:eq(2)').prop('selected', 'selected');
      }
    }
  });
  $('.modal-title').html('友链修改');
  $('#linkModal').modal('show');
  $('#linkId').val(id);
}
```

添加方法仅仅是将 Modal 框显示，修改功能则多了一个步骤，需要将选择的记录回显到编辑框中以供修改，因此需要请求 links/info/{id} 详情接口获取被修改的友情链接数据信息。

#### 添加功能和编辑功能

在信息录入完成后可以点击信息编辑框下方的**确认**按钮，此时会进行数据的交互，js 实现代码如下：

```js
//绑定modal上的保存按钮
$('#saveButton').click(function () {
  var linkId = $('#linkId').val();
  var linkName = $('#linkName').val();
  var linkUrl = $('#linkUrl').val();
  var linkDescription = $('#linkDescription').val();
  var linkRank = $('#linkRank').val();
  if (!validCN_ENString2_18(linkName)) {
    $('#edit-error-msg').css('display', 'block');
    $('#edit-error-msg').html('请输入符合规范的名称！');
    return;
  }
  if (!isURL(linkUrl)) {
    $('#edit-error-msg').css('display', 'block');
    $('#edit-error-msg').html('请输入符合规范的网址！');
    return;
  }
  if (!validCN_ENString2_100(linkDescription)) {
    $('#edit-error-msg').css('display', 'block');
    $('#edit-error-msg').html('请输入符合规范的描述！');
    return;
  }
  if (isNull(linkRank) || linkRank < 0) {
    $('#edit-error-msg').css('display', 'block');
    $('#edit-error-msg').html('请输入符合规范的排序值！');
    return;
  }
  var params = $('#linkForm').serialize();
  var url = '/admin/links/save';
  if (linkId != null && linkId > 0) {
    url = '/admin/links/update';
  }
  $.ajax({
    type: 'POST', //方法类型
    url: url,
    data: params,
    success: function (result) {
      if (result.resultCode == 200 && result.data) {
        $('#linkModal').modal('hide');
        swal('保存成功', {
          icon: 'success',
        });
        reload();
      } else {
        $('#linkModal').modal('hide');
        swal('保存失败', {
          icon: 'error',
        });
      }
    },
    error: function () {
      swal('操作失败', {
        icon: 'error',
      });
    },
  });
});
```

由于传参和后续处理逻辑类似，为了避免太多重复代码因此将修改友链信息和添加友链信息两个方法写在一起了，通过 id 是否大于 0 来确定是修改操作还是添加操作，方法步骤如下：

1. 前端对用户输入的数据进行简单的正则验证
2. 封装数据
3. 向对应的后端接口发送 Ajax 请求
4. 请求成功后提醒用户请求成功并隐藏当前的信息编辑框，同时刷新列表数据
5. 请求失败则提醒对应的错误信息

#### 删除功能

删除按钮的点击触发事件为 deleteLink()，在 link.js 文件中新增如下代码：

```js
function deleteLink() {
  var ids = getSelectedRows();
  if (ids == null) {
    return;
  }
  swal({
    title: '确认弹框',
    text: '确认要删除数据吗?',
    icon: 'warning',
    buttons: true,
    dangerMode: true,
  }).then((flag) => {
    if (flag) {
      $.ajax({
        type: 'POST',
        url: '/admin/links/delete',
        contentType: 'application/json',
        data: JSON.stringify(ids),
        success: function (r) {
          if (r.resultCode == 200) {
            swal('删除成功', {
              icon: 'success',
            });
            $('#jqGrid').trigger('reloadGrid');
          } else {
            swal(r.message, {
              icon: 'error',
            });
          }
        },
      });
    }
  });
}
```

获取用户在 jqgrid 表格中选择的需要删除的所有记录的 id，之后将参数封装并向后端发送 Ajax 请求，请求地址为 links/delete。



# 博客主页部分



## 网站首页制作

### 页面设计

一个博客首页布局的通用模板：

![](http://images.yingwai.top/picgo/20201218100935.jpg)

![](http://images.yingwai.top/picgo/20201218100946.jpg)

由上图可以看出，博客首屏页面的整个设计版面被切分成七个部分：

1. 顶部导航栏：可以放置 Logo 图片、博客系统名称、其它页面的名称及跳转链接等信息；
2. 搜索框：实现文章搜索功能；
3. 文章列表：用于展示文章列表，显示文章的概览信息；
4. 博客统计：根据发布时间、点击次数等维度筛选出的博客列表；
5. 标签统计：筛选出使用频次高的标签或者分类数据；
6. 分页导航：放置分页按钮，用于分页跳转功能；
7. 页脚区域：放置博客的基本信息。

社区中还有许多其他的博客系统项目，由于前端的设计和实现非常灵活且多变，这些博客系统可能又是另外一些页面样式和页面布局，但是页面上所展现的数据和基础的页面布局可能不会大改，基础布局包括以下四个部分：

- 顶部导航栏区域：这个区域处于页面顶部或者左侧区域且占用页面的面积较小，用于放置 Logo 图、系统名称、其它页面的名称及跳转链接等导航信息用于实现页面跳转的管理。
- 侧边工具栏区域：这个区域中包括搜索框、博客统计、标签统计等信息，会展示一些数据但并不是最主要的部分，甚至有很多博客系统中并不会有侧边工具栏区域，该区域会被文章列表区所占用。
- 文章列表区域：这个区域会占用整个版面的大部分面积，包括前文中提到的文章列表和分页导航，因此是整个系统最重要的部分。
- 底部页脚区域：这个区域占用的面积较小，通常会在整个版面的底部一小部分区域，用来展示辅助信息，如版权信息、系统信息、项目版本号等等，不过这个区域并不是必须的。



### 首页制作

#### 静态页面

html 代码如下：

```html
<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <title>主页</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/style.css">
    <!--[if lt IE 9]>
    <script src="js/respond.js"></script>
    <![endif]-->
</head>
<body>
<header>
    <div class="widewrapper masthead">
        <div class="container">
            <a href="index.html" id="logo">
                <img src="img/logo.png" class="logo-img" alt="personal-blog">
            </a>
            <div id="mobile-nav-toggle" class="pull-right">
                <a href="#" data-toggle="collapse" data-target=".clean-nav .navbar-collapse">
                    <i class="fa fa-bars"></i>
                </a>
            </div>
            <nav class="pull-right clean-nav">
                <div class="collapse navbar-collapse">
                    <ul class="nav nav-pills navbar-nav">
                        <li>
                            <a href="index.html">主页</a>
                        </li>
                        <li>
                            <a href="##">关于</a>
                        </li>
                        <li>
                            <a href="##">联系我</a>
                        </li>
                    </ul>
                </div>
            </nav>
        </div>
    </div>
    <div class="widewrapper subheader">
        <div class="container">
            <div class="clean-breadcrumb">
                <a href="#">首页</a>
            </div>
            <div class="clean-searchbox">
                <form action="#" method="get" accept-charset="utf-8">
                    <input class="searchfield" id="searchbox" type="text" placeholder="搜索">
                    <button class="searchbutton" type="submit">
                        <i class="fa fa-search"></i>
                    </button>
                </form>
            </div>
        </div>
    </div>
</header>
<div class="widewrapper main">
    <div class="container">
        <div class="row">
            <div class="col-md-8 blog-main">
                <div class="col-md-6 col-sm-6 blog-main-card ">
                    <article class=" blog-summary">
                        <header>
                            <img src="img/photo1.png" alt="">
                            <h3><a href="##">第14课：SweetAlert 插件整合及搜索功能实现</a></h3>
                        </header>

                    </article>
                </div>
                <div class="col-md-6 col-sm-6 blog-main-card ">
                    <article class="blog-summary">
                        <header>
                            <img src="img/photo2.png" alt="">
                            <h3><a href="##">第13课：富文本信息管理模块</a></h3>
                        </header>

                    </article>
                </div>

                <div class="col-md-6 col-sm-6 blog-main-card ">
                    <article class="blog-summary">
                        <header>
                            <img src="img/photo1.png" alt="">
                            <h3><a href="##">第12课：文件导入导出功能</a></h3>
                        </header>

                    </article>
                </div>
                <div class="col-md-6 col-sm-6 blog-main-card ">
                    <article class="blog-summary">
                        <header>
                            <img src="img/photo1.png" alt="">
                            <h3><a href="##">第11课：多图上传与大文件分片上传、断点续传</a></h3>
                        </header>

                    </article>
                </div>
                <div class="col-md-6 col-sm-6 blog-main-card ">
                    <article class="blog-summary">
                        <header>
                            <img src="img/photo1.png" alt="">
                            <h3><a href="##">第10课：图片管理模块</a></h3>
                        </header>

                    </article>
                </div>
                <div class="col-md-6 col-sm-6 blog-main-card ">
                    <article class=" blog-summary">
                        <header>
                            <img src="img/photo2.png" alt="">
                            <h3><a href="##">第09课：弹框组件整合——完善添加和修改功能</a></h3>
                        </header>
                    </article>
                </div>
                <ul class="blog-pagination">
                    <li><a href="#">&laquo;</a></li>
                    <li class="active"><a href="#"> 1 </a></li>
                    <li class="disabled"><a href="#"> 2 </a></li>
                    <li><a href="#"> 3 </a></li>
                    <li><a href="#"> 4 </a></li>
                    <li><a href="#"> 5 </a></li>
                    <li><a href="#">&raquo;</a></li>
                </ul>
            </div>
            <aside class="col-md-4 blog-aside">
                <div class="aside-widget">
                    <header>
                        <h3>点击最多</h3>
                    </header>
                    <div class="body">
                        <ul class="clean-list">
                            <li><a href="">关于personal-blog</a></li>
                            <li><a href="">关于personal-blog</a></li>
                            <li><a href="">关于personal-blog</a></li>
                            <li><a href="">关于personal-blog</a></li>
                            <li><a href="">关于personal-blog</a></li>
                        </ul>
                    </div>
                </div>
                <div class="aside-widget">
                    <header>
                        <h3>最新发布</h3>
                    </header>
                    <div class="body">
                        <ul class="clean-list">
                            <li><a href="">SpringBoot</a></li>
                            <li><a href="">SpringBoot</a></li>
                            <li><a href="">SpringBoot</a></li>
                            <li><a href="">SpringBoot</a></li>
                            <li><a href="">SpringBoot</a></li>
                            <li><a href="">SpringBoot</a></li>
                        </ul>
                    </div>
                </div>
                <div class="aside-widget">
                    <header>
                        <h3>标签栏</h3>
                    </header>
                    <div class="body clearfix">
                        <ul class="tags">
                            <li><a href="#">HTML5</a></li>
                            <li><a href="#">CSS3</a></li>
                            <li><a href="#">COMPONENTS</a></li>
                            <li><a href="#">TEMPLATE</a></li>
                            <li><a href="#">PLUGIN</a></li>
                            <li><a href="#">BOOTSTRAP</a></li>
                            <li><a href="#">TUTORIAL</a></li>
                            <li><a href="#">UI/UX</a></li>
                        </ul>
                    </div>
                </div>
            </aside>
        </div>
    </div>
</div>
<footer>
    <div class="widewrapper footer">
        <div class="container">
            <div class="row">
                <div class="col-md-4 footer-widget">
                    <h3><i class="fa fa-user"></i>About</h3>
                    <p>your singal blog.</p>
                    <p>have fun.</p>
                </div>
                <div class="col-md-4 footer-widget">
                    <h3><i class="fa fa-tag"></i>备案</h3>
                    <p>粤ICP备 xxxxxx-x号</p>
                </div>
                <div class="col-md-4 footer-widget">
                    <h3><i class="fa fa-copyright"></i>Copy Right</h3>
                    <p>My Blog</p>
                </div>
            </div>
        </div>
    </div>
</footer>
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/modernizr.js"></script>
</body>
</html>
```

#### 首页整合

为了与后端管理系统相区别，新建包和目录。在 resources/templates 目录下新建 blog 目录用于存放博客页面的模板页面,之后并放入 index.html 页面，在 resources/static 目录下新建 blog 目录用于存放博客页面的相关静态资源，之后将前文中提到的静态资源文件都移动到该目录下。

然后打开 index.html 文件并在该模板文件的 标签中导入 Thymeleaf 的名称空间：

```html
<html lang="en" xmlns:th="http://www.thymeleaf.org"></html>
```

导入该名称空间主要是为了 Thymeleaf 的语法提示和 Thymeleaf 标签的使用，接着我们在模板中使用 th 标签来修改静态资源的引用路径，最终的模板文件如下：

```html
<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <title>主页</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
    <!-- Bootstrap styles -->
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <!-- Font-Awesome -->
    <link rel="stylesheet" href="css/font-awesome/css/font-awesome.min.css">
    <!-- Styles -->
    <link rel="stylesheet" href="css/style.css">
    <!--[if lt IE 9]>
    <script src="js/respond.js"></script>
    <![endif]-->
</head>
<body>
<header>
    <div class="widewrapper masthead">
        <div class="container">
            <a href="index.html" id="logo">
                <img src="img/logo.png" class="logo-img" alt="personal-blog">
            </a>
            <div id="mobile-nav-toggle" class="pull-right">
                <a href="#" data-toggle="collapse" data-target=".clean-nav .navbar-collapse">
                    <i class="fa fa-bars"></i>
                </a>
            </div>
            <nav class="pull-right clean-nav">
                <div class="collapse navbar-collapse">
                    <ul class="nav nav-pills navbar-nav">
                        <li>
                            <a href="index.html">主页</a>
                        </li>
                        <li>
                            <a href="##">关于</a>
                        </li>
                        <li>
                            <a href="##">联系我</a>
                        </li>
                    </ul>
                </div>
            </nav>
        </div>
    </div>
    <div class="widewrapper subheader">
        <div class="container">
            <div class="clean-breadcrumb">
                <a href="#">首页</a>
            </div>
            <div class="clean-searchbox">
                <form action="#" method="get" accept-charset="utf-8">
                    <input class="searchfield" id="searchbox" type="text" placeholder="搜索">
                    <button class="searchbutton" type="submit">
                        <i class="fa fa-search"></i>
                    </button>
                </form>
            </div>
        </div>
    </div>
</header>
<div class="widewrapper main">
    <div class="container">
        <div class="row">
            <div class="col-md-8 blog-main">
                <div class="col-md-6 col-sm-6 blog-main-card ">
                    <article class=" blog-summary">
                        <header>
                            <img src="img/photo1.png" alt="">
                            <h3><a href="##">第14课：SweetAlert 插件整合及搜索功能实现</a></h3>
                        </header>

                    </article>
                </div>
                <div class="col-md-6 col-sm-6 blog-main-card ">
                    <article class="blog-summary">
                        <header>
                            <img src="img/photo2.png" alt="">
                            <h3><a href="##">第13课：富文本信息管理模块</a></h3>
                        </header>

                    </article>
                </div>

                <div class="col-md-6 col-sm-6 blog-main-card ">
                    <article class="blog-summary">
                        <header>
                            <img src="img/photo1.png" alt="">
                            <h3><a href="##">第12课：文件导入导出功能</a></h3>
                        </header>

                    </article>
                </div>
                <div class="col-md-6 col-sm-6 blog-main-card ">
                    <article class="blog-summary">
                        <header>
                            <img src="img/photo1.png" alt="">
                            <h3><a href="##">第11课：多图上传与大文件分片上传、断点续传</a></h3>
                        </header>

                    </article>
                </div>
                <div class="col-md-6 col-sm-6 blog-main-card ">
                    <article class="blog-summary">
                        <header>
                            <img src="img/photo1.png" alt="">
                            <h3><a href="##">第10课：图片管理模块</a></h3>
                        </header>

                    </article>
                </div>
                <div class="col-md-6 col-sm-6 blog-main-card ">
                    <article class=" blog-summary">
                        <header>
                            <img src="img/photo2.png" alt="">
                            <h3><a href="##">第09课：弹框组件整合——完善添加和修改功能</a></h3>
                        </header>
                    </article>
                </div>
                <ul class="blog-pagination">
                    <li><a href="#">&laquo;</a></li>
                    <li class="active"><a href="#"> 1 </a></li>
                    <li class="disabled"><a href="#"> 2 </a></li>
                    <li><a href="#"> 3 </a></li>
                    <li><a href="#"> 4 </a></li>
                    <li><a href="#"> 5 </a></li>
                    <li><a href="#">&raquo;</a></li>
                </ul>
            </div>
            <aside class="col-md-4 blog-aside">
                <div class="aside-widget">
                    <header>
                        <h3>点击最多</h3>
                    </header>
                    <div class="body">
                        <ul class="clean-list">
                            <li><a href="">关于personal-blog</a></li>
                            <li><a href="">关于personal-blog</a></li>
                            <li><a href="">关于personal-blog</a></li>
                            <li><a href="">关于personal-blog</a></li>
                            <li><a href="">关于personal-blog</a></li>
                        </ul>
                    </div>
                </div>
                <div class="aside-widget">
                    <header>
                        <h3>最新发布</h3>
                    </header>
                    <div class="body">
                        <ul class="clean-list">
                            <li><a href="">SpringBoot</a></li>
                            <li><a href="">SpringBoot</a></li>
                            <li><a href="">SpringBoot</a></li>
                            <li><a href="">SpringBoot</a></li>
                            <li><a href="">SpringBoot</a></li>
                            <li><a href="">SpringBoot</a></li>
                        </ul>
                    </div>
                </div>
                <div class="aside-widget">
                    <header>
                        <h3>标签栏</h3>
                    </header>
                    <div class="body clearfix">
                        <ul class="tags">
                            <li><a href="#">HTML5</a></li>
                            <li><a href="#">CSS3</a></li>
                            <li><a href="#">COMPONENTS</a></li>
                            <li><a href="#">TEMPLATE</a></li>
                            <li><a href="#">PLUGIN</a></li>
                            <li><a href="#">BOOTSTRAP</a></li>
                            <li><a href="#">TUTORIAL</a></li>
                            <li><a href="#">UI/UX</a></li>
                        </ul>
                    </div>
                </div>
            </aside>
        </div>
    </div>
</div>
<footer>
    <div class="widewrapper footer">
        <div class="container">
            <div class="row">
                <div class="col-md-4 footer-widget">
                    <h3><i class="fa fa-user"></i>About</h3>
                    <p>your singal blog.</p>
                    <p>have fun.</p>
                </div>
                <div class="col-md-4 footer-widget">
                    <h3><i class="fa fa-tag"></i>备案</h3>
                    <p>粤ICP备 xxxxxx-x号</p>
                </div>
                <div class="col-md-4 footer-widget">
                    <h3><i class="fa fa-copyright"></i>Copy Right</h3>
                    <p>My Blog</p>
                </div>
            </div>
        </div>
    </div>
</footer>
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/modernizr.js"></script>
</body>
</html>
```

前端文件制作完毕，接下来新建 Controller 来处理首页请求路径并跳转到对应的页面。

#### Controller 处理跳转

首先在 controller 包下新建 blog 包，并新建 MyBlogController.java，之后新增如下代码：

```java
package cn.yuyingwai.springbootblog.controller.blog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyBlogController {

    /**
     * 首页
     * @return
     */
    @GetMapping({"/", "/index", "index.html"})
    public String index() {
        return "blog/index";
    }

}
```

该方法用于处理 "/", "/index", "index.html" 等请求，这种路径的请求一般为首页请求，如果觉得还需要加其他路径的话也可以在 Mapping 配置中加上，方法的最后返回 "blog/index" ，即访问该方法会跳转到 blog 目录下的 index.html 模板文件中。



### 页面抽取

页面顶部和页面底部两个区域在我们本次的实践系统中都是相似的，因此对这两部分进行公共代码抽取减少重复编码，在 resources/templates 目录下新建 header.html 和 footer.html 两个模板文件，代码如下：

#### header.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head th:fragment="head-fragment">
    <meta charset="utf-8" />
    <title th:text="${pageName}">主页</title>
    <meta name="viewport" content="width=device-width" />
    <!-- Bootstrap styles -->
    <link rel="stylesheet" th:href="@{/blog/css/bootstrap.min.css}" />
    <!-- Font-Awesome -->
    <link
            rel="stylesheet"
            th:href="@{/blog/css/font-awesome/css/font-awesome.min.css}"
    />
    <!-- Styles -->
    <link rel="stylesheet" th:href="@{/blog/css/style.css}" id="theme-styles" />
    <!--[if lt IE 9]>
    <script th:src="@{/blog/default/js/respond.js}"></script>
    <![endif]-->
</head>
<header th:fragment="header-fragment">
    <div class="widewrapper masthead">
        <div class="container">
            <a th:href="@{/index}" id="logo">
                <img
                        th:src="@{/blog/img/logo.png}"
                        class="logo-img"
                        alt="my personal blog"
                />
            </a>
            <div id="mobile-nav-toggle" class="pull-right">
                <a
                        href="#"
                        data-toggle="collapse"
                        data-target=".clean-nav .navbar-collapse"
                >
                    <i class="fa fa-bars"></i>
                </a>
            </div>
            <nav class="pull-right clean-nav">
                <div class="collapse navbar-collapse">
                    <ul class="nav nav-pills navbar-nav">
                        <li>
                            <a th:href="@{/index}">主页</a>
                        </li>
                        <li>
                            <a th:href="@{/link}">友链</a>
                        </li>
                        <li>
                            <a th:href="@{/about}">关于</a>
                        </li>
                    </ul>
                </div>
            </nav>
        </div>
    </div>

    <div class="widewrapper subheader">
        <div class="container">
            <div class="clean-breadcrumb">
                <th:block th:text="${pageName}"></th:block>
            </div>
            <div class="clean-searchbox">
                <form method="get" accept-charset="utf-8">
                    <input
                            class="searchfield"
                            id="searchbox"
                            type="text"
                            placeholder="  搜索"
                    />
                    <button class="searchbutton" id="searchbutton">
                        <i class="fa fa-search"></i>
                    </button>
                </form>
            </div>
        </div>
    </div>
</header>
<!-- /.header -->
</html>
```

#### footer.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<footer th:fragment="footer-fragment">
    <div class="widewrapper footer">
        <div class="container">
            <div class="row">
                <div class="col-md-3 footer-widget">
                    <h3><i class="fa fa-user"></i>About</h3>
                    <p>your singal blog.</p>
                </div>
                <div class="col-md-3 footer-widget">
                    <h3><i class="fa fa-info-circle"></i>ICP</h3>
                    <p>粤ICP备 xxxxxx-x号</p>
                </div>
                <div class="col-md-3 footer-widget">
                    <h3><i class="fa fa-copyright"></i>Copy Right</h3>
                    <p>2020 Ray</p>
                </div>
                <div class="col-md-3 footer-widget">
                    <h3><i class="fa fa-arrow-circle-o-up"></i>Powered By</h3>
                    Ray
                </div>
            </div>
        </div>
    </div>
</footer>
</html>
```

#### 修改 index.html

页面的头部区域和底部区域通过 `th:replace` 标签 引入进来，修改代码如下：

```html
<!-- 引入导航栏和搜索栏 -->
<head th:replace="blog/header::head-fragment"></head>
<body>
  <header th:replace="blog/header::header-fragment"></header>

  <!-- 引入页脚footer-fragment -->
  <footer th:replace="blog/footer::footer-fragment"></footer>
</body>
```



## 分页及侧边栏

首页的前端页面编码完成，但是页面上都是静态数据，并没有与后端交互进行实际数据的读取和渲染，本节将会讲解首屏页面上数据的查询，以及如何将这些数据填充到页面中，前面一章更多的偏重于页面制作，本节则是数据填充和功能实现，数据主要有左侧功能栏中的博客统计列表数据、标签栏数据、首页文章列表数据以及分页功能实现。



### 点击最多&最新发布

#### 数据格式定义

首先，把侧边栏中**点击最多**和**最新发布**两个栏目中的数据填充完成，数据填充之前，需要确认一下这两个栏目中的数据格式是怎样的，如下图中第四部分，这里以**点击最多**为例，通过图中的信息可以得出，这是一个博客列表，所以后端肯定会返回一个 List 格式的对象，展示的数据仅仅为博客标题，即**哪些博客是点击量比较高的**，同理，**最新发布**栏目中即为**哪些博客是发布时间较新的**。

![](http://images.yingwai.top/picgo/20201218100935.jpg)

虽然通过图片我们只能看出一个博客标题字段，但是这里通常会设计成可跳转的形式，即点击标题后会跳转到对应的博客详情页面中，因此还需要一个博客实体的 id 字段，因此返回数据的格式就得出来了，编码如下：

```java
package cn.yuyingwai.springbootblog.controller.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SimpleBlogListV0 implements Serializable {

    private Long blogId;

    private String blogTitle;

}
```

#### 数据查询实现

接下来是数据查询的功能实现，上述两种类型的博客列表都是可以通过直接查询 tb_blog 文章表来获取，只不过查询时使用到的字段不同，一个会使用到浏览量字段，一个是使用创建时间字段，实现逻辑如下。

首先，定义 service 方法，业务层代码如下（BlogServiceImpl.java）：

```java
    @Override
    public List<SimpleBlogListVO> getBlogListForIndexPage(int type) {
        List<SimpleBlogListVO> simpleBlogListVOs = new ArrayList<>();
        List<Blog> blogs = blogDao.findBlogListByType(type, 9);
        if (!CollectionUtils.isEmpty(blogs)) {
            for (Blog blog : blogs) {
                SimpleBlogListVO simpleBlogListVO = new SimpleBlogListVO();
                BeanUtils.copyProperties(blog, simpleBlogListVO);
                simpleBlogListVOs.add(simpleBlogListVO);
            }
        }
        return simpleBlogListVOs;
    }
```

定义了 `getBlogListForIndexPage()` 方法并定义 type 参数，type 等于 0 时为查询**点击最多**的博客列表，type 等于 1 时为查询**最新发布**的博客列表，返回的数据格式为 SimpleBlogListVO，方法逻辑实现为：首先根据 type 字段的不同去查询对应的博客列表，但是查询出来的数据类型为 Blog，之后将 Blog 类型的数据转换为 SimpleBlogListVO 并返回即可。

具体的 SQL 语句如下（BlogMapper.xml）：

```xml
    <select id="findBlogListByType" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_blog
        where is_deleted=0 AND blog_status = 1<!-- 发布状态的文章 -->
        <if test="type!=null and type==0">
            order by blog_views desc
        </if>
        <if test="type!=null and type==1">
            order by blog_id desc
        </if>
        limit #{limit}
    </select>
```

#### 数据渲染

想要将数据通过 Thymeleaf 语法渲染到前端页面上，首先需要将数据带过来，需要在首页请求的 Controller 方法中将查询到的数据放入 request 域中，代码修改如下：

```java
    @GetMapping({"/", "/index", "index.html"})
    public String index(HttpServletRequest request) {
        request.setAttribute("newBlogs", blogService.getBlogListForIndexPage(1));
        request.setAttribute("hotBlogs", blogService.getBlogListForIndexPage(0));
        request.setAttribute("pageName", "首页");
        return "blog/index";
    }
```

分别查出最新发布的博客列表和点击最多的博客列表并放入到 request 对象中，分别取名为 newBlogs 和 hotBlogs，之后跳转到 index 模板页面进行数据渲染。

index.html 文件修改如下：

```html
<div class="aside-widget">
    <header>
        <h3>点击最多</h3>
    </header>
    <div class="body">
        <ul class="clean-list">
            <th:block th:if="${null != hotBlogs}">
                <th:block th:each="hotBlog : ${hotBlogs}">
                    <li>
                        <a>
                            <th:block th:text="${hotBlog.blogTitle}"></th:block>
                        </a>
                    </li>
                </th:block>
            </th:block>
        </ul>
    </div>
</div>
<div class="aside-widget">
    <header>
        <h3>最新发布</h3>
    </header>
    <div class="body">
        <ul class="clean-list">
            <th:block th:if="${null != newBlogs}">
                <th:block th:each="newBlog : ${newBlogs}">
                    <li>
                        <a>
                            <th:block th:text="${newBlog.blogTitle}"></th:block>
                        </a>
                    </li>
                </th:block>
            </th:block>
        </ul>
    </div>
</div>
```

在**点击最多**栏目和**最新发布**栏目对应的位置读取 hotBlogs 和 newBlogs，并使用 `th:each` 循环语法将标题渲染出来。



### 标签栏

#### 数据格式定义

数据填充之前，需要确认一下数据格式是怎样的，如下图中第五部分，通过图中的信息可以得出，这是一个标签名称的列表，展示的数据为标签的名称，但是这样的话数据有些单薄，所以在这个原型图的基础上又加上了每个标签对应的有多少篇文章在使用，因此这里显示的会是标签的名称和对应的博客数量两个字段。

![](http://images.yingwai.top/picgo/20201218100946.jpg)

当然这里通常也会设计成可跳转的形式，即点击标签后会跳转到对应的该标签下的博客列表中，因此还需要一个标签的主键字段，因此返回数据的格式就得出来了，编码如下：

```java
package cn.yuyingwai.springbootblog.entity;

import lombok.Data;

@Data
public class BlogTagCount {
    
    private Integer tagId;
    
    private String tagName;
    
    private Integer tagCount;
    
}
```

#### 数据查询实现

通过前文中的数据格式定义也大致的清楚了我们需要查询的是什么数据，但是也不可能把所有的标签数据都查出来，因为数据量太大的话全部显示在页面会有些怪，所以标签栏的数据设计成了查询当前使用最多的 20 个标签数据，这个数据的获取会比较复杂，因为标签和文章会涉及到三张表的操作：tb_blog 表、tb_blog_tag 表以及 tb_blog_tag_relation 表。

首先，定义 service 方法，业务层代码如下（TagServiceImpl.java）：

```java
    @Override
    public List<BlogTagCount> getBlogTagCountForIndex() {
        return blogTagDao.getTagCount();
    }
```

定义了 `getBlogTagCountForIndex()` 方法，实现逻辑是直接返回 `blogTagMapper.getTagCount()` 执行后的返回数据，该方法其实并没有做什么逻辑，实现的难点都在 SQL 语句。

具体的 SQL 语句如下（BlogTagMapper.xml）：

首先在 Mapper 文件中添加一个 ResultMap，代码如下：

```xaml
    <resultMap id="BaseCountResultMap" type="cn.yuyingwai.springbootblog.entity.BlogTagCount">
        <id column="tag_id" jdbcType="INTEGER" property="tagId"/>
        <result column="tag_count" jdbcType="INTEGER" property="tagCount"/>
        <result column="tag_name" jdbcType="VARCHAR" property="tagName"/>
    </resultMap>
```

之后是 `getTagCount()` 方法的 SQL 具体实现，代码如下：

```xml
    <select id="getTagCount" resultMap="BaseCountResultMap">
        SELECT t_r.*,t.tag_name FROM
            (SELECT r.tag_id,r.tag_count FROM
                (SELECT tag_id ,COUNT(*) AS tag_count FROM
                    (SELECT tr.tag_id FROM tb_blog_tag_relation tr LEFT JOIN tb_blog b ON tr.blog_id = b.blog_id WHERE b.is_deleted=0)
                        trb GROUP BY tag_id) r ORDER BY tag_count DESC LIMIT 20 ) AS t_r LEFT JOIN tb_blog_tag t ON t_r.tag_id = t.tag_id WHERE t.is_deleted=0
    </select>
```

以上就是查询当前使用最多的 20 个标签数据的 SQL 语句，用到了连接查询以及聚合方法，看起来有些复杂，查询的层级也深。

如何理解该 SQL：

* **第一层 SQL：**

```sql
SELECT tr.tag_id FROM tb_blog_tag_relation tr LEFT JOIN tb_blog b ON tr.blog_id = b.blog_id WHERE b.is_deleted=0
```

tb_blog_tag_relation 表和 tb_blog 表通过 blog_id 字段进行左连接查询，主要是为了过滤掉已删除博客记录的关联数据。

- **第二层 SQL：**

```sql
SELECT tag_id ,COUNT(*) AS tag_count FROM
          (SELECT tr.tag_id FROM tb_blog_tag_relation tr LEFT JOIN tb_blog b ON tr.blog_id = b.blog_id WHERE b.is_deleted=0) trb GROUP BY tag_id
```

直接根据第一层查询后的数据进行操作，并使用 GROUP BY tag_id 来进行数量统计，这一层 SQL 执行后返回的数据是标签的主键 tag_id 以及该主键下共有多少条关系数据。

- **第三层 SQL：**

```sql
SELECT r.tag_id,r.tag_count FROM
         (SELECT tag_id ,COUNT(*) AS tag_count FROM
          (SELECT tr.tag_id FROM tb_blog_tag_relation tr LEFT JOIN tb_blog b ON tr.blog_id = b.blog_id WHERE b.is_deleted=0)
            trb GROUP BY tag_id) r ORDER BY tag_count DESC LIMIT 20
```

直接根据第二层查询后的数据进行操作，主要是根据 tag_count 进行排序同时取出数量最多的 20 条数据。

* **第四层 SQL：**

即前文中写在 Mapper 文件中的 SQL 语句，这一层主要是对第三层查询后的数据与 tb_blog_tag 标签表做连接查询，把前一步查询出的 20 条记录的标签名称查出。

#### 数据渲染

首先要在首页请求的 Controller 方法中将查询到的数据放入 request 域中，代码修改如下：

```java
    @GetMapping({"/", "/index", "index.html"})
    public String index(HttpServletRequest request) {
        request.setAttribute("newBlogs", blogService.getBlogListForIndexPage(1));
        request.setAttribute("hotBlogs", blogService.getBlogListForIndexPage(0));
        request.setAttribute("hotTags", tagService.getBlogTagCountForIndex());
        request.setAttribute("pageName", "首页");
        return "blog/index";
    }
```

查出标签统计数据并放入到 request 对象中，对象命名为 hotTags，之后跳转到 index 模板页面进行数据渲染。

index.html 文件修改如下：

```html
<div class="aside-widget">
    <header>
        <h3>标签栏</h3>
    </header>
    <div class="body clearfix">
        <ul class="tags">
            <th:block th:if="${null != hotTags}">
                <th:block th:each="hotTag : ${hotTags}">
                    <li>
                        <a>
                            <th:block
                                      th:text="${hotTag.tagName}+'('+${hotTag.tagCount}+')'"
                                      ></th:block>
                        </a>
                    </li>
                </th:block>
            </th:block>
        </ul>
    </div>
</div>
```

在**标签栏**栏目对应的位置读取 hotTags 对象，并使用 `th:each` 循环语法将表情的名称和数量渲染出来。



### 博客列表

#### 数据格式定义

接下来是**首页博客列表和分页功能实现**，这两部分需要填充的数据就是博客列表和分页按钮以及跳转逻辑，分页按钮中并没有需要定义的数据格式，只是把页码放上去即可，主要确认一下博客列表中的数据格式是怎样的，如下图所示，即为首页博客列表中需要渲染的内容。

![](http://images.yingwai.top/picgo/20201218115529.jpg)

首先博客列表肯定是一个 List 对象，但是因为有分页功能，所以还需要返回分页字段，因此最终接收到的结果返回格式为 PageResult 对象，而列表中的单项对象中的字段则需要通过下图中的内容进行确认。

通过图片可以看到博客标题字段、预览图字段、分类名称字段、分类图片字段，当然这里通常会设计成可跳转的形式，即点击标题或者预览图后会跳转到对应的博客详情页面中，点击分类名称或者分类图片也会跳转到对应的博客分类页面，因此还需要一个博客实体的 id 字段和分类实体的 id 字段，因此返回数据的格式就得出来了，编码如下：

```java
package cn.yuyingwai.springbootblog.controller.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BlogListVO implements Serializable {
    
    private Long blogId;
    
    private String blogTitle;
    
    private String blogCoverImage;
    
    private Integer blogCategoryId;
    
    private String blogCategoryIcon;
    
    private String blogCategoryName;
    
}
```

#### 数据查询实现

接下来是数据查询的功能实现，上述博客列表中的字段可以通过直接查询 tb_blog 文章表和 tb_blog_category 表来获取，同时需要注意分页功能实现，传参时也需要传上此时的页码，首页默认是第 1 页，实现逻辑如下。

首先，定义 service 方法，业务层代码如下：

```java
    @Override
    public PageResult getBlogsForIndexPage(int page) {
        Map params = new HashMap();
        params.put("page", page);
        // 每页8条
        params.put("limit", 8);
        params.put("blogStatus", 1);    // 过滤发布状态下的数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        List<Blog> blogList = blogDao.findBlogList(pageUtil);
        List<BlogListVO> blogListVOS = getBlogListVOsByBlogs(blogList);
        int total = blogDao.getTotalBlogs(pageUtil);
        PageResult pageResult = new PageResult(blogListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    /**
     * 数据填充
     * @param blogList
     * @return
     */
    private List<BlogListVO> getBlogListVOsByBlogs(List<Blog> blogList) {
        List<BlogListVO> blogListVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(blogList)) {
            List<Integer> categoryIds = blogList.stream().map(Blog::getBlogCategoryId).collect(Collectors.toList());
            Map<Integer, String> blogCategoryMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(categoryIds)) {
                List<BlogCategory> blogCategories = categoryDao.selectByCategoryIds(categoryIds);
                if (!CollectionUtils.isEmpty(blogCategories)) {
                    blogCategoryMap = blogCategories.stream()
                            .collect(Collectors.toMap(BlogCategory::getCategoryId, BlogCategory::getCategoryIcon,
                                    (key1, key2) -> key2));
                }
            }
            for (Blog blog : blogList) {
                BlogListVO blogListVO = new BlogListVO();
                BeanUtils.copyProperties(blog, blogListVO);
                if (blogCategoryMap.containsKey(blog.getBlogCategoryId())) {
                    blogListVO.setBlogCategoryIcon(blogCategoryMap.get(blog.getBlogCategoryId()));
                } else {
                    blogListVO.setBlogCategoryId(0);
                    blogListVO.setBlogCategoryName("默认分类");
                    blogListVO.setBlogCategoryIcon("/admin/dist/img/category/1.png");
                }
                blogListVOS.add(blogListVO);
            }
        }
        return blogListVOS;
    }
```

定义了 `getBlogsForIndexPage()` 方法并定义 page 参数来确定查询第几页的数据，之后通过 SQL 查询出对应的分页数据，再之后是填充数据，某些字段是 tb_blog 表中没有的，所以再去分类表中查询并设置到 BlogListVO 对象中，最终返回的数据是 PageResult 对象。

具体的 SQL 语句如下（BlogMapper.xml）：

```xml
    <select id="findBlogList" parameterType="Map" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from tb_blog
        where is_deleted=0
        <if test="blogStatus!=null">
            AND blog_status = #{blogStatus}
        </if>
        order by blog_id desc
        <if test="start!=null and limit!=null">
            limit #{start},#{limit}
        </if>
    </select>

    <select id="getTotalBlogs" parameterType="Map" resultType="int">
        select count(*) from tb_blog
        where is_deleted=0
        <if test="blogStatus!=null">
            AND blog_status = #{blogStatus}
        </if>
    </select>
```

在原来查询博客列表 SQL 的基础上增加了对于 blog_status 字段的过滤，因为首页肯定显示已发布状态的博客。

#### 数据渲染

想要将数据通过 Thymeleaf 语法渲染到前端页面上，首先需要将数据带过来，需要在首页请求的 Controller 方法中将查询到的数据放入 request 域中，因为有分页逻辑，所以需要对原首页跳转方法进行修改，代码修改如下：

```java
    /**
     * 首页（取第一页数据）
     * @return
     */
    @GetMapping({"/", "/index", "index.html"})
    public String index(HttpServletRequest request) {
        return this.page(request, 1);
    }

    /**
     * 首页 分页数据
     * @param request
     * @param pageNum
     * @return
     */
	@GetMapping({"/page/{pageNum}"})
    public String page(HttpServletRequest request, @PathVariable("pageNum") int pageNum) {
        PageResult blogPageResult = blogService.getBlogsForIndexPage(pageNum);
        if (blogPageResult == null) {
            return "error/error_404";
        }
        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("newBlogs", blogService.getBlogListForIndexPage(1));
        request.setAttribute("hotBlogs", blogService.getBlogListForIndexPage(0));
        request.setAttribute("hotTags", tagService.getBlogTagCountForIndex());
        request.setAttribute("pageName", "首页");
        return "blog/index";
    }
```

添加了 `page()` 方法用于处理分页逻辑，原来的逻辑处理都放在了该方法中，首页方法是第 1 页的数据，所以默认调用 `page()` 方法的第 1 页即可，根据页码查询出对应的分页数据 blogPageResult 并放入到 request 对象中，之后跳转到 index 模板页面进行数据渲染。

index.html 文件修改如下：

```html
<div class="col-md-8 blog-main">
    <th:block th:if="${null != blogPageResult}">
        <th:block th:each="blog,iterStat : ${blogPageResult.list}">
            <div class="col-md-6 col-sm-6 blog-main-card">
                <article class="blog-summary">
                    <header>
                        <a>
                            <img th:src="@{${blog.blogCoverImage}}" alt="">
                            <h3>
                                <th:block th:text="${blog.blogTitle}"></th:block>
                            </h3>
                        </a>
                        <div class="blog-category">
                            <a>
                                <div class="blog-category-icon">
                                    <img th:src="@{${blog.blogCategoryIcon}}" alt="">
                                </div>
                                <div class="blog-category" th:utext="${blog.blogCategoryName}">
                                </div>
                            </a>
                        </div>
                    </header>
                </article>
            </div>
            <th:block th:if="${iterStat.last and iterStat.count%2==1}">
                <div class="col-md-6 col-sm-6 blog-main-card">
                </div>
            </th:block>
        </th:block>
    </th:block>
    <th:block th:if="${null != blogPageResult}">
        <ul class="blog-pagination">
            <li th:class="${blogPageResult.currPage==1}?'disabled' : ''"><a
                                                                            th:href="@{${blogPageResult.currPage==1}?'##':'/page/' + ${blogPageResult.currPage-1}}">&laquo;</a>
            </li>
            <li th:if="${blogPageResult.currPage-3 >=1}"><a
                                                            th:href="@{'/page/' + ${blogPageResult.currPage-3}}"
                                                            th:text="${blogPageResult.currPage -3}">1</a></li>
            <li th:if="${blogPageResult.currPage-2 >=1}"><a
                                                            th:href="@{'/page/' + ${blogPageResult.currPage-2}}"
                                                            th:text="${blogPageResult.currPage -2}">1</a></li>
            <li th:if="${blogPageResult.currPage-1 >=1}"><a
                                                            th:href="@{'/page/' + ${blogPageResult.currPage-1}}"
                                                            th:text="${blogPageResult.currPage -1}">1</a></li>
            <li class="active"><a href="#" th:text="${blogPageResult.currPage}">1</a></li>
            <li th:if="${blogPageResult.currPage+1 <=blogPageResult.totalPage}"><a
                                                                                   th:href="@{'/page/' + ${blogPageResult.currPage+1}}"
                                                                                   th:text="${blogPageResult.currPage +1}">1</a></li>
            <li th:if="${blogPageResult.currPage+2 <=blogPageResult.totalPage}"><a
                                                                                   th:href="@{'/page/' + ${blogPageResult.currPage+2}}"
                                                                                   th:text="${blogPageResult.currPage +2}">1</a></li>
            <li th:if="${blogPageResult.currPage+3 <=blogPageResult.totalPage}"><a
                                                                                   th:href="@{'/page/' + ${blogPageResult.currPage+3}}"
                                                                                   th:text="${blogPageResult.currPage +3}">1</a></li>
            <li th:class="${blogPageResult.currPage==blogPageResult.totalPage}?'disabled' : ''"><a
                                                                                                   th:href="@{${blogPageResult.currPage==blogPageResult.totalPage}?'##' : '/page/' + ${blogPageResult.currPage+1}}">&raquo;</a>
            </li>
        </ul>
    </th:block>
</div>
```

在博客列表区域和分页功能区域对应的位置读取 blogPageResult 对象中的 list 数据和分页数据，list 数据为博客列表数据，使用 `th:each` 循环语法将标题、预览图、分类数据渲染出来，博客详情页面还没做，a 标签中的详情跳转链接会在博客详情页面功能完善后补上，之后根据分页字段 currPage(当前页码)和 totalPage(总页码)将下方的分页按钮渲染出来。



## 搜索页面制作