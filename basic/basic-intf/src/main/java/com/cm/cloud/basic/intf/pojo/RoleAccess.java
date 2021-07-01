
package com.cm.cloud.basic.intf.pojo;

import com.cm.cloud.commons.pojo.po.MainPO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.OrderBy;
import javax.persistence.Table;


@ApiModel("角色权限")
@Table(name = "role_access")
@Data
public class RoleAccess extends MainPO {

	@OrderBy("desc")
    @ApiModelProperty(value = "角色ID")
    public Long id;

	@OrderBy("desc")
    @ApiModelProperty(value = "菜单ID组")
    public String accessId;
}

