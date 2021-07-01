package com.cm.cloud.authorization.service.services;

import com.cm.cloud.basic.intf.clients.UserClient;
import com.cm.cloud.basic.intf.enums.EnumUserType;
import com.cm.cloud.basic.intf.pojo.User;
import com.cm.cloud.constant.intf.Enum.EnumVcode;
import com.cm.cloud.constant.intf.client.SmsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Component
public class MobileCodePolicy implements LoginTypePolicy {

    private static Integer LOGIN = 1;

    private static Integer REGISTER = 2;

    public final List<Integer> types = Arrays.asList(LOGIN, REGISTER);

    @Autowired
    UserClient userClient;

    @Autowired
    SmsClient smsClient;

    @Override
    public int sort() {
        return 1;
    }

    /**
     * 是否支持当前登录类型
     *
     * @param type
     * @return
     */
    @Override
    public boolean supports(Integer type) {
        return types.contains(type);
    }

    @Override
    public Authentication loadUser(String username, String password, Map<String, String> params) {

        org.springframework.security.core.userdetails.UserDetails userDetails = null;

        if (!Boolean.TRUE.equals(smsClient.verifyCode(EnumVcode.LOGIN.getType(), username, password))) {
            throw new BadCredentialsException("验证码不正确！");
        }

        User user = userClient.findByUsernameOrPhone(username);
        if (user == null) {
            User register = new User();
            register.setNickname(username);
            register.setUserType(EnumUserType.APP.getKey());
            register.setUsername(username);
            register.setPhone(username);
            register.setSource("APP-" + params.getOrDefault("device", "UNKNOWN"));
            register.setLastLoginTime(LocalDateTime.now());
            userDetails = CustomUserDetailService.transform(userClient.save(register));
        } else
            userDetails = CustomUserDetailService.transform(user);


        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

    }
}
