package com.singy.community.controller;

import com.singy.community.annotation.LoginRequired;
import com.singy.community.entity.User;
import com.singy.community.service.UserService;
import com.singy.community.util.CommunityUtil;
import com.singy.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还未选择要上传的图片！");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        if (StringUtils.isBlank(suffix) || !suffix.equals(".jpg") && !suffix.equals(".jpeg") && !suffix.equals(".png")) {
            model.addAttribute("error", "文件仅支持jpg、jpeg、png格式！");
            return "/site/setting";
        }

        // 生成随机的文件名，以将用户上传的头像保存到服务器（名字不重复）
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件的存放的路径
        File destination = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(destination);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }

        // 更新当前用户头像的路径（web访问路径：服务器本地磁盘的图片，映射到服务器上也能访问）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    // web访问路径：服务器本地磁盘的图片，映射到服务器上也能访问
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存储文件的路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf('.') + 1); // 不包括 .
        /**
         * 响应图片：jpeg和jpg的MIME类型都为image/jpeg
         * <mime-mapping>
         *      <extension>jpeg</extension>
         *      <mime-type>image/jpeg</mime-type>
         * </mime-mapping>
         * <mime-mapping>
         *      <extension>jpg</extension>
         *      <mime-type>image/jpeg</mime-type>
         * </mime-mapping>
         */
        response.setContentType("image/" + (suffix.equals("png") ? "png" : "jpeg"));
        try (
                FileInputStream fis = new FileInputStream(fileName);
                ServletOutputStream os = response.getOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }

    // 修改密码
    @LoginRequired
    @RequestMapping(path = "/changePassword", method = RequestMethod.POST)
    public String changePassword(String oldPassword, String newPassword, String confirmPassword, Model model) {
        User user = hostHolder.getUser(); // 获取当前登录对象
        Map<String, Object> map = userService.changePassword(user, oldPassword, newPassword, confirmPassword);
        if (map == null || map.isEmpty()) {
            return "redirect:/index"; // 修改密码成功
        } else {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            model.addAttribute("confirmPasswordMsg", map.get("confirmPasswordMsg"));
            return "/site/setting";
        }
    }
}
