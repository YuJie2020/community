package com.singy.community.quartz;

import com.singy.community.entity.DiscussPost;
import com.singy.community.service.DiscussPostService;
import com.singy.community.service.ElasticsearchService;
import com.singy.community.service.LikeService;
import com.singy.community.util.CommunityConstant;
import com.singy.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 刷新帖子分数任务
 */
public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    // 社区论坛创建时间
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-06-20 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化社区论坛创建时间失败！", e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            logger.info("[任务取消] 暂时没有需要刷新分数的帖子");
            return;
        }

        logger.info("[任务开始] 正在刷新 " + operations.size() + " 个帖子的分数");

        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop()); // 使用弹出的方式，确保下个时间段更新分数的帖子id集合初始为空
        }

        logger.info("[任务结束] 帖子分数刷新完毕");
    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);

        if (post == null) {
            logger.error("id 为 " + postId + " 的帖子不存在");
            return;
        }

        // 是否为精华帖
        boolean isWonderful = post.getStatus() == 1;
        // 帖子评论数量
        int commentCount = post.getCommentCount();
        // 帖子点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // 计算权重
        double w = (isWonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 帖子分数 = 权重 + 距离天数
        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);

        // 更新帖子分数
        discussPostService.updateScore(postId, score);

        // 同步搜索数据
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}
