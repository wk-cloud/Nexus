package com.nexus.common.utils;


import cn.hutool.core.util.IdUtil;
import com.github.yitter.idgen.YitIdHelper;

/**
 * id 工具类
 *
 * @author wk
 * @date 2024/01/13
 */
public class IdUtils extends IdUtil {

    /**
     * 默认的起始时间，为Thu, 04 Nov 2010 01:42:54 GMT
     */
    public static long BASE_TIME = 1288834974657L;

    /**
     * 机器码
     */
    public static short workerId = 1;

    /**
     * 机器码位长
     * 默认值6，限定 WorkerId 最大值为2^6-1，即默认最多支持64个节点。
     */
    public static byte workerIdBitLength = 6;

    /**
     * 序列位长度
     * 默认值6，限制每毫秒生成的ID个数。若生成速度超过5万个/秒，建议加大 SeqBitLength 到 10。
     */
    public static byte seqBitLength = 6;

    /**
     * 雪花 ID (16位)
     * 需要在项目启动时先进行 id生成器 初始化
     *
     * @return {@link Long}
     */
    public static Long snowflakeId(){
        return YitIdHelper.nextId();
    }
}
