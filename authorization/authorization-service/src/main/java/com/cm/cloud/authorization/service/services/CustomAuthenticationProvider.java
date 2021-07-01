package com.cm.cloud.authorization.service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

/**
 * 注意这个refresh 不是走的这个Provider
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CustomUserDetailService userService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private ArrayList<LoginTypePolicy> policies;

    @Autowired
    ApplicationContext applicationContext;

    @PostConstruct
    public void initPolicy() {
        this.policies = new ArrayList<>(this.applicationContext.getBeansOfType(LoginTypePolicy.class).values());
        this.policies.sort(Comparator.comparingInt(LoginTypePolicy::sort));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        Object details = authentication.getDetails();

        if (details instanceof Map) {

            Map<String, String> map = (Map<String, String>) details;

            if (map.containsKey(MobileCodePolicy.LOGIN_TYPE_KEY)) {

                Authentication auth;
                for (LoginTypePolicy policy : policies) {
                    if (!policy.supports(Integer.valueOf(map.get(MobileCodePolicy.LOGIN_TYPE_KEY)))) {
                        continue;
                    }
                    auth = policy.loadUser(username, password, map);
                    if (auth != null) {
                        return auth;
                    }

                }
            }
        }

        UserDetails user = userService.loadUserByUsername(username);
        if (user == null) {
            throw new BadCredentialsException("用户不存在！");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("密码错误！");
        }

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
