package com.singy.community.controller;

import com.singy.community.entity.Comment;
import com.singy.community.service.CommentService;
import com.singy.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        /**
         * 设置comment对象的属性（字段）
         * 其中userId、status及createTime由服务器设置好
         * content、entityType、entityId、targetId由页面（客户端）传给服务器（通过表单提交，除了content其它字段使用隐藏框提交）
         */
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        return "redirect:/discuss/detail/" + discussPostId; // 重定向的方式使添加后的评论可以显示在页面上
    }
}
