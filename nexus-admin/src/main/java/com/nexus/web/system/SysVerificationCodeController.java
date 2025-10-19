package com.nexus.web.system;


import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import com.alibaba.fastjson2.JSONObject;
import com.nexus.common.annotation.Limit;
import com.nexus.common.annotation.Pass;
import com.nexus.common.core.domain.dto.VerificationCodeDto;
import com.nexus.common.core.view.Result;
import com.nexus.common.enums.LimitTypeEnum;
import com.nexus.system.service.VerificationCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码就控制器
 *
 * @author wk
 * @date 2024/05/29
 */
@Tag(name = "验证码模块")
@CrossOrigin
@RestController
@RequestMapping("/system/verificationCode")
public class SysVerificationCodeController {
    @Resource
    private VerificationCodeService verificationCodeService;

    /**
     * 发送电子邮件验证码
     *
     * @param verificationCodeDto 验证码
     * @return {@link Result }<{@link Void }>
     */
    @Pass
    @Limit(limitType = LimitTypeEnum.IP)
    @Operation(description = "发送邮箱验证码接口")
    @GetMapping("/verificationCode")
    public Result<Void> sendEmailVerificationCode(VerificationCodeDto verificationCodeDto) {
        verificationCodeService.sendEmailVerificationCode(verificationCodeDto);
        return Result.success();
    }

    /**
     * 生成图像验证码
     *
     * @return {@link Result }<{@link String }>
     */
    @Pass
    @Limit(limitType = LimitTypeEnum.IP)
    @Operation(description = "生成图形验证码接口")
    @GetMapping("/captcha")
    public Result<String> generateImageCaptcha() {
        return Result.<String>success().add(verificationCodeService.generateImageCaptcha());
    }

    /**
     * 生成滑块验证码
     *
     * @return {@link CaptchaResponse }<{@link ImageCaptchaVO }>
     */
    @Pass
    @Limit(limitType = LimitTypeEnum.IP)
    @Operation(description = "生成滑块验证码接口")
    @PostMapping("/sliderCaptcha")
    public CaptchaResponse<ImageCaptchaVO> generateSliderCaptcha() {
        return verificationCodeService.generateSliderCaptcha();
    }

    /**
     * 验证滑块验证码
     *
     * @param verifyData 验证数据
     * @return {@link Result }<{@link Boolean }>
     */
    @Pass
    @Limit(limitType = LimitTypeEnum.IP)
    @Operation(description = "验证滑块验证码接口")
    @PostMapping("/verifySliderCaptcha")
    public Result<Boolean> verifySliderCaptcha(@RequestBody JSONObject verifyData) {
        return Result.success(verificationCodeService.verifySliderCaptcha(verifyData));
    }


}
