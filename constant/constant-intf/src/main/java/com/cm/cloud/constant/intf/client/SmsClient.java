package com.cm.cloud.constant.intf.client;

import com.cm.cloud.constant.intf.ConstantContonts;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = ConstantContonts.SERVICE_NAME)
public interface SmsClient {

    //接收手机号和代码 发送请求信息
    @ApiOperation("短信发送服务")
    @RequestMapping(value = "/send_sms",method = RequestMethod.POST)
    Boolean sendVerifyCode(@RequestParam("phone") String phone,
                           @ApiParam("0:登录 1:注册：2：找回密码") @RequestParam("type") Integer type);

    @ApiOperation(value = "验证手机验证码")
    @RequestMapping(value = "/verify",method = RequestMethod.POST)
    Boolean verifyCode(@RequestParam("type") @ApiParam("验证类型") Integer type,
                       @RequestParam("phone") @ApiParam("手机号码") String phone,
                       @RequestParam("code") @ApiParam("验证码") String code);
}
