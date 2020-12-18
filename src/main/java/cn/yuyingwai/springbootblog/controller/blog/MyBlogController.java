package cn.yuyingwai.springbootblog.controller.blog;

import cn.yuyingwai.springbootblog.service.BlogService;
import cn.yuyingwai.springbootblog.service.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MyBlogController {

    @Resource
    private BlogService blogService;
    @Resource
    private TagService tagService;

    /**
     * 首页
     * @return
     */
    @GetMapping({"/", "/index", "index.html"})
    public String index(HttpServletRequest request) {
        request.setAttribute("newBlogs", blogService.getBlogListForIndexPage(1));
        request.setAttribute("hotBlogs", blogService.getBlogListForIndexPage(0));
        request.setAttribute("hotTags", tagService.getBlogTagCountForIndex());
        request.setAttribute("pageName", "首页");
        return "blog/index";
    }

}
