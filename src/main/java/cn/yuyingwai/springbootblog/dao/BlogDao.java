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
