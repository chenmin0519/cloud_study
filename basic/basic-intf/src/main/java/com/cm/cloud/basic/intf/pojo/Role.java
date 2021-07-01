
package com.cm.cloud.basic.intf.pojo;

import com.cm.cloud.commons.pojo.po.MainPO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.List;


@ApiModel("角色")
@Table(name = "sys_role")
@Data
public class Role extends MainPO{

    @Id
	@OrderBy("desc")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ApiModelProperty(value = "角色名")
    private String roleName;

    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @Transient
    @ApiModelProperty(value = "菜单ID组")
    public List<Long> scenics;


}

