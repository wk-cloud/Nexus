package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.enums.AdminEnum;
import com.nexus.common.core.enums.PermissionStateEnum;
import com.nexus.common.core.exception.ServiceException;
import com.nexus.common.core.domain.ip.IpHome;
import com.nexus.common.core.service.UserService;
import com.nexus.common.core.utils.*;
import com.nexus.common.mybatisplus.core.page.PagingData;
import com.nexus.common.mybatisplus.core.query.QueryParams;
import com.nexus.common.shiro.helper.LoginHelper;
import com.nexus.common.token.utils.TokenUtils;
import com.nexus.system.domain.SysMenu;
import com.nexus.system.domain.SysRoleMenu;
import com.nexus.system.domain.SysUser;
import com.nexus.system.domain.SysUserRole;
import com.nexus.system.domain.dto.SysUserDto;
import com.nexus.system.domain.vo.SysMenuVo;
import com.nexus.system.domain.vo.SysRoleVo;
import com.nexus.system.domain.vo.SysUserVo;
import com.nexus.system.mapper.SysUserMapper;
import com.nexus.system.service.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 用户基本服务impl
 *
 * @author wk
 * @date 2023/04/16
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService, UserService {

    @Resource
    private SysUserMapper baseMapper;
    @Resource
    private SysUserRoleService sysUserRoleService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysRoleMenuService sysRoleMenuService;
    @Resource
    private SysMenuService sysMenuService;

    /**
     * 根据用户id获取用户昵称
     *
     * @param userId 用户id
     * @return {@link String }
     */
    @Override
    public String getUserNickName(Long userId) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(SysUser::getNickName, SysUser::getUsername).eq(SysUser::getId, userId);
        SysUser user = baseMapper.selectOne(lambdaQueryWrapper);
        return StringUtils.isBlank(user.getNickName()) ? user.getUsername() : user.getNickName();
    }

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
            Set<Long> userIds = sysUserVoList.stream().map(SysUserVo::getId).collect(Collectors.toSet());
            // 查询用户和角色关联信息
            LambdaQueryWrapper<SysUserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userRoleLambdaQueryWrapper.in(SysUserRole::getUserId, userIds);
            List<SysUserRole> sysUserRoleList = sysUserRoleService.list(userRoleLambdaQueryWrapper);
            if (CollectionUtils.isNotEmpty(sysUserRoleList)) {
                Map<Long, Set<Long>> userRoleIdMap = sysUserRoleList.stream().collect(Collectors.groupingBy(SysUserRole::getUserId,
                        Collectors.mapping(SysUserRole::getRoleId, Collectors.toSet())));
                Set<Long> roleIds = sysUserRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());
                List<SysRoleVo> roleList = BeanUtils.copyToList(sysRoleService.listByIds(roleIds), SysRoleVo.class);
                for (SysUserVo sysUserVo : sysUserVoList) {
                    IpHome ipHome = IpUtils.completeIpHome(sysUserVo.getLoginIp());
                    sysUserVo.setLoginIpHome(ipHome);
                    Set<Long> currentRoleIds = userRoleIdMap.getOrDefault(sysUserVo.getId(), new HashSet<>());
                    Set<String> rolePermissionList = roleList.stream()
                            .filter(item -> currentRoleIds.contains(item.getId()))
                            .map(SysRoleVo::getLabel)
                            .collect(Collectors.toSet());
                    sysUserVo.setRolePermissions(rolePermissionList);
                }
            }
        }
        return pagingData;
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
     * 修改用户信息
     *
     * @param sysUserDto 系统用户
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateUser(SysUserDto sysUserDto) {
        SysUser user = BeanUtils.toBean(sysUserDto, SysUser.class);
        sysUserRoleService.addRoleForUser(user.getId(), sysUserDto.getRolePermissions());
        return super.updateById(user);
    }

    /**
     * 修改用户禁用状态
     *
     * @param userId 用户id
     * @param disabled 禁用状态
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
        if (ObjectUtils.isNotNull(sysUserVo)) {
            sysUserVo.setLoginIpHome(IpUtils.completeIpHome(sysUserVo.getLoginIp()));
            // 2. 查询用户角色信息列表
            List<SysRoleVo> roleList = sysUserRoleService.queryRoleListByUserId(userId);
            // 3. 封装角色菜单信息
            if (CollectionUtils.isNotEmpty(roleList)) {
                // 3.1. 查询角色和菜单关联数据分组
                List<Long> roleIdList = roleList.stream().map(SysRoleVo::getId).collect(Collectors.toList());
                LambdaQueryWrapper<SysRoleMenu> roleMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
                roleMenuLambdaQueryWrapper.in(SysRoleMenu::getRoleId, roleIdList);
                Map<Long, Set<Long>> roleMenuIdMap = sysRoleMenuService.list(roleMenuLambdaQueryWrapper)
                        .stream().collect(Collectors.groupingBy(SysRoleMenu::getRoleId, Collectors.mapping(SysRoleMenu::getMenuId, Collectors.toSet())));
                // 3.2. 查询状态是正常的菜单列表
                LambdaQueryWrapper<SysMenu> menuLambdaQueryWrapper = new LambdaQueryWrapper<>();
                menuLambdaQueryWrapper.eq(SysMenu::getState, PermissionStateEnum.NORMAL.getCode());
                List<SysMenu> menuList = sysMenuService.list(menuLambdaQueryWrapper);
                // 3.3. 给角色封装对应关联的菜单信息
                roleList.forEach(role -> {
                    if (AdminEnum.SUPER_ADMIN.getLabel().equals(role.getLabel())) {
                        List<SysMenuVo> menuVoList = BeanUtils.copyToList(menuList, SysMenuVo.class);
                        role.setMenuList(menuVoList);
                    } else {
                        Set<Long> menuIds = roleMenuIdMap.getOrDefault(role.getId(), new HashSet<>());
                        List<SysMenu> currentSysMenuList = menuList.stream()
                                .filter(menu -> menuIds.contains(menu.getId()))
                                .collect(Collectors.toList());
                        List<SysMenuVo> menuVoList = BeanUtils.copyToList(currentSysMenuList, SysMenuVo.class);
                        role.setMenuList(menuVoList);
                    }
                });
                // 3.4. 提取去重后的菜单信息
                List<SysMenuVo> distinctMenuList = roleList.stream()
                        .map(SysRoleVo::getMenuList)
                        .flatMap(Collection::stream)
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(SysMenuVo::getId, menu -> menu, (existing, replacement) -> existing),
                                map -> new ArrayList<>(map.values()))
                        );
                distinctMenuList.sort(Comparator.comparing(SysMenuVo::getCreateTime));
                sysUserVo.setMenuList(TreeUtils.createTree(distinctMenuList));
                Set<String> rolePermissions = roleList.stream().map(SysRoleVo::getLabel).filter(StringUtils::isNotBlank)
                        .collect(Collectors.toSet());
                Set<String> menuPermissions = distinctMenuList.stream().map(SysMenuVo::getPerms).filter(StringUtils::isNotBlank)
                        .collect(Collectors.toSet());
                sysUserVo.setRolePermissions(rolePermissions);
                sysUserVo.setMenuPermissions(menuPermissions);
            }
        }
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
     * @return {@link SysUserVo }
     */
    @Override
    public SysUserVo queryUserInfoByToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new ServiceException("用户信息获取失败，令牌为空");
        }
        Map<String, String> userId = TokenUtils.getValueFromToken(token, Collections.singletonList("userId"));
        return this.queryUserById(Long.parseLong(userId.get("userId")));
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
