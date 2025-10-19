package com.nexus.system.service.impl;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.alibaba.fastjson2.JSONObject;
import com.nexus.common.core.domain.dto.VerificationCodeDto;
import com.nexus.common.enums.EmailTemplateEnum;
import com.nexus.common.enums.VerificationCodeTypeEnum;
import com.nexus.common.exception.ServiceException;
import com.nexus.common.utils.*;
import com.nexus.system.service.VerificationCodeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * 验证码服务实现
 *
 * @author wk
 * @date 2024/11/17
 */
@Slf4j
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    @Resource
    private MailUtils mailUtils;
    @Resource
    private ImageCaptchaApplication application;

    /**
     * 发送电子邮件验证码
     *
     * @param verificationCodeDto 验证码信息
     */
    @Override
    public void sendEmailVerificationCode(VerificationCodeDto verificationCodeDto) {
        String email = verificationCodeDto.getEmail().trim();
        if (!VerificationUtils.isEmail(email)) {
            throw new ServiceException("验证码发送失败，邮箱格式不合法");
        }
        boolean verification = VerificationCodeUtils.signatureVerification(verificationCodeDto);
        if(!verification){
            throw new ServiceException("验证码发送失败，签名校验失败");
        }
        String verificationCodeKey = VerificationCodeTypeEnum.getKey(verificationCodeDto.getVerificationCodeType()) + email;
        String verificationCode = VerificationCodeUtils.create(8);
        if (RedisUtils.hasKey(verificationCodeKey)) {
            throw new ServiceException("验证码已经发送，请耐心等待");
        }
        RedisUtils.setEx(verificationCodeKey, verificationCode, 1, TimeUnit.MINUTES);
        mailUtils.sendSimpleMail(email, EmailTemplateEnum.VERIFICATION_CODE.getSubject(), String.format(EmailTemplateEnum.VERIFICATION_CODE.getText(), verificationCode));
    }

    /**
     * 生成图形验证码
     */
    @Override
    public String generateImageCaptcha() {
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(116, 36, 4, 5);
        String code = circleCaptcha.getCode();
        RedisUtils.setEx("captcha:" + code, code, 1, TimeUnit.MINUTES);
        String imageBase64 = circleCaptcha.getImageBase64();
        return "data:image/png;base64," + imageBase64;
    }

    /**
     * 校验图形验证码
     *
     * @param captcha 验证码
     * @return boolean
     */
    @Override
    public boolean verifyImageCaptcha(String captcha) {
        String code = (String) RedisUtils.get("captcha:" + captcha);
        if(StringUtils.isBlank(code)){
            return false;
        }
        return code.equals(captcha);
    }

    /**
     * 生成滑块验证码
     *
     * @return {@link CaptchaResponse}
     */
    @Override
    public CaptchaResponse<ImageCaptchaVO> generateSliderCaptcha() {
        return application.generateCaptcha(CaptchaTypeConstant.SLIDER);
    }

    /**
     * 验证 滑块验证码
     *
     * @param verifyData 验证数据
     * @return boolean
     */
    @Override
    public boolean verifySliderCaptcha(JSONObject verifyData) {
        String id = verifyData.getString("id");
        ImageCaptchaTrack imageCaptchaTrack = verifyData.getJSONObject("data").toJavaObject(ImageCaptchaTrack.class);
        ApiResponse<?> match = application.matching(id,imageCaptchaTrack);
        return match.isSuccess();
    }
}
