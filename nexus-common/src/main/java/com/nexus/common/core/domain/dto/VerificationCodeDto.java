package com.nexus.common.core.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码dto
 *
 * @author wk
 * @date 2025/04/06
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VerificationCodeDto {

    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码类型
     */
    private Integer verificationCodeType;

    /**
     * 验证码来源
     */
    private String verificationCodeSource;

    /**
     * 签名
     */
    private String signature;

    /**
     * 时间戳
     * */
    private Long timestamp;

}
