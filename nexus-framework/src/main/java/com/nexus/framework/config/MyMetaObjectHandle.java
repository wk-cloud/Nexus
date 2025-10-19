package com.nexus.framework.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 时间自动填充
 *
 * @author wk
 * @date 2023/10/05
 */
@Slf4j
@Component
public class MyMetaObjectHandle implements MetaObjectHandler {


    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("====> 插入自动填充");
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("====> 更新自动填充");
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
}
