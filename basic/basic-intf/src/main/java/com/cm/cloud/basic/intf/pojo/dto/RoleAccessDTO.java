package com.cm.cloud.basic.intf.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("角色权限")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAccessDTO {

    @ApiModelProperty("角色编码")
    private String roleCode;

    @ApiModelProperty("权限地址")
    private String accessAdress;
}
