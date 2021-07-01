package com.cm.cloud.constant.service.service.impl;

import com.cm.cloud.commons.excption.ParameterNotValidException;
import com.cm.cloud.commons.excption.ServiceLogicException;
import com.cm.cloud.constant.service.SmsCodeDecoder;
import com.cm.cloud.constant.service.service.SmsService;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
import java.util.stream.Stream;


@Service
@Slf4j
public class SmsServiceImpl implements SmsService {


    private static final String domain = "dysmsapi.aliyuncs.com";
    private static final String product = "Dysmsapi";

    static {
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "3000");
        System.setProperty("sun.net.client.defaultReadTimeout", "3000");
    }


    @Autowired
    @Qualifier("smsClient")
    IAcsClient acsClient;

    @Override
    public void sendMessage(String phoneNumbers, String code, String param, String sign) {

        Stream.of(phoneNumbers.split(","))
                .forEach(phone -> {
                    if (!Pattern.matches("^1[0-9]{10}$", phone)) {
                        throw new ParameterNotValidException("手机号码不合法:" + phone);
                    }
                });

        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
        request.setPhoneNumbers(phoneNumbers);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(sign);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(code);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam(param);
        //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//        request.setOutId("yourOutId");
//请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            log.error("发送短信失败", e);
            throw new ServiceLogicException("发送短信失败:" + e.getErrMsg());
        }
        if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK"))
            return;

        throw new ServiceLogicException(SmsCodeDecoder.decode(sendSmsResponse.getCode()));

    }

}
