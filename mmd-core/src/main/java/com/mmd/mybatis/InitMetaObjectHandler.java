package com.mmd.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InitMetaObjectHandler implements MetaObjectHandler {

    private static final Integer DEFAULT_RECORD_VERSION = 1;
    private static final Integer DELETE_FLAG_FALSE = 0;

    private final static String RECORD_VERSION = "recordVersion";
    private final static String DELETE_FLAG = "deleteFlag";
    private final static String CREATE_TIME = "createTime";
    private final static String UPDATE_TIME = "updateTime";
    private final static String CREATE_BY = "createBy";
    private final static String UPDATE_BY = "updateBy";

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();

        Object createBy = getFieldValByName(CREATE_BY, metaObject);

        setFieldValByName(RECORD_VERSION, DEFAULT_RECORD_VERSION, metaObject);
        setFieldValByName(DELETE_FLAG, DELETE_FLAG_FALSE, metaObject);
        setFieldValByName(CREATE_TIME, now, metaObject);
        setFieldValByName(UPDATE_TIME, now, metaObject);
        setFieldValByName(UPDATE_BY, createBy, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName(UPDATE_TIME, LocalDateTime.now(), metaObject);
    }

}
