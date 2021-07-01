package com.cm.cloud.constant.service.service;


public interface SmsService {

    /**
     * 发送短信
     *
     * @param phoneNumbers 手机号以逗号隔开
     * @param code         验证码
     * @param param        JSON参数
     * @param sign         签名
     * @return
     */
    void sendMessage(String phoneNumbers, String code, String param, String sign);

}
