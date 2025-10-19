package com.nexus.common.core.sensitive;

import com.nexus.common.utils.FileUtils;
import com.nexus.common.utils.SpringUtils;
import com.nexus.common.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 敏感词
 *
 * @author wk
 * @date 2025/07/13
 */
@Slf4j
@Data
public class SensitiveWord {

    /**
     * 敏感词数据
     */
    private WordDataTree wordData = WordDataTree.newInstance();

    /**
     * 默认敏感词文件读取目录
     */
    private final String DEFAULT_DIRECTORY = "classpath*:/sensitive";

    /**
     * 自定义敏感词文件读取目录或敏感词文件路径
     */
    private final String CUSTOM_PATH = SpringUtils.getProperty("sensitive.path");

    /**
     * 默认替换字符串
     */
    private final String DEFAULT_REPLACE_STR = "*";

    /**
     * 是否结束标识
     */
    private final String isEnd = "isEnd";

    /**
     * 忽略特殊字符的正则表达式
     */
    private final String IGNORE_SPECIAL_CHAR_REGEX = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\\\s*";

    /**
     * 私有化构造器
     */
    private SensitiveWord() {
    }

    /**
     * 创建实例
     *
     * @return {@link SensitiveWord }
     */
    public static SensitiveWord newInstance() {
        return new SensitiveWord();
    }

    /**
     * 初始化
     *
     * @return {@link SensitiveWord }
     */
    public SensitiveWord init() {
        wordData.initWordData(this.loadWord());
        return this;
    }

    /**
     * 初始化
     *
     * @param wordPath 敏感词文件目录或敏感词文件路径
     * @return {@link SensitiveWord }
     */
    public SensitiveWord init(String wordPath) {
        wordData.initWordData(this.loadWord(wordPath));
        return this;
    }

    /**
     * 初始化
     * @param words 敏感词集合
     * @return {@link SensitiveWord }
     */
    public SensitiveWord init(Collection<String> words) {
        wordData.initWordData(words);
        return this;
    }

    /**
     * 加载敏感词
     */
    private Set<String> loadWord() {
        if (StringUtils.isNotBlank(CUSTOM_PATH)) {
            return readWord(FileUtils.getFilePathListDeep(CUSTOM_PATH));
        }else {
            try {
                return readWordFromByte(FileUtils.getResourceFileBytes(DEFAULT_DIRECTORY, "txt"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 加载敏感词
     *
     * @param wordPath 敏感词文件目录或敏感词文件路径
     * @return {@link Set }<{@link String }>
     */
    private Set<String> loadWord(String wordPath) {
        return readWord(FileUtils.getFilePathListDeep(wordPath));
    }

    /**
     * 读取敏感词
     *
     * @param pathList 敏感词文件路径集合
     * @return {@link Set}<{@link String}>
     */
    private Set<String> readWord(List<String> pathList) {
        // 创建一个HashSet来存储敏感词
        Set<String> wordSet = new HashSet<>();
        // 遍历敏感词文件路径集合
        for (String path : pathList) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
                // 读取敏感词
                String sensitiveWord;
                while ((sensitiveWord = bufferedReader.readLine()) != null) {
                    // 如果敏感词包含逗号，则将其拆分为多个敏感词
                    if (sensitiveWord.contains(",")) {
                        String[] sensitiveWordArr = sensitiveWord.split(",");
                        // 将拆分后的敏感词添加到HashSet中
                        wordSet.addAll(Arrays.asList(sensitiveWordArr));
                    } else {
                        // 如果敏感词不包含逗号，则直接添加到HashSet中
                        wordSet.add(sensitiveWord);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 返回HashSet
        return wordSet.stream().map(String::trim).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    }

    /**
     * 读取敏感词
     * @param contents 敏感词字节数组集合
     * @return {@link Set }<{@link String }>
     */
    private Set<String> readWordFromByte(List<byte[]> contents) {
        // 创建一个HashSet来存储敏感词
        Set<String> wordSet = new HashSet<>();
        // 遍历敏感词文件路径集合
        for (byte[] content : contents) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)))) {
                // 读取敏感词
                String sensitiveWord;
                while ((sensitiveWord = bufferedReader.readLine()) != null) {
                    // 如果敏感词包含逗号，则将其拆分为多个敏感词
                    if (sensitiveWord.contains(",")) {
                        String[] sensitiveWordArr = sensitiveWord.split(",");
                        // 将拆分后的敏感词添加到HashSet中
                        wordSet.addAll(Arrays.asList(sensitiveWordArr));
                    } else {
                        // 如果敏感词不包含逗号，则直接添加到HashSet中
                        wordSet.add(sensitiveWord);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 返回HashSet
        return wordSet.stream().map(String::trim).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    }

    /**
     * 检查文本中某个文字是否匹配关键词
     *
     * @param text       文本
     * @param beginIndex 开始索引位置
     * @return int
     */
    private int checkWord(String text, int beginIndex) {
        if(StringUtils.isBlank(text)){
            return 0;
        }
        boolean is_End = false;
        int wordLength = 0;
        WordDataTreeNode currentWordDataNode = wordData.root();
        int length = text.length();
        // 从文本的第beginIndex开始匹配
        for (int i = beginIndex; i < length; i++) {
            String key = String.valueOf(text.charAt(i));
            // 获取当前key的下一个节点
            currentWordDataNode = currentWordDataNode.getSubNode(key);
            if (currentWordDataNode == null) {
                break;
            } else {
                wordLength++;
                if (currentWordDataNode.end()) {
                    is_End = true;
                }
            }
        }
        // 长度必须大于等于1，才是一个词
        if (wordLength < 1 || !is_End) {
            wordLength = 0;
        }
        return wordLength;
    }

    /**
     * 获取匹配的敏感词的命中次数
     *
     * @param text 文本
     * @return {@link Map}<{@link String}, {@link Integer}> 敏感词和命中次数映射
     */
    public Map<String, Integer> getMatchedCount(String text) {
        Map<String, Integer> wordMap = new LinkedHashMap<>();
        if(StringUtils.isBlank(text)){
            return wordMap;
        }
        int length = text.length();
        for (int i = 0; i < length; i++) {
            int wordLength = checkWord(text, i);
            if (wordLength > 0) {
                String word = text.substring(i, i + wordLength);
                // 添加关键词匹配次数
                if (wordMap.containsKey(word)) {
                    wordMap.put(word, wordMap.get(word) + 1);
                } else {
                    wordMap.put(word, 1);
                }
                i += wordLength - 1;
            }

        }
        return wordMap;
    }

    /**
     * 获取匹配的敏感词集合
     *
     * @param text 文本
     * @return {@link Map}<{@link String}, {@link Integer}> 敏感词和命中次数映射
     */
    private Set<String> getMatchedWords(String text) {
        if(StringUtils.isBlank(text)){
            return Collections.emptySet();
        }
        return getMatchedCount(text).keySet();
    }

    /**
     * 替换敏感词
     *
     * @param text 文本
     * @return {@link String }
     */
    public String replace(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 获取敏感词
        Set<String> sensitiveWordSet = this.getMatchedWords(text);
        // 替换敏感词
        for (String sensitiveWord : sensitiveWordSet) {
            String str = "";
            for (int i = 0; i < sensitiveWord.length(); i++) {
                str += DEFAULT_REPLACE_STR;
            }
            text = text.replaceAll(sensitiveWord, str);
        }
        return text;
    }

    /**
     * 替换敏感词
     *
     * @param text       文本
     * @param replaceStr 替换字符串
     * @return {@link String }
     */
    public String replace(String text, String replaceStr) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 获取敏感词
        Set<String> sensitiveWordSet = this.getMatchedWords(text);
        // 替换敏感词
        for (String sensitiveWord : sensitiveWordSet) {
            String str = "";
            for (int i = 0; i < sensitiveWord.length(); i++) {
                str += replaceStr;
            }
            text = text.replaceAll(sensitiveWord, str);
        }
        return text;
    }

    /**
     * 判断文本是否包含敏感词
     *
     * @param text 文本
     * @return boolean
     */
    public boolean contains(String text) {
        if(StringUtils.isBlank(text)){
            return false;
        }
        return getMatchedWords(text).size() > 0;
    }

    /**
     * 返回匹配到的第一个敏感词
     *
     * @param text 文本
     * @return {@link String }
     */
    public String findFirst(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return findAll(text).stream().findFirst().orElse(null);
    }

    /**
     * 返回匹配到的所有敏感词
     *
     * @param text 文本
     * @return {@link Set }<{@link String }>
     */
    public Set<String> findAll(String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptySet();
        }
        return this.getMatchedWords(text);
    }
}
