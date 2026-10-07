package com.nexus.common.netty;
import com.nexus.common.netty.config.properties.NettyProperties;
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
    private final NettyProperties nettyProperties;

    /**
     * 客户端引导包装器
     *
     * @param nettyProperties Netty 属性配置
     */
    public ClientBootstrapWrapper(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
        this.workerGroup = new NioEventLoopGroup(nettyProperties.getWorkerThreads());
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
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyProperties.getConnectTimeout())
                .option(ChannelOption.SO_KEEPALIVE, nettyProperties.isSoKeepalive())
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