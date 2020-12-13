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
