package com.cm.cloud.commons.util.wechat;

import com.cm.cloud.commons.excption.ServiceLogicException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;

/**
 * 微信aes 解密 小程序
 */
public class WechatAES {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
//    public static boolean initialized = false;

    public static byte[] decrypt(byte[] content, byte[] keyByte, byte[] ivByte) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            Key sKeySpec = new SecretKeySpec(keyByte, "AES");

            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));// 初始化
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceLogicException("微信解密失败:" + e.getMessage());
        }
    }

//    public static void initialize() {
//        if (initialized) return;
//
//        initialized = true;
//    }

    //生成iv
    public static AlgorithmParameters generateIV(byte[] iv) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(iv));
        return params;
    }
}
