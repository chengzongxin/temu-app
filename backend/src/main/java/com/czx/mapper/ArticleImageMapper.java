package com.czx.mapper;

import com.czx.pojo.ArticleImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ArticleImageMapper {
    
    // 批量插入文章图片
    int batchInsertImages(@Param("images") List<ArticleImage> images);
    
    // 根据文章ID获取图片
    List<ArticleImage> getImagesByArticleId(@Param("articleId") Integer articleId);
    
    // 删除文章的所有图片
    int deleteImagesByArticleId(@Param("articleId") Integer articleId);
    
    // 删除单张图片
    int deleteImage(@Param("id") Integer id);
}
