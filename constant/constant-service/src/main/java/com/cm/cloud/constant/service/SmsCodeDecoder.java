package com.cm.cloud.constant.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 将阿里sms 错误码 解析成中文信息
 */
public class SmsCodeDecoder {

    static Map<String, String> map = new ConcurrentHashMap<>();

    static {
        map.put("isp.RAM_PERMISSION_DENY", "RAM权限DENY");
        map.put("isv.OUT_OF_SERVICE", "业务停机");
        map.put("isv.PRODUCT_UN_SUBSCRIPT", "未开通云通信产品的阿里云客户");
        map.put("isv.PRODUCT_UNSUBSCRIBE", "产品未开通");
        map.put("isv.ACCOUNT_NOT_EXISTS", " 账户不存在");
        map.put("isv.ACCOUNT_ABNORMAL", "账户异常");
        map.put("isv.SMS_TEMPLATE_ILLEGAL", "短信模板不合法");
        map.put("isv.MOBILE_NUMBER_ILLEGAL", "非法手机号");
        map.put("isp.SYSTEM_ERROR", "短信服务系统错误");
        map.put("isv.AMOUNT_NOT_ENOUGH ", "账户余额不足");
        map.put("isv.BUSINESS_LIMIT_CONTROL ", "业务限流(短信发送过于频繁)");
    }

    public static String decode(String code) {
        return map.getOrDefault(code, "发送短信失败：" + code);
    }
//
//    isv.SMS_SIGNATURE_ILLEGAL 短信签名不合法
//    isv.INVALID_PARAMETERS 参数异常
//    isv.MOBILE_COUNT_OVER_LIMIT 手机号码数量超过限制
//    isv.TEMPLATE_MISSING_PARAMETERS 模板缺少变量
//    isv.BUSINESS_LIMIT_CONTROL 业务限流
//    isv.INVALID_JSON_PARAM JSON参数不合法，只接受字符串值
//    isv.BLACK_KEY_CONTROL_LIMIT 黑名单管控
//    isv.PARAM_LENGTH_LIMIT 参数超出长度限制
//    isv.PARAM_NOT_SUPPORT_URL 不支持URL

}
