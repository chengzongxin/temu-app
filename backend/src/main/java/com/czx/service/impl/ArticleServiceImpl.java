package com.czx.service.impl;

import com.czx.mapper.ArticleMapper;
import com.czx.mapper.ArticleImageMapper;
import com.czx.pojo.Article;
import com.czx.pojo.ArticleImage;
import com.czx.pojo.ArticleSubmitRequest;
import com.czx.pojo.ReviewRequest;
import com.czx.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class ArticleServiceImpl implements ArticleService {
    
    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private ArticleImageMapper articleImageMapper;
    
    @Override
    @Transactional
    public boolean submitArticle(ArticleSubmitRequest request, Integer authorId) {
        try {
            // 创建文章
            Article article = new Article();
            article.setTitle(request.getTitle());
            article.setContent(request.getContent());
            article.setAuthorId(authorId);
            article.setStatus(Article.STATUS_SUBMITTED);
            article.setSubmittedAt(LocalDateTime.now());
            
            articleMapper.createArticle(article);
            
            // 保存图片关联
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                List<ArticleImage> images = new ArrayList<>();
                for (ArticleSubmitRequest.ArticleImageRequest imgRequest : request.getImages()) {
                    ArticleImage image = new ArticleImage();
                    image.setArticleId(article.getId());
                    image.setImageId(imgRequest.getId());
                    image.setSortOrder(imgRequest.getSortOrder());
                    images.add(image);
                }
                articleImageMapper.batchInsertImages(images);
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getArticleList(int page, int pageSize, Integer status, Integer authorId) {
        Map<String, Object> result = new HashMap<>();
        
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 获取文章列表
        List<Article> articles = articleMapper.getArticleList(offset, pageSize, status, authorId);
        
        // 获取总数
        int total = articleMapper.getArticleCount(status, authorId);
        
        // 为每篇文章加载图片
        for (Article article : articles) {
            List<ArticleImage> images = articleImageMapper.getImagesByArticleId(article.getId());
            article.setImages(images);
        }
        
        result.put("articles", articles);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    @Override
    public Article getArticleById(Integer id) {
        Article article = articleMapper.getArticleById(id);
        if (article != null) {
            // 加载图片
            List<ArticleImage> images = articleImageMapper.getImagesByArticleId(id);
            article.setImages(images);
        }
        return article;
    }
    
    @Override
    @Transactional
    public boolean updateArticle(Integer id, String title, String content, Integer authorId) {
        try {
            Article article = new Article();
            article.setId(id);
            article.setTitle(title);
            article.setContent(content);
            article.setAuthorId(authorId);
            
            return articleMapper.updateArticle(article) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean deleteArticle(Integer id, Integer authorId) {
        try {
            // 删除图片关联
            articleImageMapper.deleteImagesByArticleId(id);
            
            // 删除文章
            return articleMapper.deleteArticle(id) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean reviewArticle(Integer id, ReviewRequest request, Integer reviewerId) {
        try {
            return articleMapper.updateArticleStatus(id, request.getStatus(), reviewerId, request.getReviewComment()) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Article> getPendingReviewArticles() {
        List<Article> articles = articleMapper.getArticleList(0, 1000, Article.STATUS_SUBMITTED, null);
        
        // 为每篇文章加载图片
        for (Article article : articles) {
            List<ArticleImage> images = articleImageMapper.getImagesByArticleId(article.getId());
            article.setImages(images);
        }
        
        return articles;
    }
    
    @Override
    public List<Article> getMyArticles(Integer authorId) {
        List<Article> articles = articleMapper.getArticleList(0, 1000, null, authorId);
        
        // 为每篇文章加载图片
        for (Article article : articles) {
            List<ArticleImage> images = articleImageMapper.getImagesByArticleId(article.getId());
            article.setImages(images);
        }
        
        return articles;
    }
}
