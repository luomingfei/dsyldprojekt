package com.mmd.domain.dto.input;

import com.mmd.entity.BaseQueryBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/4/10 10:00
 */
@Data
@Builder
public class StoreQueryInputDTO extends BaseQueryBean {

    private String city;

    private String name;

}
