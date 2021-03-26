package com.mmd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/30 11:17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSexEntity {
    private int id;
    private int pid;
    private double male;
    private double famale;
}
