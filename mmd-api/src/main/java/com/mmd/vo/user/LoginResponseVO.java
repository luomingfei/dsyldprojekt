package com.mmd.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiWen.xue
 * @date 2020-03-25 11:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseVO {
    private JwtTokenResponseVO token;
    private String tid;
    private String type;
    private Boolean isBlack;
    private String uid;
    private Boolean getCoupon;
    private String username;
    private Boolean isNew;
}
