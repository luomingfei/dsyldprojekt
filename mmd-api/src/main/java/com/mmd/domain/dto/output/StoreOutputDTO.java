package com.mmd.domain.dto.output;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/4/9 16:00
 */
@Data
@Accessors(chain = true)
public class StoreOutputDTO {

    private Integer id;

    private String name;

    private String photo;

    private String coord;

    private Integer status;

    private String info;

    private String phone;

    private Integer cover;

    private String address;

    private String city;

    private Integer level;

    private String pids;

    /**
     * 店长ID（关联技师ID）
     */
    @TableField("managerId")
    private Integer managerId;
}
