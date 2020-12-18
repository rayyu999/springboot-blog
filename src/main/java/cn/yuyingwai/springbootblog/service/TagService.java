package cn.yuyingwai.springbootblog.service;

import cn.yuyingwai.springbootblog.entity.BlogTagCount;
import cn.yuyingwai.springbootblog.util.PageQueryUtil;
import cn.yuyingwai.springbootblog.util.PageResult;

import java.util.List;

public interface TagService {

    /**
     * 查询标签的分页数据
     * @param pageUtil
     * @return
     */
    PageResult getBlogTagPage(PageQueryUtil pageUtil);

    Boolean saveTag(String tagName);

    Boolean deleteBatch(Integer[] ids);

    public List<BlogTagCount> getBlogTagCountForIndex();

}
