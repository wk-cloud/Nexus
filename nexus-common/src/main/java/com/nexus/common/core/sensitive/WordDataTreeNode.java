package com.nexus.common.core.sensitive;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词数据树节点
 *
 * @author wk
 * @date 2025/07/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordDataTreeNode {

    /**
     * 结束标识
     * */
    private Boolean isEnd;

    /**
     * 子节点集合
     * */
    private Map<String, WordDataTreeNode> subNodeMap;

    /**
     * 子节点为空
     *
     * @return boolean
     */
    public boolean subNodeIsNull(){
        return subNodeMap == null;
    }

    /**
     * 返回结束表示
     *
     * @return boolean
     */
    public boolean end() {
        return isEnd;
    }

    /**
     * 结束
     *
     * @param isEnd 结束
     * @return {@link WordDataTreeNode }
     */
    public WordDataTreeNode end(Boolean isEnd){
        this.isEnd = isEnd;
        return this;
    }

    /**
     * 获取子节点
     *
     * @param key
     * @return {@link WordDataTreeNode }
     */
    public WordDataTreeNode getSubNode(String key){
        if(subNodeIsNull()){
            return null;
        }
        return subNodeMap.get(key);
    }

    /**
     * 获取子节点大小
     *
     * @return int
     */
    public int getNodeSize(){
        if(subNodeIsNull()){
            return 0;
        }
        return subNodeMap.size();
    }

    /**
     * 添加子节点
     *
     * @param key
     * @param node
     * @return {@link WordDataTreeNode }
     */
    public WordDataTreeNode addSubNode(String key, WordDataTreeNode node){
        if(subNodeIsNull()){
            subNodeMap = new HashMap<>();
        }
        subNodeMap.put(key, node);
        return this;
    }

    /**
     * 清除节点
     */
    public void clearNode(){
        if(subNodeIsNull()){
            return;
        }
        subNodeMap.clear();
    }

    /**
     * 删除节点
     *
     * @param key
     */
    public void removeNode(String key){
        if(subNodeIsNull()){
            return;
        }
        subNodeMap.remove(key);
    }

}
