package com.cm.cloud.authorization.service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Map;


//@Component
public class DefaultPolicy implements LoginTypePolicy {

    @Autowired
    CustomUserDetailService userDetailService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 授权顺序
     *
     * @return
     */
    @Override
    public int sort() {
        return 9999;
    }

    /**
     * 是否支持当前验证类型
     *
     * @param type
     * @return
     */
    @Override
    public boolean supports(Integer type) {
        return true;
    }

    @Override
    public Authentication loadUser(String username, String password, Map<String, String> params) {

        UserDetails user = userDetailService.loadUserByUsername(username);
        if (user == null) {
            throw new BadCredentialsException("用户不存在！");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("密码错误！");
        }

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, password, authorities);

    }
}
