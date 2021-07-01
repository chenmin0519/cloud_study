package com.cm.cloud.authorization.common.userdetails;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义UserDetails
 */
@Data
public class UserDetails implements Serializable, org.springframework.security.core.userdetails.UserDetails {

    private Long id;

    private Long userId;

    private String username;

    private String nickname;

    private String password;

    private String unlockCode;

    private String phone;

    private String email;

    private Integer userType;

    private String avatar;

    private Integer state;

    private Integer sex;

    private String sessionKey;
    private String wxOpenid;

    private boolean superUser;//超级管理员


    /**
     * 数据权限
     */
    private List<Long> dataAuthorities;
    /* 角色扩展属性 */
    private Integer primaryRole;//主角色

    /* Spring Security related fields*/
    private List<GrantedAuthority> authorities; //用户拥有权限集合,可以自定义实现
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
}
