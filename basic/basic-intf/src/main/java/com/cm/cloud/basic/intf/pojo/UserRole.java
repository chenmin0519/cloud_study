
package com.cm.cloud.basic.intf.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;


@ApiModel("用户角色")
@Table(name = "user_role")
@Data
public class UserRole {


    @ApiModelProperty(value = "角色ID")
    public String roleId;

    @ApiModelProperty(value = "用户ID")
    public Long id;

}

