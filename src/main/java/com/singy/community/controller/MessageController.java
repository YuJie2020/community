package com.singy.community.controller;

import com.singy.community.entity.Message;
import com.singy.community.entity.Page;
import com.singy.community.entity.User;
import com.singy.community.service.MessageService;
import com.singy.community.service.UserService;
import com.singy.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser(); // 当前（登录）用户
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message); // 会话的最新私信
                map.put("letterCount", messageService.findLetterCount(message.getConversationId())); // 会话中包含的私信数量
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId())); // 会话的未读私信数量
                /**
                 * 会话的目标用户（当前用户的某一私信的另一方参与者）
                 * 无论当前用户某一会话的最新私信中当前用户为发送者还是接收者都显示另一方用户的信息（头像，名称）
                 */
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 用户的未读私信数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId())); // 私信的发送用户

                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        // 会话的目标用户
        model.addAttribute("target", getLetterTarget(conversationId));

        return "/site/letter-detail";
    }

    // 获取会话的目标用户
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }
}
