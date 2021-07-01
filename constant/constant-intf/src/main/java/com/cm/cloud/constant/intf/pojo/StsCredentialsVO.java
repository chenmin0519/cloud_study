package com.cm.cloud.constant.intf.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@ApiModel("ali oss 凭证")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StsCredentialsVO {

    private String securityToken;

    private String accessKeySecret;

    private String accessKeyId;

    private String expiration;

    @ApiModelProperty("授权的文件夹")
    private String path;

    @ApiModelProperty("外网访问地址")
    private String endpoint;

    @ApiModelProperty("工作区间")
    private String bucketName;


}
