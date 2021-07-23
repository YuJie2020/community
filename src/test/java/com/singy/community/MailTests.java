package com.singy.community;

import com.singy.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) // 引用主配置类（主程序类）
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine; // 模板引擎

    @Test
    public void testTextMail() {
        mailClient.sendMail("vegalau@qq.com", "TEST", "welcome.");
    }

    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "singy");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("vegalau@qq.com", "HTML", content);
    }
}
