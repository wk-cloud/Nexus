package com.nexus.common.verificationcode.utils;

import com.nexus.common.verificationcode.domain.dto.VerificationCodeDto;
import com.nexus.common.core.utils.EncryptionUtils;
import com.nexus.common.redis.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类
 * @author wk
 * @date 2022/8/5
 */
@Slf4j
public class VerificationCodeUtils {

    /**
     * 签名有效期（毫秒）
     */
    private static final long SIGNATURE_EXPIRE_TIME = 60 * 1000; // 1分钟

    /**
     * 签名验证（带防重放机制）
     *
     * @param verificationCodeDto 验证码信息
     * @return boolean
     */
    public static boolean signatureVerification(VerificationCodeDto verificationCodeDto){
        // 1. 检查时间戳是否在有效期内（使用毫秒）
        long currentTime = System.currentTimeMillis();
        if (verificationCodeDto.getTimestamp() == null ||
                Math.abs(currentTime - verificationCodeDto.getTimestamp()) > SIGNATURE_EXPIRE_TIME) {
            log.warn("====> 签名验证失败：时间戳过期或无效, timestamp={}", verificationCodeDto.getTimestamp());
            return false;
        }

        // 2. 验证签名是否正确（使用原始毫秒时间戳）
        String source = verificationCodeDto.getEmail() + ":" + verificationCodeDto.getTimestamp();
        String signature = EncryptionUtils.md5Hash(source);
        if (!signature.equals(verificationCodeDto.getSignature())) {
            log.warn("====> 签名验证失败：签名不匹配");
            return false;
        }

        // 3. 检查签名是否已被使用（防重放）
        String signatureKey = "verification:signature:used:" + verificationCodeDto.getSignature();
        if (RedisUtils.hasKey(signatureKey)) {
            log.warn("====> 签名验证失败：签名已被使用，可能是重放攻击");
            return false;
        }

        // 4. 标记签名为已使用，设置过期时间为签名有效期（转换为秒）
        RedisUtils.setEx(signatureKey, "1", SIGNATURE_EXPIRE_TIME / 1000, TimeUnit.SECONDS);

        return true;
    }

    /**
     * 创建字母验证码
     *
     * @param len 验证码长度
     * @return {@link String}
     */
    public static String createLetterCode(int len) {
        // 纯字母组合的验证码
        String[] letterCodes = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        // 随机获取指定len长度的字母组合的验证码
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(letterCodes[(int) (Math.random() * letterCodes.length)]);
        }
        return sb.toString();
    }

    /**
     * 创建数字验证码
     *
     * @param len 验证码长度
     * @return {@link String}
     */
    public static String createNumberCode(int len) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < len; i++) {
            code.append((int) (Math.random() * 10));
        }
        return code.toString();
    }

    /**
     * 创造验证码(数字和字母混合)
     *
     * @param len 验证码长度
     * @return {@link String}
     */
    public static String create(int len) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, len);
    }

    /**
     * 创造验证码(数字和字母混合)
     *
     * @param begin 开始索引
     * @param end   结束索引
     * @return {@link String}
     */
    public static String create(int begin, int end) {
        return UUID.randomUUID().toString().replace("-", "").substring(begin, end);
    }

}
