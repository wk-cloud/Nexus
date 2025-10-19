package com.nexus.common.utils;

import com.nexus.common.core.domain.dto.VerificationCodeDto;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * 验证码工具类
 * @author wk
 * @date 2022/8/5
 */
@Slf4j
public class VerificationCodeUtils {

    /**
     * 签名验证
     *
     * @param verificationCodeDto 验证码信息
     * @return boolean
     */
    public static boolean signatureVerification(VerificationCodeDto verificationCodeDto){
        String source = verificationCodeDto.getEmail() + ":" + verificationCodeDto.getTimestamp();
        String signature = EncryptionUtils.md5Hash(source);
        return signature.equals(verificationCodeDto.getSignature());
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
        String code = "";
        for (int i = 0; i < len; i++) {
            code += (int) (Math.random() * 10);
        }
        return code;
    }

    /**
     * 创造验证码(数字和字母混合)
     *
     * @param len 验证码长度
     * @return {@link String}
     */
    public static String create(int len) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, len);
    }

    /**
     * 创造验证码(数字和字母混合)
     *
     * @param begin 开始索引
     * @param end   结束索引
     * @return {@link String}
     */
    public static String create(int begin, int end) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(begin, end);
    }

}
