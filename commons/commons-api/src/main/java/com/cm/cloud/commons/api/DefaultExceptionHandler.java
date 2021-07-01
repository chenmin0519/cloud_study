package com.cm.cloud.commons.api;

import com.cm.cloud.commons.ApiResult;
import com.cm.cloud.commons.excption.ParameterNotValidException;
import com.cm.cloud.commons.excption.ServiceLogicException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import feign.RetryableException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Optional;

/**
 * 默认异常处理
 */
@RestControllerAdvice
@Component
public class DefaultExceptionHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());


//    /**
//     * 处理所有不可知的异常
//     * @param e
//     * @return
//     */
//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    ApiResult handlerException(Exception e){
//        if(e instanceof ServiceLogicException){
//            return ApiResult.error(((ServiceLogicException) e).getErrcode(),e.getMessage());
//        }
//        e.printStackTrace();
//        //未知异常
//        return ApiResult.error(600L,"系统异常");
//    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(value = {
            HttpMediaTypeNotSupportedException.class})
    public ApiResult notSupportedMediaTypeException(Exception e) {
        String message = e.getMessage();
        return ApiResult.error(41501L, message);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(value = {
            HttpRequestMethodNotSupportedException.class})
    public ApiResult notAllowedException(Exception e) {
        String message = e.getMessage();
        return ApiResult.error(40501L, message);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(value = {
            HttpMediaTypeNotAcceptableException.class})
    public ApiResult notAcceptableException(Exception e) {
        String message = e.getMessage();
        return ApiResult.error(40601L, message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {
//            NoSuchRequestHandlingMethodException.class,
            NoHandlerFoundException.class})
    public ApiResult notFoundException(Exception e) {
        String message = "找不到资源异常";
       /* if (e instanceof NoSuchRequestHandlingMethodException) {
            message = e.getMessage();
        } else*/
        if (e instanceof NoHandlerFoundException) {
            NoHandlerFoundException nhfe = (NoHandlerFoundException) e;
            message = nhfe.getHttpMethod() + "\t" + nhfe.getRequestURL() + "\n" + nhfe.getMessage();
        }
        return ApiResult.error(40401L, message);
    }


    /**
     * 参数校验异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestPartException.class,
            BindException.class,
            ParameterNotValidException.class})
    public ApiResult argumentValidException(Exception e) {
        String message = "参数异常";
        if (e instanceof MissingServletRequestParameterException) {
            message = e.getMessage();
        } else if (e instanceof ServletRequestBindingException) {
            message = e.getMessage();
        } else if (e instanceof TypeMismatchException) {
            message = e.getMessage();
        } else if (e instanceof HttpMessageNotReadableException) {
            message = e.getMessage();
        } else if (e instanceof HttpMessageNotWritableException) {
            message = e.getMessage();
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException manv = (MethodArgumentNotValidException) e;
            message = manv.getBindingResult().getFieldError().getDefaultMessage();
        } else if (e instanceof MissingServletRequestPartException) {
            message = e.getMessage();
        } else if (e instanceof BindException) {
            BindException be = (BindException) e;
            message = be.getFieldError().getDefaultMessage();
        } else if (e instanceof ParameterNotValidException) {
            message = e.getMessage();
        }
        return ApiResult.error(40001L, message);
    }

    /**
     * RPC 异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {
            ConversionNotSupportedException.class,
            ServiceLogicException.class,
            RetryableException.class,
            HystrixRuntimeException.class})
    public ApiResult feignException(Exception e) {
        String message = "服务器内部异常";
        logger.error("feignException:", e);
        if (e instanceof RetryableException) {
            message = e.getMessage();
        } else if (e instanceof ConversionNotSupportedException) {
            message = e.getMessage();
        } else if (e instanceof ServiceLogicException) {
            ServiceLogicException le = (ServiceLogicException) e;
            return ApiResult.error(Optional.ofNullable(le.getErrcode()).orElse(50001L), le.getMessage());
        } else if (e instanceof HystrixRuntimeException) {
            Throwable real = e;
            while (real.getCause() != null) {
                real = real.getCause();
            }
            message = real.getMessage();
        }
        return ApiResult.error(50001L, message);
    }


    /**
     * 未知的异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ApiResult runtime(Exception e) {

       //ClientException
        String message = e.getMessage();
        if (StringUtils.isNotBlank(message)) {
            if (message.startsWith("com.netflix.client.ClientException")) {
                String str = "Load balancer does not have available server for client";
                if (message.contains("Load balancer does not have available server for client")) {
                    return ApiResult.error(50404L, "底层服务未启动:" +
                            message.substring(message.indexOf(str) + str.length()));
                }

                return ApiResult.error(50405L, message);
            }
        }
        logger.error("发生未知错误", e);
        return ApiResult.error(50303L, "未知的错误:" + e.getMessage());
    }

    /**
     * 未知的异常
     */
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(Exception.class)
    public ApiResult unknownException(Exception e) {
        logger.error("发生未知错误", e);
        return ApiResult.error(50301L, "未知的错误:" + e.getMessage());
    }

}
