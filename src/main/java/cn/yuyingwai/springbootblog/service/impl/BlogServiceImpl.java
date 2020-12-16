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
import cn.yuyingwai.springbootblog.util.PageQueryUtil;
import cn.yuyingwai.springbootblog.util.PageResult;
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

    @Override
    public Blog getBlogById(Long blogId) {
        return null;
    }

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

    @Override
    public PageResult getBlogsPage(PageQueryUtil pageUtil) {
        return null;
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        return null;
    }

}
