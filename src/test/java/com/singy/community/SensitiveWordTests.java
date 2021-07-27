package com.singy.community;

import com.singy.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) // 引用主配置类（主程序类）
public class SensitiveWordTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以☆赌*博，可以*嫖☆*娼，可以吸☆毒，可以*开☆票，哈*哈哈!";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
