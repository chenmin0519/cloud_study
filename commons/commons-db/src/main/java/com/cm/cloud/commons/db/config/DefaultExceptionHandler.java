package com.cm.cloud.commons.db.config;

import com.cm.cloud.commons.ApiResult;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 默认异常处理
 */
@RestControllerAdvice
@Component
public class DefaultExceptionHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 未知的异常
     */
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(SQLException.class)
    public ApiResult unknownException(SQLException e) {
        logger.error("发生未知错误", e);
        return ApiResult.error(50301L, "未知的错误");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({SQLException.class})
    public ApiResult sqlException(SQLException e) {
        logger.error("发生未知错误", e);

        if (e instanceof MySQLIntegrityConstraintViolationException) {
            MySQLIntegrityConstraintViolationException cast = (MySQLIntegrityConstraintViolationException) e;
            return ApiResult.error(5002300L, e.getMessage());

        }
        return ApiResult.error(50301L, "SQL异常：" + e.getMessage());
    }

}
