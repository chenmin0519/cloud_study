package com.cm.cloud.basic.intf.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;

@ApiModel("用户角色关系表")
@Table(name = "sys_user_role")
@Data
public class SysUserRole {
    @ApiModelProperty(value = "角色ID")
    public Long roleId;

    @ApiModelProperty(value = "用户ID")
    public Long userId;
}
