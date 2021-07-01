package com.cm.cloud.commons.api;

import com.cm.cloud.commons.excption.ServiceLogicException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 异常转换
 */
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder delegate = new Default();

    /**
     * 错误解码
     */
    @Override
    public Exception decode(String methodKey, Response response) {

        //if 404
        if (response.status() == 404) {
            return new ServiceLogicException(404_001L, "服务未定义：" + methodKey);
        }
        if (response.headers().getOrDefault("Content-Type", Lists.newArrayList()).stream()
                .filter(s -> s.toLowerCase().contains("json")).count() > 0) {
            try {
                String body = Util.toString(response.body().asReader());
                JSONObject error = JSONObject.parseObject(body);

                if (error.containsKey("code") || error.containsKey("errmsg")) {

                    return new ServiceLogicException(error.getLongValue("code"), error.getString("errmsg"));
                }

                String exc = error.getString("exception");
                if (StringUtils.isNotBlank(exc)) {
                    long code = 500_001L;
                    if (error.containsKey("errcode")) {
                        code = error.getLongValue("errcode");
                    }
                    exc = exc.concat(":");
                    String msg = error.getString("message");
                    int idx = msg.indexOf(exc);
                    return new ServiceLogicException(code, idx > 0 ? msg.substring(msg.indexOf(exc) + exc.length()) :
                            msg);
                } else {
                    long code = 500_002L;
                    String msg = error.getString("message");
                    String subFlag = "Exception:";
                    int idx = msg.indexOf(subFlag);
                    return new ServiceLogicException(code, idx > 0 ? msg.substring(idx + subFlag.length()) : msg);
                }
                // 转换并返回异常对象
            } catch (IOException ex) {
                throw new RuntimeException("Failed to process response body.", ex);
            }
        }
        return delegate.decode(methodKey, response);
    }

}
