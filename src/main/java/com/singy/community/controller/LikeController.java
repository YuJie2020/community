package com.singy.community.controller;

import com.singy.community.entity.User;
import com.singy.community.service.LikeService;
import com.singy.community.util.CommunityUtil;
import com.singy.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    // 异步请求，不刷新页面，只更新局部数据（点赞与否与数量）
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId) {
        User user = hostHolder.getUser();
        if (user != null) {
            likeService.like(user.getId(), entityType, entityId, entityUserId); // 点赞
            long likeCount = likeService.findEntityLikeCount(entityType, entityId); // 点赞的数量
            int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId); // 点赞的状态
            Map<String, Object> map = new HashMap<>();
            map.put("likeCount", likeCount);
            map.put("likeStatus", likeStatus);
            return CommunityUtil.getJSONString(0, null, map);
        } else {
            return CommunityUtil.getJSONString(1, "请您先登录！");
        }
    }
}
