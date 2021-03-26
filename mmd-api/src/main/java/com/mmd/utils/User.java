package com.mmd.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiWen.xue
 * @date 2020-03-09 21:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String username;
}
