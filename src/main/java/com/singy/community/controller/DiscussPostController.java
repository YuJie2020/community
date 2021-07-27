package com.singy.community.controller;

import com.singy.community.entity.Comment;
import com.singy.community.entity.DiscussPost;
import com.singy.community.entity.Page;
import com.singy.community.entity.User;
import com.singy.community.service.CommentService;
import com.singy.community.service.DiscussPostService;
import com.singy.community.service.UserService;
import com.singy.community.util.CommunityConstant;
import com.singy.community.util.CommunityUtil;
import com.singy.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    // 异步请求（返回JSON字符串）
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            // 403：表示资源不可用（服务器理解客户的请求，但拒绝处理它，通常由于服务器上文件或目录的权限设置导致的WEB访问错误）
            return CommunityUtil.getJSONString(403, "用户暂未登录！");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        // 报错的情况统一处理
        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 帖子信息
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", discussPost);
        // 作者信息
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);

        // 评论分页信息
        page.setLimit(5); // 每页显示评论数
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());

        /**
         * 评论：给帖子的评论 Comment
         * 回复：给帖子的评论的评论 Reply
         */
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());
        // Vo: ViewObject 视图（显示）对象
        // 评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVO = new HashMap<>();
                // 评论
                commentVO.put("comment", comment);
                // 作者
                commentVO.put("user", userService.findUserById(comment.getUserId()));

                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVO = new HashMap<>();
                        // 回复
                        replyVO.put("reply", reply);
                        // 作者
                        replyVO.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVO.put("target", target);

                        replyVoList.add(replyVO);
                    }
                }
                commentVO.put("replies", replyVoList);

                // 回复的数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("replyCount", replyCount);

                commentVoList.add(commentVO);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
