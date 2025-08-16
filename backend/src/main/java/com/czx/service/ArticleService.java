package com.czx.service;

import com.czx.pojo.Article;
import com.czx.pojo.ArticleSubmitRequest;
import com.czx.pojo.ReviewRequest;
import java.util.List;
import java.util.Map;

public interface ArticleService {
    
    // 提交文章
    boolean submitArticle(ArticleSubmitRequest request, Integer authorId);
    
    // 获取文章列表
    Map<String, Object> getArticleList(int page, int pageSize, Integer status, Integer authorId);
    
    // 获取文章详情
    Article getArticleById(Integer id);
    
    // 更新文章
    boolean updateArticle(Integer id, String title, String content, Integer authorId);
    
    // 删除文章
    boolean deleteArticle(Integer id, Integer authorId);
    
    // 审核文章
    boolean reviewArticle(Integer id, ReviewRequest request, Integer reviewerId);
    
    // 获取待审核文章列表
    List<Article> getPendingReviewArticles();
    
    // 获取我的文章列表
    List<Article> getMyArticles(Integer authorId);
}
