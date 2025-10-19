package com.nexus.common.core.sensitive;


import com.alibaba.excel.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词数据树
 *
 * @author wk
 * @date 2025/07/19
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordDataTree {

    /**
     * 根节点
     */
    private WordDataTreeNode root;

    /**
     * 获取实例
     * @return {@link WordDataTree }
     */
    public static WordDataTree newInstance() {
        return new WordDataTree();
    }

    /**
     * 根节点
     * @return {@link WordDataTreeNode }
     */
    public WordDataTreeNode root(){
        return this.root;
    }

    /**
     * 初始化数据
     *
     * @param words 字
     */
    public WordDataTree initWordData(Collection<String> words) {
        WordDataTreeNode newRoot = new WordDataTreeNode();

        for (String word : words) {
            if (StringUtils.isBlank(word)) {
                continue;
            }
            addWord(newRoot, word);
        }
        this.root = newRoot;
        return this;
    }

    /**
     * 获取当前的 Map
     * @param nowNode 当前节点
     * @param key 指定的 key
     * @return 实际的当前 map
     * @since 0.0.7
     */
    public WordDataTreeNode getNowMap(WordDataTreeNode nowNode, String key) {
        return nowNode.getSubNode(key);
    }

    /**
     * 添加单词
     * @param newRoot 新的根节点
     * @param word    单词
     */
    private void addWord(WordDataTreeNode newRoot, String word) {
        WordDataTreeNode tempNode = newRoot;
        char[] wordCharArray = word.toCharArray();
        for (char c : wordCharArray) {
            // 获取子节点
            WordDataTreeNode subNode = tempNode.getSubNode(String.valueOf(c));
            if (subNode == null) {
                subNode = new WordDataTreeNode();
                // 加入新的子节点
                tempNode.addSubNode(String.valueOf(c), subNode);
            }

            // 临时节点指向子节点，进入下一次循环
            tempNode = subNode;
        }

        // 设置结束标识（循环结束，设置一次即可）
        tempNode.end(true);
    }

    /**
     * 删除单词
     *
     * @param root 根节点
     * @param word 单词
     */
    private void removeWord(WordDataTreeNode root, String word){
        WordDataTreeNode tempNode = root;
        Map<String, WordDataTreeNode> map = new HashMap<>();
        char[] chars = word.toCharArray();
        int length = chars.length;
        for (int i = 0; i < length; i++) {
            //不存在第一个词,直接退出
            WordDataTreeNode subNode = tempNode.getSubNode(String.valueOf(chars[i]));
            if (subNode == null) {
                return;
            }
            if (i == (length - 1)) {
                //尾字符判断是否结束
                if (!subNode.end()) {
                    return;
                }
                if (subNode.getNodeSize() > 0) {
                    //尾字符下还存在字符
                    subNode.end(false);
                    return;
                }
            }
            if (subNode.end()) {
                map.clear();
            }
            map.put(String.valueOf(chars[i]), tempNode);
            tempNode = subNode;
        }

        for (Map.Entry<String, WordDataTreeNode> entry : map.entrySet()) {
            WordDataTreeNode value = entry.getValue();
            //节点只有一个就清空
            if (value.getNodeSize() == 1) {
                value.clearNode();
                return;
            }
            //多个就删除
            value.removeNode(entry.getKey());
        }
    }
}
