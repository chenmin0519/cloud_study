package com.cm.cloud.constant.service.controller;

import com.cm.cloud.constant.service.util.OSSClientUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/oss")
public class OssController {

    @Autowired
    private OSSClientUtils ossClient;

    @PostMapping(value = "/uploadFile",headers = "content-type=multipart/form-data")
    @ApiOperation("上传图片")
    public String uploadFile(@RequestParam("file") @ApiParam("文件") MultipartFile file) throws Exception {
        String name = file.getOriginalFilename();
        String url = ossClient.uploadFile(file.getInputStream(),"/images/other",file.getOriginalFilename());
        return url;
    }

    @PostMapping(value = "/uploadImage",headers = "content-type=multipart/form-data")
    @ApiOperation("上传商品图片")
    public String uploadImage(@RequestParam("file") @ApiParam("文件") MultipartFile file) throws Exception {
        String url = ossClient.uploadFile(file.getInputStream(),"/images/goods",file.getOriginalFilename());
        log.info(url);
        return url;
    }

    @PostMapping(value = "/uploadFriendsImage",headers = "content-type=multipart/form-data")
    @ApiOperation("上传图片")
    public String uploadFriendsImage(@RequestParam("file") @ApiParam("文件") MultipartFile file) throws Exception {
        String url = ossClient.uploadFile(file.getInputStream(),"/images/friends",file.getOriginalFilename());
        log.info(url);
        return url;
    }

    @GetMapping(value = "/geUrl")
    @ApiOperation("获取url")
    public String geUrl(@RequestParam("file") @ApiParam("请求openid必须") String key) throws Exception {
        String url = ossClient.geUrl(key);
        return url;
    }
}
