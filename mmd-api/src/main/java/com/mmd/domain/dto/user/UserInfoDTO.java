package com.mmd.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiWen.xue
 * @date 2020-03-16 10:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Integer id;
    private String username;
    private String headimgurl;
    private String nickname;
}
