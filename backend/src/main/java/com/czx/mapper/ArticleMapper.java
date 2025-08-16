package com.czx.mapper;

import com.czx.pojo.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ArticleMapper {
    
    // 创建文章
    int createArticle(Article article);
    
    // 更新文章
    int updateArticle(Article article);
    
    // 根据ID获取文章
    Article getArticleById(@Param("id") Integer id);
    
    // 获取文章列表（分页）
    List<Article> getArticleList(@Param("page") int page, @Param("pageSize") int pageSize, 
                                 @Param("status") Integer status, @Param("authorId") Integer authorId);
    
    // 获取文章总数
    int getArticleCount(@Param("status") Integer status, @Param("authorId") Integer authorId);
    
    // 删除文章
    int deleteArticle(@Param("id") Integer id);
    
    // 更新文章状态
    int updateArticleStatus(@Param("id") Integer id, @Param("status") Integer status, 
                           @Param("reviewedBy") Integer reviewedBy, @Param("reviewComment") String reviewComment);
}
