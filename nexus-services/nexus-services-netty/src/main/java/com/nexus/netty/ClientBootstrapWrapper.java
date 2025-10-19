package com.nexus.netty;
import com.nexus.netty.config.NettyConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CompletableFuture;

/**
 * Netty客户端引导包装器
 *
 * @author wk
 * @date 2025/10/14
 */
public class ClientBootstrapWrapper {
    private final Bootstrap bootstrap;
    private final EventLoopGroup workerGroup;
    private final NettyConfig config;

    /**
     * 客户端引导包装器
     *
     * @param config netty配置
     */
    public ClientBootstrapWrapper(NettyConfig config) {
        this.config = config;
        this.workerGroup = new NioEventLoopGroup(config.getWorkerThreads());
        this.bootstrap = new Bootstrap();
    }

    /**
     * 连接
     *
     * @param host 主机
     * @param port 端口
     * @return {@link CompletableFuture }<{@link Channel }>
     */
    public CompletableFuture<Channel> connect(String host, int port) {
        CompletableFuture<Channel> future = new CompletableFuture<>();

        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeout())
                .option(ChannelOption.SO_KEEPALIVE, config.isSoKeepalive())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // 由外部配置
                    }
                });

        bootstrap.connect(host, port).addListener((ChannelFutureListener) connectFuture -> {
            if (connectFuture.isSuccess()) {
                future.complete(connectFuture.channel());
            } else {
                future.completeExceptionally(connectFuture.cause());
            }
        });

        return future;
    }
}