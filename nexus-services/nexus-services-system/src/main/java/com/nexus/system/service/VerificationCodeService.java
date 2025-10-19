package com.nexus.system.service;

import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import com.alibaba.fastjson2.JSONObject;
import com.nexus.common.core.domain.dto.VerificationCodeDto;

/**
 * 验证码服务
 *
 * @author wk
 * @date 2024/11/17
 */
public interface VerificationCodeService {

    /**
     * 发送电子邮件验证码
     *
     * @param verificationCodeDto 验证码信息
     */
    void sendEmailVerificationCode(VerificationCodeDto verificationCodeDto);

    /**
     * 生成图形验证码
     */
    String generateImageCaptcha();

    /**
     * 验证图形验证码
     * @param captcha 验证码
     * @return 是否验证通过
     */
    boolean verifyImageCaptcha(String captcha);

    /**
     * 生成滑块验证码
     *
     * @return {@link CaptchaResponse}
     */
    CaptchaResponse<ImageCaptchaVO> generateSliderCaptcha();

    /**
     * 验证滑块验证码
     *
     * @param verifyData 验证数据
     * @return boolean
     */
    boolean verifySliderCaptcha(JSONObject verifyData);
}
