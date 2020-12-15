package cn.yuyingwai.springbootblog.dao;

import cn.yuyingwai.springbootblog.entity.BlogTag;
import cn.yuyingwai.springbootblog.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
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

    int batchInsertBlogTag(List<BlogTag> tagList);

}
