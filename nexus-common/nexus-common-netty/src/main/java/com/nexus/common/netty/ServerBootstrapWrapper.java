package com.nexus.common.netty;
import com.nexus.common.netty.config.properties.NettyProperties;
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
    private final NettyProperties nettyProperties;

    /**
     * 服务器引导包装器
     *
     * @param nettyProperties Netty 属性配置
     */
    public ServerBootstrapWrapper(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
        this.bossGroup = new NioEventLoopGroup(nettyProperties.getBossThreads());
        this.workerGroup = new NioEventLoopGroup(nettyProperties.getWorkerThreads());
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
                .option(ChannelOption.SO_BACKLOG, nettyProperties.getSoBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, nettyProperties.isSoKeepalive())
                .childOption(ChannelOption.TCP_NODELAY, nettyProperties.isTcpNodelay())
                .childHandler(initializer);

        return serverBootstrap.bind(nettyProperties.getPort()).addListener(future -> {
            if (future.isSuccess()) {
                log.info("Netty server started on port: {}", nettyProperties.getPort());
            } else {
                log.error("Failed to start Netty server", future.cause());
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