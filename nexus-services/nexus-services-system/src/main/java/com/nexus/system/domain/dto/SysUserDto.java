package com.nexus.system.domain.dto;

import com.nexus.common.core.ip.IpHome;
import com.nexus.common.core.validation.ValidGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户DTO
 *
 * @author wk
 * @date 2024/07/06
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(title = "用户信息dto", description = "用户信息dto")
public class SysUserDto implements Serializable {

    @Schema(name = "主键id")
    @NotNull(message = "主键id不能为空", groups = {ValidGroup.Update.class})
    private Long id;

    @Schema(name = "关联父id")
    private Long parentId;

    @Schema(name = "用户名")
    private String username;

    @Schema(name = "用户昵称")
    @Size(max = 25, message = "昵称长度不能超过25个字符", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String nickName;

    @Schema(name = "头像")
    private String avatar;

    @Schema(name = "个人网站地址")
    @Size(max = 255, message = "个人网站地址长度不能超过255个字符", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String website;

    @Schema(name = "个人简介")
    @Size(max = 255, message = "个人简介长度不能超过255个字符", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String profile;

    @Schema(name = "用户密码")
    @NotBlank(message = "密码不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,16}$",
            message = "密码格式不合法，必须包含大小写字母和数字的组合，可以使用特殊字符，长度在 8-16 之间",
            groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String password;

    @Schema(name = "用户邮箱")
    @NotBlank(message = "邮箱不能为空", groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$",
            message = "邮箱格式不合法",
            groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String email;

    @Schema(name = "随机盐")
    private String salt;

    @Schema(name = "登录时间")
    private LocalDateTime loginTime;

    @Schema(name = "登录类型")
    private Integer loginType;

    @Schema(name = "登录ip地址")
    private String loginIp;

    @Schema(name = "登录ip归属")
    private IpHome loginIpHome;

    @Schema(name = "第三方登录id")
    private String openid;

    @Schema(name = "禁用用户")
    private Boolean disabled;

    @Schema(name = "角色列表")
    private List<SysRoleDto> roleList;

    @Schema(name = "角色标签列表")
    private List<String> roleLabelList;

}
