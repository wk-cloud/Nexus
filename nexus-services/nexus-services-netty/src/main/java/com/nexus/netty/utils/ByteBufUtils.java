package com.nexus.netty.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

/**
 * 字节buf 工具类
 *
 * @author wk
 * @date 2025/10/14
 */
public class ByteBufUtils {

    /**
     * 读取字节
     *
     * @param byteBuf 字节buf
     * @return {@link byte[] }
     */
    public static byte[] readBytes(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    /**
     * 复制到字节buf
     *
     * @param bytes 字节
     * @return {@link ByteBuf }
     */
    public static ByteBuf copyToByteBuf(byte[] bytes) {
        ByteBuf byteBuf = Unpooled.buffer(bytes.length);
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }

    /**
     * 读取字符串
     *
     * @param byteBuf 字节buf
     * @param charset 字符集
     * @return {@link String }
     */
    public static String readString(ByteBuf byteBuf, Charset charset) {
        return byteBuf.toString(charset);
    }

    /**
     * 安全释放 ByteBuf 资源
     *
     * @param byteBuf 字节buf
     */
    public static void release(ByteBuf byteBuf) {
        if (byteBuf != null && byteBuf.refCnt() > 0) {
            byteBuf.release();
        }
    }
}