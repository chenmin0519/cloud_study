package com.cm.cloud.authorization.service.services;

import com.cm.cloud.basic.intf.clients.UserClient;
import com.cm.cloud.basic.intf.enums.EnumUserType;
import com.cm.cloud.basic.intf.pojo.Role;
import com.cm.cloud.basic.intf.pojo.User;
import com.cm.cloud.basic.intf.pojo.dto.UserAuthority;
import com.cm.cloud.basic.intf.pojo.dto.WechatLoginVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Objects;


@Component
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username))
            return null;

        return transformLoadRole(userClient.findByUsernameOrPhone(username));
    }

    public UserDetails loadUserByUsername(WechatLoginVO loginVO) throws UsernameNotFoundException {
        if (Objects.isNull(loginVO))
            return null;

        return transform(userClient.addByWeixin(loginVO));
    }

    /**
     * 转换成userDetails 并加载角色
     *
     * @param user
     * @return
     */
    private UserDetails transformLoadRole(User user) {
        UserDetails transform = transform(user);
        if (transform != null) {
            com.cm.cloud.authorization.common.userdetails.UserDetails userDetails = (com.cm
                    .cloud.authorization.common.userdetails.UserDetails) transform;

            if (EnumUserType.ADMIN.getKey().equals(userDetails.getUserType())) {

                UserAuthority userAuthority = userClient.selectUserAuthority(userDetails.getUserId());

                if(!CollectionUtils.isEmpty(userAuthority.getRoles())){
                    String[] rolecodes = userAuthority.getRoles().stream().map(Role::getRoleCode)
                            .distinct().filter(Objects::nonNull).toArray(String[]::new);
                    userDetails.setAuthorities(AuthorityUtils.createAuthorityList(rolecodes));
                    userDetails.setDataAuthorities(userAuthority.getScenics());
                }

            }

        }
        return transform;
    }

    public static UserDetails transform(User user) {
        com.cm.cloud.authorization.common.userdetails.UserDetails userDetails = null;
        if (Objects.nonNull(user)) {
            userDetails = new com.cm.cloud.authorization.common.userdetails.UserDetails();
            BeanUtils.copyProperties(user, userDetails);
            userDetails.setUserId(user.getId());
            userDetails.setAuthorities(AuthorityUtils.createAuthorityList("USER"));
        }
        return userDetails;
    }

}
