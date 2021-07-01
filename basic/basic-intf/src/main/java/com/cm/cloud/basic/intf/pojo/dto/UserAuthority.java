package com.cm.cloud.basic.intf.pojo.dto;

import com.cm.cloud.basic.intf.pojo.Role;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;


@ApiModel("用户权限")
@Data
public class UserAuthority {

    private List<Role> roles;

    private List<Long> scenics;
}
