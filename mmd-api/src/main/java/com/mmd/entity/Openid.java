package com.mmd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/27 15:37
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Openid {

    private int id;
    private int uid;
    private String openid;
    private String nickname;
    private int sex;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private String miniappId;
}
