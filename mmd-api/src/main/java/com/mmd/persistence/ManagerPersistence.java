package com.mmd.persistence;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户信息新增dto
 * </p>
 *
 * @author dsc
 * @since 2020年2月13日
 */
@Data
@Accessors(chain = true)
public class ManagerPersistence implements Serializable {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 真实姓名
     */
    private String realName;

}
