package com.mmd.domain.dto.input;

import com.mmd.entity.BaseQueryBean;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/30 11:40
 */
@Data
@Accessors(chain = true)
public class ProductQueryInputDTO extends BaseQueryBean {
    private Integer isRecommend;
    private String category;
    private String cityId;
}
