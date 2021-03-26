package com.mmd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/30 11:14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LevelInfo {
    private Integer lid;
    private String info;
    private Double extra;
}
