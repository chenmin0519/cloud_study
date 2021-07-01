
package com.cm.cloud.basic.intf.pojo;

import com.cm.cloud.commons.pojo.po.MainPO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;


@ApiModel("用户扩展")
@Table(name = "user_extends")
@Data
public class UserExtends extends MainPO {

    @Id
    @ApiModelProperty(value = "用户ID")
    public Long id;

    @ApiModelProperty(value = "")
    private String  scenicId;

}

