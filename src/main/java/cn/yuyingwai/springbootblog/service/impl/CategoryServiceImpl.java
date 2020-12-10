package cn.yuyingwai.springbootblog.service.impl;

import cn.yuyingwai.springbootblog.dao.BlogCategoryDao;
import cn.yuyingwai.springbootblog.entity.BlogCategory;
import cn.yuyingwai.springbootblog.service.CategoryService;
import cn.yuyingwai.springbootblog.util.PageQueryUtil;
import cn.yuyingwai.springbootblog.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    public List<BlogCategory> getAllCategories() {
        return blogCategoryDao.findCategoryList(null);
    }

    @Override
    public BlogCategory selectById(Integer id) {
        return blogCategoryDao.selectByPrimaryKey(id);
    }

}
