package com.nexus.netty;
import com.nexus.netty.config.NettyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty服务器引导包装器
 *
 * @author wk
 * @date 2025/10/12
 */
@Slf4j
public class ServerBootstrapWrapper {
    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final NettyConfig config;

    /**
     * 服务器引导包装器
     *
     * @param config 配置
     */
    public ServerBootstrapWrapper(NettyConfig config) {
        this.config = config;
        this.bossGroup = new NioEventLoopGroup(config.getBossThreads());
        this.workerGroup = new NioEventLoopGroup(config.getWorkerThreads());
        this.serverBootstrap = new ServerBootstrap();
    }

    /**
     * 启动Netty服务
     *
     * @param initializer 初始化器
     * @return {@link ChannelFuture }
     */
    public ChannelFuture start(ChannelInitializer<SocketChannel> initializer) {
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, config.getSoBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, config.isSoKeepalive())
                .childOption(ChannelOption.TCP_NODELAY, config.isTcpNodelay())
                .childHandler(initializer);

        return serverBootstrap.bind(config.getPort()).addListener(future -> {
            if (future.isSuccess()) {
                log.info("Netty server started on port: {}", config.getPort());
            } else {
                log.error("Failed to start netty server", future.cause());
            }
        });
    }

    /**
     * 关闭
     */
    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}