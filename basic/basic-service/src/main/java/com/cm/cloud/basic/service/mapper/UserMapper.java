package com.cm.cloud.basic.service.mapper;

import com.cm.cloud.basic.intf.pojo.User;
import com.cm.cloud.commons.db.CommonMapper;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper extends CommonMapper<User> {
}