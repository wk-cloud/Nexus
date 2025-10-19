package com.nexus.netty.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * netty配置
 *
 * @author wk
 * @date 2025/10/12
 */
@Configuration
@ConfigurationProperties(prefix = "netty")
@Data
public class NettyConfig {
    /**
     *  服务端监听端口号
     */
    private int port = 8080;
    /**
     * 服务端接受连接线程数
     * 1. 负责接受客户端的连接请求
     * 2. 通常只需要1个线程，因为只有一个ServerSocketChannel
     * 3. 如需要绑定多个端口时，需要增加数量
     */
    private int bossThreads = 1;
    /**
     * 处理 I/O 操作的线程数
     * 1. 负责处理已接受连接的读写操作
     * 2. 默认值0表示自动设置为 CPU核心数×2
     * 3. 线程数越多，处理能力越强，但同时消耗的内存也越多
     * 4. 计算密集型任务可适当减少
     * 5. I/O密集型任务可适当增加
     */
    private int workerThreads = 0;
    /**
     * TCP连接等待队列的最大长度
     * 1. 当所有线程都忙于处理连接时，新连接会进入等待队列
     * 2. 队列满时，新连接将被拒绝
     * 3. 高并发场景下需要适当增大此值
     * 4. Linux系统最大值为/proc/sys/net/core/somaxconn
     */
    private int soBacklog = 1024;

    /**
     * 启用 TCP keepalive 机制
     * 1. true: 启用TCP层的心跳检测
     * 2. false: 关闭TCP层心跳检测
     * 作用：检测空闲连接是否仍然有效
     * 防止因网络问题导致的"僵尸连接"
     */
    private boolean soKeepalive = true;

    /**
     * 禁用 Nagle 算法
     * 1. true: 禁用Nagle算法，数据立即发送
     * 2. false: 启用Nagle算法，，可能延迟发送以合并小包
     * 作用：减少小包的传输延迟，对延迟敏感的应用应该设为true
     *
     */
    private boolean tcpNodelay = true;
    /**
     * 客户端连接超时时间(毫秒)
     * 1. 客户端建立连接的最大等待时间
     * 2. 超时未连接成功会抛出异常
     * 3. 网络不稳定时应适当增大此值
     * 4. 默认 30 s
     */
    private int connectTimeout = 30000;
    /**
     * 读空闲超时时间(秒)
     * 1. 读超时，超过此时间没有数据读入，会触发一次读空闲事件
     * 2. 用于检测客户端是否还在活跃状态
     * 3. 默认 60 s
     * 4. 超时可能表示客户端断开或网络故障
     */
    private int readerIdleTime = 60;
    /**
     * 写空闲超时时间(秒)
     * 1. 写超时，超过此时间没有数据写出，会触发一次写空闲事件
     * 2. 用于检测客户端是否还在活跃状态
     * 3. 用于发送心跳包维持连接
     * 4. 默认 30 s
     * 5. 超时可能表示客户端断开或网络故障
     * 6. 防止因长时间无数据传输导致连接被中间设备关闭
     */
    private int writerIdleTime = 30;
    /**
     * 读写都空闲的时间(秒)
     * 1. 在指定时间内既没有读也没有写操作触发全空闲事件
     * 2. 0表示禁用此检测
     * 3. 通常用于严格的连接健康检查
     */
    private int allIdleTime = 0;

    /**
     * 获取工作线程数
     *
     * @return int
     */
    public int getWorkerThreads() {
        return workerThreads > 0 ? workerThreads : Runtime.getRuntime().availableProcessors() * 2;
    }
}