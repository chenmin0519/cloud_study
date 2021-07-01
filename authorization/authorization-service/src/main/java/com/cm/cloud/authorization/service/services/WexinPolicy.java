package com.cm.cloud.authorization.service.services;

import com.cm.cloud.basic.intf.pojo.dto.WechatLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class WexinPolicy implements LoginTypePolicy {

    private static final Integer WEIXIN_LOGIN_TYPE = 3;

    @Autowired
    CustomUserDetailService userService;

    @Override
    public int sort() {
        return 2;
    }

    /**
     * 是否支持当前验证类型
     *
     * @param type
     * @return
     */
    @Override
    public boolean supports(Integer type) {
        return WEIXIN_LOGIN_TYPE.equals(type);
    }

    @Override
    public Authentication loadUser(String username, String password, Map<String, String> params) {

        String source = params.getOrDefault("device", "UNKNOWN");
        WechatLoginVO wechatLoginVO = new WechatLoginVO();
        wechatLoginVO.setCode(params.get("code"));
        wechatLoginVO.setSource(source);
        //小程序登录
        if (!"MINI".equalsIgnoreCase(source)) {//Android IOS 自己拿到了unionId。。。。。
            wechatLoginVO.setUnionid(username);
        } else {
            if (!params.containsKey("code")) {
                throw new BadCredentialsException("code 不能为空");
            }
            //小程序的appid 是
            wechatLoginVO.setAppid(username);
        }
        wechatLoginVO.setAvatarUrl(params.get("avatarUrl"));
        wechatLoginVO.setNickName(params.get("nickName"));
        UserDetails user = userService.loadUserByUsername(wechatLoginVO);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, password, authorities);
    }
}
