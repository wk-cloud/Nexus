package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.utils.BeanUtils;
import com.nexus.common.core.utils.ObjectUtils;
import com.nexus.common.core.utils.StringUtils;
import com.nexus.system.domain.SysMenu;
import com.nexus.system.domain.dto.SysMenuDto;
import com.nexus.system.domain.vo.SysMenuVo;
import com.nexus.system.mapper.SysMenuMapper;
import com.nexus.system.service.SysMenuService;
import com.nexus.system.service.SysRoleMenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * 菜单服务impl
 *
 * @author wk
 * @date 2023/04/16
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysMenuMapper baseMapper;
    @Resource
    private SysRoleMenuService roleMenuService;

    /**
     * @param menuId 菜单id
     * @return {@link SysMenuVo}
     */
    @Override
    public SysMenuVo getMenuById(Long menuId) {
        return baseMapper.queryVoById(menuId);
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单id
     * @return {@link Boolean}
     */
    @Override
    public Boolean deleteMenu(Long menuId) {
        // 1. 创建 list集合，用户封装所有删除菜单id值
        List<Long> idList = new ArrayList<>();
        // 2. 向 idList 集合设置删除菜单id
        this.queryMenuChildrenById(menuId, idList);
        // 把当前 id 封装到 idList里面
        idList.add(menuId);
        // 删除角色和菜单关系
        roleMenuService.deleteByMenuIdList(idList);
        return this.removeBatchByIds(idList);
    }

    /**
     * 通过子菜单id查询子菜单
     *
     * @param menuId 菜单id
     * @param idList       id列表
     */
    private void queryMenuChildrenById(Long menuId, List<Long> idList) {
        // 查询菜单里面子菜单id
        LambdaQueryWrapper<SysMenu> menuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        menuLambdaQueryWrapper.eq(SysMenu::getParentId, menuId).select(SysMenu::getId);
        List<SysMenu> childrenIdList = baseMapper.selectList(menuLambdaQueryWrapper);
        // 把childrenIdList中的菜单id值获取出来，封装到idList中，做递归查询
        childrenIdList.forEach(item -> {
            // 封装到 idList 里面
            idList.add(item.getId());
            // 递归查询
            this.queryMenuChildrenById(item.getId(), idList);
        });
    }

    /**
     * 更新菜单
     *
     * @param sysMenuDto 系统菜单
     * @return {@link Integer}
     */
    @Override
    public Boolean updateMenu(SysMenuDto sysMenuDto) {
        return this.updateById( BeanUtils.toBean(sysMenuDto, SysMenu.class));
    }

    /**
     * 添加菜单
     *
     * @param sysMenuDto 系统菜单
     * @return {@link Integer}
     */
    @Override
    public Boolean addMenu(SysMenuDto sysMenuDto) {
        return this.save(BeanUtils.toBean(sysMenuDto, SysMenu.class));
    }

    /**
     * 查询菜单列表
     *
     * @param sysMenuDto 系统菜单
     * @return {@link List}<{@link SysMenuVo}>
     */
    @Override
    public List<SysMenuVo> listMenu(SysMenuDto sysMenuDto) {
        LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = this.buildLambdaQueryWrapper(sysMenuDto);
        return baseMapper.queryVoList(lambdaQueryWrapper);
    }

    /**
     * 构建 Lambda 查询包装器
     *
     * @param sysMenuDto 系统菜单
     * @return {@link LambdaQueryWrapper}<{@link SysMenu}>
     */
    private LambdaQueryWrapper<SysMenu> buildLambdaQueryWrapper(SysMenuDto sysMenuDto) {
        LambdaQueryWrapper<SysMenu> menuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotNull(sysMenuDto)) {
            menuLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysMenuDto.getId()), SysMenu::getId, sysMenuDto.getId());
            menuLambdaQueryWrapper.like(StringUtils.isNotBlank(sysMenuDto.getName()), SysMenu::getName, sysMenuDto.getName());
            menuLambdaQueryWrapper.like(StringUtils.isNotBlank(sysMenuDto.getTitle()), SysMenu::getTitle, sysMenuDto.getTitle());
            menuLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysMenuDto.getState()), SysMenu::getState, sysMenuDto.getState());
        }
        return menuLambdaQueryWrapper;
    }
}
