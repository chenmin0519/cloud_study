/**
 * Created by éé  on 2017-12-08 09:27:49
 */
package com.cm.cloud.basic.intf.pojo;

import com.alibaba.fastjson.JSONObject;
import com.cm.cloud.commons.pojo.po.MainPO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@ApiModel("用户信息")
@Table(name = "sys_user")
@Data
public class User extends MainPO {

    @Id
    @OrderBy("desc")
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /**
     * 如果通过手机号去绑定微信 修改这个字段为公众平台的unionid openId是存储小程序openId的字段
     * 没有单独的unionId字段
     */
    @ApiModelProperty(value = "用户名", required = true)
    @Column(name = "username")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty(value = "昵称")
    @Column(name = "nickname")
    private String nickname;

    @Pattern(regexp = "^1[0-9]{10}$", message = "手机号码格式不正确")
    @ApiModelProperty(value = "手机号码")
    @Column(name = "phone")
    private String phone;

    @ApiModelProperty(value = "性别 1-男 2女 0-未知")
    private Integer sex;

    @ApiModelProperty("用户来源")
    private String source;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty("微信openId")
    private String wxOpenid;

    @ApiModelProperty(value = "电子邮件")
    @Column(name = "email")
    private String email;

    @ApiModelProperty("用户类型1-手机 2-后台")
    private Integer userType;

    @ApiModelProperty(value = "状态 1-正常 0-无效")
    @Column(name = "state")
    private Integer state;

    @ApiModelProperty("最后登录时间")
    private LocalDateTime lastLoginTime;

    @Transient
    @ApiModelProperty("小程序回话的密钥")
    private String sessionKey;

    @Transient
    @ApiModelProperty("用户所属景区ID")
    private String scenicId;

    @Transient
    @ApiModelProperty("用户角色ID组")
    private String roleId;

    @Transient
    @ApiModelProperty(value = "小程序Openid", hidden = true)
    private String miniOpenId;

    @Transient
    @ApiModelProperty("用户角色编码")
    private String roleCode;

    /**
     * 通过小程序appid 查找微信openId
     *
     * @param appId
     * @return
     */
    public String findOpenId(String appId) {

        if (StringUtils.isBlank(this.wxOpenid)) {
            return null;
        } else {
            JSONObject jsonObject = JSONObject.parseObject(this.wxOpenid);
            if (jsonObject.containsKey(appId)) {
                return jsonObject.getOrDefault(appId, "").toString();
            }

        }
        return null;
    }
}

