package com.mmd.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiWen.xue
 * @date 2020-03-25 11:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenResponseVO {
    private String token;
    private Long expirationTime;
}
