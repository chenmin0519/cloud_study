package com.cm.cloud.commons.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

/**
 * LocalDateTime 全局转换器
 */
//@Component
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Nullable
    @Override
    public LocalDateTime convert(String source) {

        return LocalDateTime.parse(source);
    }
}
