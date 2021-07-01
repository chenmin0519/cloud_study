package com.cm.cloud.basic.intf.pojo.vo;

import com.cm.cloud.basic.intf.pojo.Role;
import com.cm.cloud.basic.intf.pojo.SysAccess;
import com.cm.cloud.basic.intf.pojo.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("用户所有信息")
@Data
public class UserVO extends User {

    @ApiModelProperty("用户角色")
    private List<Role> roleList;

    @ApiModelProperty("用户权限")
    private List<SysAccess> sysAccessList;
}
