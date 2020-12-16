package cn.yuyingwai.springbootblog.service;

import cn.yuyingwai.springbootblog.entity.Blog;
import cn.yuyingwai.springbootblog.util.PageQueryUtil;
import cn.yuyingwai.springbootblog.util.PageResult;

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

}
