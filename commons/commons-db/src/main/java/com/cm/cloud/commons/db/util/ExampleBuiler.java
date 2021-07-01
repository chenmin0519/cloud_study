package com.cm.cloud.commons.db.util;

import tk.mybatis.mapper.entity.Example;


public class ExampleBuiler {

    public static Example cls(Class<?> clazz) {

        return new Example(clazz);
    }
}
