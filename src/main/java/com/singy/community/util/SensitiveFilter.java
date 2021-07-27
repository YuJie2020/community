package com.singy.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 敏感词替换字符串
    private static final String REPLACEMENT = "***";

    // 前缀树根节点
    private TrieNode root = new TrieNode();

    @PostConstruct // 用于指定初始化方法：构造方法执行之后执行
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加敏感词到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }

    }

    private void addKeyword(String keyword) { // 添加敏感词到前缀树
        TrieNode curNode = root; // 当前节点指针
        for (int i = 0; i < keyword.length(); i++) {
            char ch = keyword.charAt(i);
            TrieNode subNode = curNode.getSubNode(ch);
            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                curNode.addSubNode(ch, subNode);
            }
            // 将当前节点指针后移，以判断下一个字符
            curNode = subNode;
            // 设置结束标识
            if (i == keyword.length() - 1) {
                curNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        TrieNode curNode = root; // 指向前缀树的当前节点指针
        int begin = 0; // 指向待过滤本文当前判断子串的起始
        int end = 0; // 指向待过滤本文当前判断子串的末尾
        StringBuilder result = new StringBuilder(); // 过滤后的文本
        while (end < text.length()) {
            char ch = text.charAt(end);

            // 1. 跳过特殊符号
            if (isParticularSymbol(ch)) {
                // 如果前缀树指针处于根节点，则将此特殊符号计入结果，并使子串起始指针后移
                if (curNode == root) {
                    result.append(ch);
                    begin++;
                }
                // 无论特殊符号位于子串的任意位置，子串结束指针都需要后移
                end++;
                continue;
            }

            // 2. 检查下级节点
            curNode = curNode.getSubNode(ch);
            if (curNode == null) {
                // 以begin起始的子串不是敏感词
                result.append(text.charAt(begin));
                // 判断下一子串
                end = ++begin;
                curNode = root; // 前缀树指针需要重新指向根节点
            } else if (curNode.isKeywordEnd()) {
                // 发现敏感词
                result.append(REPLACEMENT); // 替换敏感子串
                // 判断下一子串
                begin = ++end;
                curNode = root; // 前缀树指针需要重新指向根节点
            } else {
                // 检查下一个字符
                end++;
            }
        }
        // 将剩余字符加入结果
        result.append(text.substring(begin));
        return result.toString();
    }

    // 判断字符是否为特殊符号
    private boolean isParticularSymbol(Character ch) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(ch) && (ch < 0x2E80 || ch > 0x9FFF);
    }

    // 前缀树节点内部类
    private class TrieNode {

        // 关键词结束标识（默认值为 false）
        private boolean isKeywordEnd = false;

        // 子节点Map集合（key为子节点对应的字符值，value为子节点对象；默认值为空集合）
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character ch, TrieNode node) {
            subNodes.put(ch, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character ch) {
            return subNodes.get(ch);
        }
    }
}