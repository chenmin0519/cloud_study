package com.cm.cloud.constant.intf.client;

import com.cm.cloud.constant.intf.ConstantContonts;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@FeignClient(value = ConstantContonts.SERVICE_NAME)
@RequestMapping("/oss")
public interface OssClient {

    @PostMapping(value = "/upload_file",headers = "content-type=multipart/form-data")
    @ApiOperation("上传文件")
    @ResponseBody
    String uploadFile(@RequestParam("file") @ApiParam("文件") MultipartFile file);

    @PostMapping(value = "/upload_image",headers = "content-type=multipart/form-data")
    @ApiOperation("上传商品图片")
    @ResponseBody
    String uploadImage(@RequestParam("file") @ApiParam("文件") MultipartFile file);
}
