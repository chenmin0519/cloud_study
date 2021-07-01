package com.cm.cloud.commons.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;

import java.io.File;
import java.io.InputStream;

/**
 * OSS 工具类
 */
public class OssUtil {

    private static OSSClient ossClient;

    static {
        ossClient = new OSSClient(OSSConfig.ENDPOINT, new DefaultCredentialProvider(OSSConfig.ACCESS_KEY_ID,
                OSSConfig.ACCESS_KEY_SECRET), null);
    }

    /**
     * @param file 需要上传的文件
     * @param key  文件key在bucket 下的存储位置(不能以 <code>/</code> 开头) e.g. userid/reportname.xls
     * @return
     */
    public static boolean upload2OSS(File file, String key) {
        return upload2OSS(file, key, OSSConfig.UPLOAD_DEFAULT_BUCKET);
    }

    /**
     * @param is  需要上传的文件流
     * @param key 文件key在bucket 下的存储位置(不能以 <code>/</code> 开头) e.g. userid/reportname.xls
     */
    public static boolean upload2OSS(InputStream is, String key) {
        return upload2OSS(is, key, OSSConfig.UPLOAD_DEFAULT_BUCKET);
    }


    /**
     * @param file       需要上传的文件
     * @param key        文件key在bucket 下的存储位置(不能以 <code>/</code> 开头) e.g. userid/reportname.xls
     * @param bucketName
     * @return
     */
    public static boolean upload2OSS(File file, String key, String bucketName) {
        return ossClient.putObject(bucketName, key, file) != null;
    }

    /**
     * @param file       需要上传的文件
     * @param key        文件key在bucket 下的存储位置(不能以 <code>/</code> 开头) e.g. userid/reportname.xls
     * @param bucketName OSS中的bucketName
     * @return
     */
    public static boolean upload2OSS(InputStream file, String key, String bucketName) {
        return ossClient.putObject(bucketName, key, file) != null;

    }

    /**
     * 删除文件
     *
     * @return
     */
    public static boolean deleteFile(String key, String bucketName) {
        ossClient.deleteObject(bucketName, key);
        return true;
    }

    /**
     * 删除文件
     *
     * @return
     */
    public static boolean deleteFile(String key) {
        ossClient.deleteObject(OSSConfig.UPLOAD_DEFAULT_BUCKET, key);
        return true;
    }

    public static String getBucketDomain() {
        return OSSConfig.BUCKET_DOMAIN;
    }

    protected interface OSSConfig {

        String ACCESS_KEY_ID = "LTAIyF6QK71liY5G";

        String ACCESS_KEY_SECRET = "gxtu4BHLogLzgYRMEb4XfOD82waBP8";

        String ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";

        String BUCKET_DOMAIN = "http://ai-static.oss-cn-hangzhou.aliyuncs.com";

        String UPLOAD_DEFAULT_BUCKET = "ai-static";
    }

}
