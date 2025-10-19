package com.nexus.netty.handler;

import com.nexus.common.core.view.Result;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.ErrorResponse;

import java.io.IOException;

/**
 * 异常处理器
 *
 * @author wk
 * @date 2025/10/14
 */
@Slf4j
public class ExceptionHandler extends ChannelDuplexHandler {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Netty Exception caught in channel: {}", ctx.channel().id(), cause);

        if (cause instanceof IOException) {
            // 网络异常，关闭连接
            ctx.close();
        } else if (cause instanceof CodecException) {
            // 编解码异常，返回错误响应
            ctx.writeAndFlush(createErrorResponse(cause));
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.write(msg, promise.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.error("Netty Write operation failed", future.cause());
                promise.setFailure(future.cause());
            }
        }));
    }

    /**
     * 创建错误响应
     *
     * @param cause 原因
     * @return {@link Object }
     */
    private Object createErrorResponse(Throwable cause) {
        // 创建错误响应对象
        return Result.fail(cause.getMessage());
    }
}
