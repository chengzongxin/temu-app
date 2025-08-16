package com.czx.controller;

import com.czx.pojo.Result;
import com.czx.pojo.Article;
import com.czx.pojo.ArticleSubmitRequest;
import com.czx.pojo.ReviewRequest;
import com.czx.service.ArticleService;
import com.czx.service.UserRoleService;
import com.czx.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private UserRoleService userRoleService;
    
    // 学生提交文章
    @PostMapping("/submit")
    public Result submitArticle(@RequestBody ArticleSubmitRequest request, HttpServletRequest httpRequest) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(httpRequest)) {
                return Result.error("用户未认证");
            }
            
            Integer userId = RequestUtils.getUserId(httpRequest);
            if (userId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有学生角色
            if (!userRoleService.hasRole(userId, "STUDENT")) {
                return Result.error("只有学生可以提交文章");
            }
            
            // 提交文章
            boolean success = articleService.submitArticle(request, userId);
            if (success) {
                return Result.success("文章提交成功");
            } else {
                return Result.error("文章提交失败");
            }
            
        } catch (Exception e) {
            return Result.error("提交失败: " + e.getMessage());
        }
    }
    
    // 获取文章列表（根据用户角色过滤）
    @GetMapping("/list")
    public Result getArticleList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer authorId,
            HttpServletRequest request) {
        
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return Result.error("用户未认证");
            }
            
            Integer userId = RequestUtils.getUserId(request);
            if (userId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 如果用户是学生，只能查看自己的文章
            if (userRoleService.hasRole(userId, "STUDENT")) {
                authorId = userId;
            }
            
            // 如果用户是审核员，可以查看所有文章
            if (userRoleService.hasRole(userId, "REVIEWER")) {
                // 审核员可以查看所有文章
            }
            
            Map<String, Object> result = articleService.getArticleList(page, pageSize, status, authorId);
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("获取文章列表失败: " + e.getMessage());
        }
    }
    
    // 获取文章详情
    @GetMapping("/{id}")
    public Result getArticleDetail(@PathVariable Integer id, HttpServletRequest request) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return Result.error("用户未认证");
            }
            
            Article article = articleService.getArticleById(id);
            if (article == null) {
                return Result.error("文章不存在");
            }
            
            return Result.success(article);
            
        } catch (Exception e) {
            return Result.error("获取文章详情失败: " + e.getMessage());
        }
    }
    
    // 审核员审核文章
    @PostMapping("/{id}/review")
    public Result reviewArticle(@PathVariable Integer id, @RequestBody ReviewRequest request, HttpServletRequest httpRequest) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(httpRequest)) {
                return Result.error("用户未认证");
            }
            
            Integer userId = RequestUtils.getUserId(httpRequest);
            if (userId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有审核员角色
            if (!userRoleService.hasRole(userId, "REVIEWER")) {
                return Result.error("只有审核员可以审核文章");
            }
            
            // 审核文章
            boolean success = articleService.reviewArticle(id, request, userId);
            if (success) {
                return Result.success("审核完成");
            } else {
                return Result.error("审核失败");
            }
            
        } catch (Exception e) {
            return Result.error("审核失败: " + e.getMessage());
        }
    }
    
    // 学生编辑文章
    @PutMapping("/{id}")
    public Result updateArticle(@PathVariable Integer id, @RequestBody ArticleSubmitRequest request, HttpServletRequest httpRequest) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(httpRequest)) {
                return Result.error("用户未认证");
            }
            
            Integer userId = RequestUtils.getUserId(httpRequest);
            if (userId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有学生角色
            if (!userRoleService.hasRole(userId, "STUDENT")) {
                return Result.error("只有学生可以编辑文章");
            }
            
            // 更新文章
            boolean success = articleService.updateArticle(id, request.getTitle(), request.getContent(), userId);
            if (success) {
                return Result.success("文章更新成功");
            } else {
                return Result.error("文章更新失败");
            }
            
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    // 删除文章
    @DeleteMapping("/{id}")
    public Result deleteArticle(@PathVariable Integer id, HttpServletRequest httpRequest) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(httpRequest)) {
                return Result.error("用户未认证");
            }
            
            Integer userId = RequestUtils.getUserId(httpRequest);
            if (userId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有学生角色
            if (!userRoleService.hasRole(userId, "STUDENT")) {
                return Result.error("只有学生可以删除文章");
            }
            
            // 删除文章
            boolean success = articleService.deleteArticle(id, userId);
            if (success) {
                return Result.success("文章删除成功");
            } else {
                return Result.error("文章删除失败");
            }
            
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
    
    // 获取待审核文章列表（审核员专用）
    @GetMapping("/pending-review")
    public Result getPendingReviewArticles(HttpServletRequest request) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return Result.error("用户未认证");
            }
            
            Integer userId = RequestUtils.getUserId(request);
            if (userId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有审核员角色
            if (!userRoleService.hasRole(userId, "REVIEWER")) {
                return Result.error("只有审核员可以查看待审核文章");
            }
            
            var articles = articleService.getPendingReviewArticles();
            return Result.success(articles);
            
        } catch (Exception e) {
            return Result.error("获取待审核文章失败: " + e.getMessage());
        }
    }
    
    // 获取我的文章列表（学生专用）
    @GetMapping("/my")
    public Result getMyArticles(HttpServletRequest request) {
        try {
            // 检查用户是否已认证
            if (!RequestUtils.isAuthenticated(request)) {
                return Result.error("用户未认证");
            }
            
            Integer userId = RequestUtils.getUserId(request);
            if (userId == null) {
                return Result.error("无法获取用户信息");
            }
            
            // 检查用户是否有学生角色
            if (!userRoleService.hasRole(userId, "STUDENT")) {
                return Result.error("只有学生可以查看自己的文章");
            }
            
            var articles = articleService.getMyArticles(userId);
            return Result.success(articles);
            
        } catch (Exception e) {
            return Result.error("获取我的文章失败: " + e.getMessage());
        }
    }
}
