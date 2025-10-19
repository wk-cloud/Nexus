package com.nexus.web.system;

import com.nexus.auth.service.LoginService;
import com.nexus.common.annotation.Limit;
import com.nexus.common.annotation.LoginLog;
import com.nexus.common.annotation.Pass;
import com.nexus.common.core.domain.dto.LoginDto;
import com.nexus.common.core.domain.vo.LoginVo;
import com.nexus.common.core.validation.ValidGroup;
import com.nexus.common.core.view.Result;
import com.nexus.common.enums.HttpCodeEnum;
import com.nexus.common.enums.LimitTypeEnum;
import com.nexus.common.enums.LoginPlatformEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 登录控制器
 *
 * @author wk
 * @date 2024/11/30
 */
@Tag(name = "系统登录模块")
@CrossOrigin
@RestController
@RequestMapping("/system/login")
public class SysLoginController {
    @Resource
    private LoginService loginService;

    /**
     * 退出登录
     *
     * @return {@link Result }<{@link Void }>
     */
    @Operation(summary = "用户退出登录接口")
    @Limit(limitType = LimitTypeEnum.IP)
    @PostMapping("/logout")
    public Result<Void> logout() {
        loginService.loginOut();
        return Result.success(HttpCodeEnum.LOGOUT.getCode(), HttpCodeEnum.LOGOUT.getInfo());
    }

    /**
     * 登录
     *
     * @param loginDto 登录数据
     * @return {@link Result }<{@link LoginVo }>
     */
    @Operation(summary = "用户登录接口")
    @Limit(limitType = LimitTypeEnum.IP)
    @Pass
    @LoginLog(loginPlatform = LoginPlatformEnum.FRONT)
    @PostMapping
    public Result<LoginVo> login(@RequestBody @Validated(ValidGroup.Select.class) LoginDto loginDto) {
        loginDto.setLoginPlatform(LoginPlatformEnum.FRONT.getCode());
        return Result.success(loginService.login(loginDto));
    }

    /**
     * 校验登录是否过期
     *
     * @return {@link Result }<{@link Void }>
     */
    @Operation(summary = "校验登录是否过期接口")
    @Limit(limitType = LimitTypeEnum.IP)
    @Pass
    @GetMapping("/checkLoginExpired")
    public Result<Void> checkLoginExpired() {
        Boolean expired = loginService.checkLoginExpired();
        if (expired) {
            return Result.fail(HttpCodeEnum.TOKEN_EXPIRED.getCode(), HttpCodeEnum.TOKEN_EXPIRED.getInfo());
        }
        return Result.success();
    }

}
