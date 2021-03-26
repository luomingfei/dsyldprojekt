package com.mmd.domain.dto.profile;

import javafx.beans.DefaultProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ProductDetailDTO {
    @NotEmpty(message="城市ID不能为空")
    private String cityId;
    private Integer pid;
}
