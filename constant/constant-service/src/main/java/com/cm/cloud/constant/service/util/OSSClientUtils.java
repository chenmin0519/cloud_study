package com.cm.cloud.constant.service.util;

import com.aliyun.oss.model.*;
import com.cm.cloud.constant.service.config.OssClientConfigurer;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.IOUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Component
public class OSSClientUtils {

    private OSSClient ossClient;

    @Autowired
    private OssClientConfigurer ossClientConfigurer;

    private OSSClient getOssClient(){
        return new OSSClient(ossClientConfigurer.getEndpoint(), ossClientConfigurer.getAccessKeyId(), ossClientConfigurer.getAccessKeySecret());
    }

    /**
     * @desc 获取临时下载文件url
     * @author chenmin
     * @create 2020/03/23 19:57
     **/
    public String geUrl(String keyname) {
        // 设置URL过期时间为1小时
        Date expiration = new Date(new Date().getTime() + 3600 * 10000);
        // 生成URL
        URL url = getOssClient().generatePresignedUrl(ossClientConfigurer.getBucketName(),keyname, expiration);
        return url.toString();
    }

    /**
     * @desc 上传本地文件（文件流上传）
     * @author chenmin
     * @create 2020/03/23 19:57
     **/
    public String uploadFile(InputStream is, String directory, String fileName) {
        String key = ossClientConfigurer.getPicLocation() + directory + "/" + fileName;
        if (Objects.isNull(directory)) {
            key = fileName;
        }
        try {
            ObjectMetadata objectMetadata = getObjectMetadata(is.available());
            PutObjectResult result = getOssClient().putObject(ossClientConfigurer.getBucketName(), key, is, objectMetadata);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        } finally {
            IOUtils.safeClose(is);
        }
        return ossClientConfigurer.getAccessUrl() + key;
    }

    /**
     * @desc 上传网络图片
     * @author chenmin
     * @create 2020/03/24 15:43
     **/
    public String uploadWebFile(String fileUrl, String directory, String fileName) {
        String key = ossClientConfigurer.getPicLocation() + directory + "/" + fileName;
        InputStream is = null;
        try {
            Integer length = new URL(fileUrl).openConnection().getContentLength();
            is = new URL(fileUrl).openStream();
            ObjectMetadata objectMetadata = getObjectMetadata(length);
            getOssClient().putObject(ossClientConfigurer.getBucketName(), key, is, objectMetadata);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        } finally {
            IOUtils.safeClose(is);
        }
        return ossClientConfigurer.getAccessUrl() + key;
    }


    /**
     * @desc 上传自定义格式
     * @author chenmin
     * @create 2020/03/24 11:09
     **/
    public String upload(InputStream is, String directory, String fileType) {
        String fileName = getFileName() + "." + fileType;
        return uploadFile(is, directory, fileName);
    }

    /**
     * @desc 上传视频指定mp4格式
     * @author chenmin
     * @create 2020/03/22 15:00
     **/
    public String uploadVideo(InputStream is, String directory) {
        String fileName = getFileName() + ".mp4";
        return uploadFile(is, directory, fileName);
    }

    /**
     * @desc 上传图片指定png格式
     * @author chenmin
     * @create 2020/03/22 14:59
     **/
    public String uploadImage(InputStream is, String directory) {
        String fileName = getFileName() + ".png";
        return uploadFile(is, directory, fileName);
    }

    /**
     * @desc 上传图片不指定格式
     * @author chenmin
     * @create 2020/03/22 14:59
     **/
    public String uploadImage(InputStream is, String directory,String type) {
        String fileName = getFileName() + "."+type;
        return uploadFile(is, directory, fileName);
    }

    /**
     * @desc 上传网络图片指定png格式
     * @author chenmin
     * @create 2020/03/25 13:37
     **/
    public String uploadWebImage(String fileUrl, String directory) {
        String fileName = getFileName() + ".png";
        return uploadWebFile(fileUrl, directory, fileName);
    }



    /**
     * @desc 更新文件:只更新内容，不更新文件名和文件地址。 (因为地址没变，可能存在浏览器原数据缓存，不能及时加载新数据，例如图片更新，请注意)
     * @author chenmin
     * @create 2020/03/23 20:40
     **/
    public String updateFile(InputStream is, String fileUrl) {
        String key = getFileNameByUrl(fileUrl);
        return uploadFile(is, null, key);
    }

    /**
     * @desc 替换文件:删除原文件并上传新文件，文件名和地址同时替换 解决原数据缓存问题，只要更新了地址，就能重新加载数据)
     * @author chenmin
     * @create 2020/03/24 14:19
     **/
    public String replaceFile(InputStream is, String fileUrl) {
        boolean flag = deleteObject(fileUrl);
        String fileName = getFileNameByUrl(fileUrl);
        if (!flag) {
            return null;
        }
        return uploadFile(is, null, fileName);
    }

    /**
     * @desc 查询文件是否存在
     * @author chenmin
     * @create 2020/03/24 10:09
     **/
    public boolean doesObjectExist(String key) {
        boolean result = false;
        try {
            //如果带http,提取key值
            if (key.indexOf("http") != -1) {
                key = getFileNameByUrl(key);
            }
            result = getOssClient().doesObjectExist(ossClientConfigurer.getBucketName(), key);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * @desc 删除Object。 注意：以下所有删除如果文件不存在返回的是true,如果需要先判断是否存在先调用doesObjectExist()方法
     * @author chenmin
     * @create 2020/03/24 10:06
     **/
    public  boolean deleteObject(String fileUrl) {
        try {
            String key = getFileNameByUrl(fileUrl);
            getOssClient().deleteObject(ossClientConfigurer.getBucketName(), key);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @desc 批量删除object(适用于相同的endpoint和bucketName)
     * @author chenmin
     * @create 2020/03/24 10:02
     **/
    public  int deleteObjects(List<String> fileUrls) {
        int count = 0;
        List<String> keys = getFileNamesByUrl(fileUrls);
        try {
            // 删除Objects
            DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(ossClientConfigurer.getBucketName());
            deleteRequest.withKeys(keys);
            count = getOssClient().deleteObjects(deleteRequest).getDeletedObjects().size();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return count;
    }

    /**
     * @desc 批量文件删除(较慢, 适用于不同endpoint和bucketName)
     * @author chenmin
     * @create 2020/03/24 13:03
     **/
    public  int deleteBatchObject(List<String> fileUrls) {
        int count = 0;
        for (String url : fileUrls) {
            if (deleteObject(url)) {
                count++;
            }
        }
        return count;
    }

    /**
     * @desc web直传获取签名
     * @author chenmin
     * @create 2017/9/11 13:01
     **/
    public  Map<String, Object> getWebSign(String callbackUrl, int seconds) {
        Map<String, Object> data = Maps.newHashMap();
        // 存储目录
        String dir = ossClientConfigurer.getPicLocation() + "web/" + getDirectory();
        //回调内容
        Map<String, String> callback = Maps.newHashMap();
        callback.put("callbackUrl", callbackUrl);
        callback.put("callbackBody", "filename=${object}&size=${size}&mimeType=${mimeType}&height" +
                "=${imageInfo.height}&width=${imageInfo.width}");
        callback.put("callbackBodyType", "application/x-www-form-urlencoded");
        //签名有效期30秒过期
        Date expiration = DateTime.now().plusSeconds(seconds).toDate();
        // 提交节点
        String host = "http://" + ossClientConfigurer.getBucketName() + "." + ossClientConfigurer.getEndpoint();
        try {
            PolicyConditions policyConds = new PolicyConditions();
            // 设置上传文件的大小限制
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0,
                    1048576000);
            //指定此次上传的文件名必须是dir变量的值开头
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            String postPolicy = getOssClient().generatePostPolicy(expiration, policyConds);
            //policy
            String policy = BinaryUtil.toBase64String(postPolicy.getBytes("utf-8"));
            //签名
            String signature = getOssClient().calculatePostSignature(postPolicy);
            //回调
            String callbackData = BinaryUtil.toBase64String(callback.toString().getBytes("utf-8"));
            data.put("policy", policy);
            data.put("signature", signature);
            data.put("callback", callbackData);
            data.put("dir", dir);
            data.put("accessKeyId", ossClientConfigurer.getAccessKeyId());
            data.put("accessUrl", ossClientConfigurer.getAccessUrl());
            data.put("host", host);
            data.put("expire", expiration);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return data;
    }



    /**
     * 单个下载文件
     *
     * @param key     文件key值
     * @param fileUrl 目标文件路径名称
     * @return boolean
     * @author chenmin
     * @date 2018/2/2 18:08
     * @since 1.0.0
     */
    public  boolean getObject(String key, String fileUrl) {
        ObjectMetadata object = getOssClient().getObject(new GetObjectRequest(ossClientConfigurer.getBucketName(), key), new
                File(fileUrl));
        if (Objects.nonNull(object)) {
            return true;
        }
        return false;
    }

    /**
     * 批量下载文件
     *
     * @param preFix 下载某个文件夹中的所有
     * @param dir    目标目录
     * @return java.lang.String
     * @author chenmin
     * @date 2018/2/2 17:33
     * @since 1.0.0
     */
    public  String listObject(String preFix, String dir) {
        // 构造ListObjectsRequest请求
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(ossClientConfigurer.getBucketName());
        //Delimiter 设置为 “/” 时，罗列该文件夹下的文件
        listObjectsRequest.setDelimiter("/");
        //Prefix 设为某个文件夹名，罗列以此 Prefix 开头的文件
        listObjectsRequest.setPrefix(preFix);

        ObjectListing listing = getOssClient().listObjects(listObjectsRequest);
        //如果改目录下没有文件返回null
        if (CollectionUtils.isEmpty(listing.getObjectSummaries())) {
            return null;
        }
        // 取第一个目录的key
        File file = new File(dir + listing.getObjectSummaries().get(0).getKey());
        //判断文件所在本地路径是否存在，若无，新建目录
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        // 遍历所有Object:目录下的文件
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            //Bucket中存储文件的路径
            String key = objectSummary.getKey();
            //下载object到文件
            getOssClient().getObject(new GetObjectRequest(ossClientConfigurer.getBucketName(), key), file);
        }
        return file.getParent();
    }

    /**
     * @desc 生成文件名
     * @author chenmin
     * @create 2020/03/24 11:15
     **/
    public  String getFileName() {
        return LocalDateTime.now().toString("yyyyMMddHHmmssSSS_") + RandomStringUtils
                .randomNumeric(6);
    }

    /**
     * 生成目录
     *
     * @return java.lang.String
     * @author chenmin
     * @date 2018/1/29 19:00
     * @since 1.0.0
     */
    public  String getDirectory() {
        return LocalDateTime.now().toString("yyyy-MM-dd");
    }


    /**
     * @desc 根据url获取fileName
     * @author chenmin
     * @create 2020/03/23 20:40
     **/
    private  String getFileNameByUrl(String fileUrl) {
        int beginIndex = fileUrl.indexOf(ossClientConfigurer.getAccessUrl());
        //针对单个图片处理的图片
        int endIndex = fileUrl.indexOf("?");
        //针对使用模板图片处理的图片
        int endIndex2 = fileUrl.indexOf("@!");
        if (beginIndex == -1) {
            return null;
        }
        if (endIndex != -1) {
            return fileUrl.substring(beginIndex + ossClientConfigurer.getAccessUrl().length(), endIndex);
        }
        if (endIndex2 != -1) {
            return fileUrl.substring(beginIndex + ossClientConfigurer.getAccessUrl().length(), endIndex2);
        }
        return fileUrl.substring(beginIndex + ossClientConfigurer.getAccessUrl().length());
    }

    /**
     * @desc 根据url获取fileNames集合
     * @author chenmin
     * @create 2020/03/23 20:42
     **/
    private  List<String> getFileNamesByUrl(List<String> fileUrls) {
        List<String> fileNames = Lists.newArrayList();
        for (String url : fileUrls) {
            fileNames.add(getFileNameByUrl(url));
        }
        return fileNames;
    }

    /**
     * @desc ObjectMetaData是用户对该object的描述，
     * 由一系列name-value对组成；其中ContentLength是必须设置的，以便SDK可以正确识别上传Object的大小
     * @author chenmin
     * @create 2020/03/23 20:12
     **/
    private  ObjectMetadata getObjectMetadata(long length) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(length);
        // 被下载时网页的缓存行为
        objectMetadata.setCacheControl("no-cache");
        objectMetadata.setHeader("Pragma", "no-cache");
        return objectMetadata;
    }
}
