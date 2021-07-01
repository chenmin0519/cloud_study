package com.cm.cloud.commons;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 */
@Data
@ApiModel("api通用返回对象")
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> implements Serializable {

    private static final Long LOGIC_CODE = 8001_0001L;
    private Long code;

    private String errmsg;

    private T data;

    public static <T> ApiResult<T> success(T t) {

        return new ApiResult<>(0L, "success", t);
    }

    public static <T> ApiResult<T> error(String message) {

        return new ApiResult<>(LOGIC_CODE, message, null);
    }

    public static <T> ApiResult<T> error(Long errcode, String message) {

        return new ApiResult<>(errcode, message, null);
    }

}
