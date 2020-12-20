package cn.yuyingwai.springbootblog.service;

import cn.yuyingwai.springbootblog.controller.vo.SimpleBlogListVO;
import cn.yuyingwai.springbootblog.entity.Blog;
import cn.yuyingwai.springbootblog.util.PageQueryUtil;
import cn.yuyingwai.springbootblog.util.PageResult;

import java.util.List;

public interface BlogService {

    String saveBlog(Blog blog);

    /**
     * 根据id获取详情
     * @param blogId
     * @return
     */
    Blog getBlogById(Long blogId);

    /**
     * 后台修改
     * @param blog
     * @return
     */
    String updateBlog(Blog blog);

    PageResult getBlogsPage(PageQueryUtil pageUtil);

    Boolean deleteBatch(Integer[] ids);

    /**
     * 首页侧边栏数据列表
     * 0-点击最多 1-最新发布
     * @param type
     * @return
     */
    List<SimpleBlogListVO> getBlogListForIndexPage(int type);

    /**
     * 获取首页文章列表
     * @param page
     * @return
     */
    PageResult getBlogsForIndexPage(int page);

    PageResult getBlogsPageBySearch(String keyword, int page);

    PageResult getBlogsPageByCategory(String categoryName, int page);

}
