package cn.yuyingwai.springbootblog.service.impl;

import cn.yuyingwai.springbootblog.dao.BlogCommentDao;
import cn.yuyingwai.springbootblog.entity.BlogComment;
import cn.yuyingwai.springbootblog.service.CommentService;
import cn.yuyingwai.springbootblog.util.PageQueryUtil;
import cn.yuyingwai.springbootblog.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private BlogCommentDao blogCommentDao;

    @Override
    public Boolean addComment(BlogComment blogComment) {
        return blogCommentDao.insertSelective(blogComment) > 0;
    }

    @Override
    public PageResult getCommentsPage(PageQueryUtil pageUtil) {
        List<BlogComment> comments = blogCommentDao.findBlogCommentList(pageUtil);
        int total = blogCommentDao.getTotalBlogComments(pageUtil);
        PageResult pageResult = new PageResult(comments, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public int getTotalComments() {
        return 0;
    }

    @Override
    public Boolean checkDone(Integer[] ids) {
        return blogCommentDao.checkDone(ids) > 0;
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        return blogCommentDao.deleteBatch(ids) > 0;
    }

    @Override
    public Boolean reply(Long commentId, String replyBody) {
        BlogComment blogComment = blogCommentDao.selectByPrimaryKey(commentId);
        // blogComment不为空且状态为已审核，则继续后续操作
        if (blogComment != null && blogComment.getCommentStatus().intValue() == 1) {
            blogComment.setReplyBody(replyBody);
            blogComment.setReplyCreateTime(new Date());
            return blogCommentDao.updateByPrimaryKeySelective(blogComment) > 0;
        }
        return false;
    }

    @Override
    public PageResult getCommentPageByBlogIdAndPageNum(Long blogId, int page) {
        if (page < 1) {
            return null;
        }
        Map params = new HashMap();
        params.put("page", page);
        // 每页8条
        params.put("limit", 8);
        params.put("blogId", blogId);   // 过滤当前博客下的评论数据
        params.put("commentStatus", 1); // 过滤审核通过的数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        List<BlogComment> comments = blogCommentDao.findBlogCommentList(pageUtil);
        if (!CollectionUtils.isEmpty(comments)) {
            int total = blogCommentDao.getTotalBlogComments(pageUtil);
            PageResult pageResult = new PageResult(comments, total, pageUtil.getLimit(), pageUtil.getPage());
            return pageResult;
        }
        return null;
    }
}
