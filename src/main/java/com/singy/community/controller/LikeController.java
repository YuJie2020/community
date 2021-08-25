package com.singy.community.controller;

import com.singy.community.entity.Event;
import com.singy.community.entity.User;
import com.singy.community.event.EventProducer;
import com.singy.community.service.LikeService;
import com.singy.community.util.CommunityConstant;
import com.singy.community.util.CommunityUtil;
import com.singy.community.util.HostHolder;
import com.singy.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    // 异步请求，不刷新页面，只更新局部数据（点赞与否与数量）
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) { // postId：点赞的帖子（评论所在帖子）id
        User user = hostHolder.getUser();
        if (user != null) {
            likeService.like(user.getId(), entityType, entityId, entityUserId); // 点赞
            long likeCount = likeService.findEntityLikeCount(entityType, entityId); // 点赞的数量
            int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId); // 点赞的状态
            Map<String, Object> map = new HashMap<>();
            map.put("likeCount", likeCount);
            map.put("likeStatus", likeStatus);

            // 触发点赞事件
            if (likeStatus == 1) { // 点赞时才发送系统通知（取消赞则不发送）
                Event event = new Event()
                        .setTopic(TOPIC_LIKE)
                        .setUserId(hostHolder.getUser().getId())
                        .setEntityType(entityType)
                        .setEntityId(entityId)
                        .setEntityUserId(entityUserId)
                        .setData("postId", postId);
                eventProducer.fireEvent(event);
            }

            if (entityType == ENTITY_TYPE_POST) { // 对帖子进行点赞/取消赞
                // 计算帖子分数
                String redisKey = RedisKeyUtil.getPostScoreKey();
                redisTemplate.opsForSet().add(redisKey, postId);
            }

            return CommunityUtil.getJSONString(0, null, map);
        } else {
            return CommunityUtil.getJSONString(1, "请您先登录！");
        }
    }
}
