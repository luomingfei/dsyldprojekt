package com.mmd.domain.dto.output;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/4/10 10:05
 */
@Data
@Accessors(chain = true)
public class TnsOutputDTO {

    private Integer id;

    /**
     * 姓名
     */
    private String xm;

    /**
     * 性别
     */
    private String xb;

    /**
     * 位置
     */
    private String wz;

    /**
     * 头像
     */
    private String tx;

    /**
     * 服务区域
     */
    private String fwqy;

    /**
     * 工作经验
     */
    private String gzjy;

    /**
     * y显示,n隐藏,m满约
     */
    private String status;

    private Integer createtime;

    /**
     * 年龄
     */
    private String age;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 城市
     */
    private String city;

    /**
     * 位置坐标信息
     */
    private String location;

    /**
     * 推拿师星级
     */
    private Integer level;

    /**
     * 是否是店长
     */
    private Boolean flag;


}
