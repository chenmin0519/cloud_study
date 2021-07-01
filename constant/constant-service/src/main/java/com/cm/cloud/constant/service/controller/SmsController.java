package com.cm.cloud.constant.service.controller;

import com.cm.cloud.commons.excption.ParameterNotValidException;
import com.cm.cloud.constant.intf.Enum.EnumSmsTemplate;
import com.cm.cloud.constant.intf.Enum.EnumVcode;
import com.cm.cloud.constant.intf.client.SmsClient;
import com.cm.cloud.constant.service.service.SmsService;
import com.cm.cloud.constant.service.util.CodeUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class SmsController implements SmsClient {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SmsService smsService;

    private static final int VCODE_EXPIRE_SECONDS = 5 * 60;

    private static final int RESEND_TIME_SECONDS = 60;




    @Override
    @ApiOperation("短信发送服务")
    @RequestMapping(value = "/send_sms",method = RequestMethod.POST)
    public Boolean sendVerifyCode(@RequestParam("phone") String phone,
                                  @ApiParam("0:绑定 1:登录：2：找回密码") @RequestParam("type") Integer type) {
        EnumVcode byType = EnumVcode.findByType(type);
        String key = byType.getPrefix() + phone;
        String code = redisTemplate.opsForValue().get(key);
        if (code == null) {
            code = CodeUtil.getRandom();
        } else {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (VCODE_EXPIRE_SECONDS - expire < RESEND_TIME_SECONDS) {
                throw new ParameterNotValidException("短信发送过于频繁！");
            }
            //跟换验证码
            code = CodeUtil.getRandom();
        }

        EnumSmsTemplate enumSmsTemplate = EnumSmsTemplate.valueOfType(type);
        smsService.sendMessage(phone, enumSmsTemplate.getCode(), "{\"name\":\"" + code + "\"}", enumSmsTemplate.getSign());
        redisTemplate.opsForValue().set(key, code, VCODE_EXPIRE_SECONDS, TimeUnit.SECONDS);

        return true;
    }

    private boolean verify(EnumVcode enumVcode, String phone, String code) {
        String key = enumVcode.getPrefix() + phone;
        String realCode = redisTemplate.opsForValue().get(key);
        return realCode != null && realCode.equals(code);
    }

    @Override
    @ApiOperation(value = "验证手机验证码")
    @RequestMapping(value = "/verify",method = RequestMethod.POST)
    public Boolean verifyCode(@RequestParam("type") @ApiParam("验证类型") Integer type,
                              @RequestParam("phone") @ApiParam("手机号码") String phone,
                              @RequestParam("code") @ApiParam("验证码") String code){
        return verify(EnumVcode.findByType(type), phone, code);
    }
}
