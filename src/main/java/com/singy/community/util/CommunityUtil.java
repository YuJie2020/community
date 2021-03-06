package com.singy.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5加密：采用Spring自带的MD5加密方法，一般还需要加延值确保安全（延长密码后加密）
    // password + 3e4a8 -> abc123def456jki4c
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) { // 如果密码为null或空格，返回null
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes()); // 返回16进制加密后的字符串
    }

    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        if (msg != null) {
            json.put("msg", msg);
        }
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toString();
    }

    // getJSONString方法的重载
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }
}
