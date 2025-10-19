package com.nexus.common.core.validation;


import jakarta.validation.GroupSequence;

/**
 * 校验分组
 *
 * @author wk
 * @date 2024/03/30
 */
public class ValidGroup {

    // 新增使用(配合spring的@Validated功能分组使用)
    public interface Insert {
    }

    // 更新使用(配合spring的@Validated功能分组使用)
    public interface Update {
    }

    // 删除使用(配合spring的@Validated功能分组使用)
    public interface Delete {
    }

    // 查询使用(配合spring的@Validated功能分组使用)
    public interface Select {
    }

    // 属性必须有这两个分组的才验证(配合spring的@Validated功能分组使用)
    @GroupSequence({Insert.class, Update.class, Delete.class, Select.class})
    public interface All {
    }
}
