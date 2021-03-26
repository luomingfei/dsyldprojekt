package com.mmd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/27 16:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyUser {

    private int id;

    private String username;

}
