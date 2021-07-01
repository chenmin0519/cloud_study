package com.cm.cloud.commons.util.wechat;

import com.cm.cloud.commons.excption.ParameterNotValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

/**
 */
@Slf4j
public class WechatCode {

    /**
     * 解密数据
     *
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptedData, String sessionKey, String iv) {
        String result = null;
        try {
            byte[] resultByte = WechatAES.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey),
                    Base64.decodeBase64(iv));
            if (null != resultByte && resultByte.length > 0) {
                result = new String(WxPKCS7Encoder.decode(resultByte));
            }
        } catch (Exception e) {
            log.error("微信解密失败", e);
            log.warn("微信解密失败encryptedData:{}", encryptedData);
            log.warn("微信解密失败sessionKey:{}", sessionKey);
            log.warn("微信解密失败iv:{}", iv);
            throw new ParameterNotValidException("微信解密失败:" + e.getMessage());
        }
        return result;
    }

}
