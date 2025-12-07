package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.helper.LoginHelper;
import com.nexus.common.core.ip.IpHome;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.common.enums.AdminEnum;
import com.nexus.common.enums.PermissionStateEnum;
import com.nexus.common.exception.ServiceException;
import com.nexus.common.utils.*;
import com.nexus.system.domain.SysPermission;
import com.nexus.system.domain.SysRolePermission;
import com.nexus.system.domain.SysUser;
import com.nexus.system.domain.SysUserRole;
import com.nexus.system.domain.dto.SysUserDto;
import com.nexus.system.domain.vo.SysPermissionVo;
import com.nexus.system.domain.vo.SysRoleVo;
import com.nexus.system.domain.vo.SysUserVo;
import com.nexus.system.mapper.SysUserMapper;
import com.nexus.system.service.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 用户基本服务impl
 *
 * @author wk
 * @date 2023/04/16
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysUserMapper baseMapper;
    @Resource
    private SysUserRoleService sysUserRoleService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysRolePermissionService sysRolePermissionService;
    @Resource
    private SysPermissionService sysPermissionService;


    /**
     * 查询用户列表
     *
     * @param sysUserDto 系统用户
     * @param queryParams 查询参数
     * @return {@link PagingData}<{@link SysUserVo}>
     */
    @Override
    public PagingData<SysUserVo> listUser(SysUserDto sysUserDto, QueryParams queryParams) {
        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = this.buildLambdaQueryWrapper(sysUserDto);
        Page<SysUserVo> sysUserVoPage = baseMapper.queryVoPage(queryParams.build(), userLambdaQueryWrapper);
        PagingData<SysUserVo> pagingData = PagingData.build(sysUserVoPage);
        List<SysUserVo> sysUserVoList = pagingData.getDataList();
        if (CollectionUtils.isNotEmpty(sysUserVoList)) {
            // 查询所有的用户和角色关联信息
            List<SysUserRole> userRoleList = sysUserRoleService.list();
            // 查询所有的角色信息
            List<SysRoleVo> roleList = sysRoleService.queryRoleListAll();
            for (SysUserVo sysUserVo : sysUserVoList) {
                IpHome ipHome = IpUtils.completeIpHome(sysUserVo.getLoginIp());
                sysUserVo.setLoginIpHome(ipHome);
                List<SysUserRole> userRoles = userRoleList.stream().filter(item -> item.getUserId().equals(sysUserVo.getId())).toList();
                List<SysRoleVo> sysRoleVoList = roleList.stream()
                        .filter(item -> userRoles.stream().anyMatch(userRole -> userRole.getRoleId().equals(item.getId())))
                        .collect(Collectors.toList());
                sysUserVo.setRoleList(sysRoleVoList);
            }
        }
        return pagingData;
    }

    /**
     * 获取用户分布
     *
     * @return {@link Map}<{@link String}, {@link Integer}>
     */
    @Override
    public Map<String, Integer> queryUserDistribution() {
        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.select(SysUser::getLoginIp);
        List<SysUser> userList = baseMapper.selectList(userLambdaQueryWrapper);
        HashMap<String, Integer> map = new HashMap<>(CollectionUtils.getInitialCapacity(userList.size()));
        for (SysUser user : userList) {
            IpHome ipHome = IpUtils.getIpHome(user.getLoginIp());
            String ipAddress = ipHome.getProvince();
            if (map.containsKey(ipAddress)) {
                Integer count = map.get(ipAddress);
                count++;
                map.put(ipAddress, count);
            } else {
                map.put(ipAddress, 1);
            }
        }
        return map;
    }

    /**
     * 通过用户id获取电子邮件
     *
     * @param userId 用户id
     * @return {@link String}
     */
    @Override
    public String queryEmailByUserId(Long userId) {
        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.select(SysUser::getId, SysUser::getEmail).eq(SysUser::getId, userId);
        SysUser user = baseMapper.selectOne(userLambdaQueryWrapper);
        if(ObjectUtils.isNull(user)){
            return null;
        }
        return user.getEmail();
    }

    /**
     * 更新用户
     *
     * @param userBackDto 用户 DTO
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateUser(SysUserDto userBackDto) {
        SysUser user = BeanUtils.toBean(userBackDto, SysUser.class);
        sysUserRoleService.addRoleForUser(user.getId(), userBackDto.getRoleLabelList());
        return super.updateById(user);
    }

    /**
     * 更新禁用状态
     *
     * @param userId 用户基本id
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateDisableState(Long userId, Boolean disabled) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setDisabled(disabled);
        return super.updateById(user);
    }

    /**
     * 更新密码
     *
     * @param olderPassword 旧密码
     * @param newPassword   新密码
     * @return {@link Boolean}
     */
    @Override
    public Boolean updatePassword(String olderPassword, String newPassword) {
        Long userId = LoginHelper.getUserId();
        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper
                .select(SysUser::getSalt, SysUser::getPassword, SysUser::getId, SysUser::getLoginTime)
                .eq(SysUser::getId, userId);
        SysUser user = baseMapper.selectOne(userLambdaQueryWrapper);
        olderPassword = EncryptionUtils.passwordEncryption(olderPassword, user.getSalt());
        if (!olderPassword.equals(user.getPassword())) {
            throw new ServiceException("密码修改失败，失败原因：旧密码有误");
        }
        String salt = RandomSaltUtils.createRandomSalt(8);
        newPassword = EncryptionUtils.passwordEncryption(newPassword, salt);
        user.setSalt(salt);
        user.setPassword(newPassword);
        return super.updateById(user);
    }

    /**
     * 根据id，查询用户基础信息
     *
     * @param userId 用户基本id
     * @return {@link SysUserVo}
     */
    @Override
    public SysUserVo queryUserById(Long userId) {
        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper
                .select(SysUser::getUsername, SysUser::getLoginIp, SysUser::getLoginType,
                        SysUser::getEmail, SysUser::getAvatar, SysUser::getNickName,
                        SysUser::getProfile, SysUser::getId, SysUser::getDisabled,
                        SysUser::getWebsite, SysUser::getCreateTime, SysUser::getLoginTime)
                .eq(SysUser::getId, userId);
        // 1. 查询用户信息
        SysUserVo sysUserVo = baseMapper.queryVoOne(userLambdaQueryWrapper, SysUserVo.class);
        sysUserVo.setLoginIpHome(IpUtils.completeIpHome(sysUserVo.getLoginIp()));
        // 2. 查询用户角色信息列表
        List<SysRoleVo> roleList = sysUserRoleService.queryRoleListByUserId(userId);
        // 3. 封装角色权限信息
        if (CollectionUtils.isNotEmpty(roleList)) {
            // 3.1. 查询角色和权限关联数据分组
            List<Long> roleIdList = roleList.stream().map(SysRoleVo::getId).collect(Collectors.toList());
            LambdaQueryWrapper<SysRolePermission> rolePermissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            rolePermissionLambdaQueryWrapper.in(SysRolePermission::getRoleId, roleIdList);
            Map<Long, List<SysRolePermission>> rolePermissionGroup = sysRolePermissionService.list(rolePermissionLambdaQueryWrapper)
                    .stream().collect(Collectors.groupingBy(SysRolePermission::getRoleId));
            // 3.2. 查询所有的权限列表
            LambdaQueryWrapper<SysPermission> permissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            permissionLambdaQueryWrapper.eq(SysPermission::getState, PermissionStateEnum.NORMAL.getCode());
            List<SysPermission> permissionList = sysPermissionService.list(permissionLambdaQueryWrapper);
            // 3.3. 封装权限信息
            roleList.forEach(role -> {
                if(AdminEnum.SUPER_ADMIN.getLabel().equals(role.getLabel())){
                    role.setPermissionList(TreeUtils.createTree(BeanUtils.copyToList(permissionList, SysPermissionVo.class)));
                } else {
                    List<Long> permissionIdList = rolePermissionGroup.get(role.getId())
                            .stream()
                            .map(SysRolePermission::getPermissionId)
                            .toList();
                    List<SysPermission> currentPermissionList = permissionList.stream()
                            .filter(permission -> permissionIdList.contains(permission.getId()))
                            .collect(Collectors.toList());
                    role.setPermissionList(TreeUtils.createTree(BeanUtils.copyToList(currentPermissionList, SysPermissionVo.class)));
                }
            });
        }
        sysUserVo.setRoleList(roleList);
        return sysUserVo;
    }

    /**
     * 验证电子邮件和密码
     *
     * @param email    电子邮件
     * @param password 密码
     * @return {@link SysUser}
     */
    @Override
    public SysUser verificationEmailAndPassword(String email, String password) {
        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(SysUser::getEmail, email).last("limit 1");
        SysUser user = baseMapper.selectOne(userLambdaQueryWrapper);
        if (ObjectUtils.isNull(user)) {
            throw new ServiceException("账号不存在，请先注册");
        }
        // 检查用户是否被禁止登录
        if (user.getDisabled()) {
            throw new ServiceException("登录失败，当前账号已被禁止登录，请联系管理员进行账号解封");
        }
        String salt = user.getSalt();
        password = EncryptionUtils.passwordEncryption(password, salt);
        if (!user.getPassword().equals(password)) {
            throw new ServiceException("用户密码错误");
        }
        return user;
    }

    /**
     * 按令牌查询用户信息
     *
     * @param token 令 牌
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @Override
    public Map<String, Object> queryUserInfoByToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new ServiceException("用户信息获取失败，令牌为空");
        }
        Map<String, String> userId = TokenUtils.getValueFromToken(token, List.of("userId"));
        SysUserVo sysUserVo = this.queryUserById(Long.parseLong(userId.get("userId")));
        HashMap<String, Object> map = new HashMap<>(CollectionUtils.getInitialCapacity(2));
        List<String> roleLabelList = sysUserVo.getRoleList().stream().map(SysRoleVo::getLabel).toList();
        // 放入用户信息
        map.put("user", sysUserVo);
        map.put("roleLabelList", roleLabelList);
        return map;
    }

    /**
     * 检查用户是否存在
     *
     * @param sysUserDto 系统用户
     * @return boolean
     */
    @Override
    public Boolean checkEmailUnique(SysUserDto sysUserDto) {
        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper
                .eq(SysUser::getEmail, sysUserDto.getEmail())
                .ne(ObjectUtils.isNotNull(sysUserDto.getId()), SysUser::getId, sysUserDto.getId());
        return !baseMapper.exists(userLambdaQueryWrapper);
    }

    /**
     * 构建 Lambda 查询包装器
     *
     * @param sysUserDto 系统用户
     * @return {@link LambdaQueryWrapper}<{@link SysUser}>
     */
    private LambdaQueryWrapper<SysUser> buildLambdaQueryWrapper(SysUserDto sysUserDto) {
        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotNull(sysUserDto)) {
            userLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysUserDto.getId()), SysUser::getId, sysUserDto.getId());
            userLambdaQueryWrapper.like(StringUtils.isNotBlank(sysUserDto.getUsername()), SysUser::getUsername, sysUserDto.getUsername());
            userLambdaQueryWrapper.like(StringUtils.isNotBlank(sysUserDto.getNickName()), SysUser::getNickName, sysUserDto.getNickName());
            userLambdaQueryWrapper.like(StringUtils.isNotBlank(sysUserDto.getEmail()), SysUser::getEmail, sysUserDto.getEmail());
            userLambdaQueryWrapper.like(StringUtils.isNotBlank(sysUserDto.getLoginIp()), SysUser::getLoginIp, sysUserDto.getLoginIp());
            userLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysUserDto.getLoginType()), SysUser::getLoginType, sysUserDto.getLoginType());
        }
        return userLambdaQueryWrapper;
    }
}
