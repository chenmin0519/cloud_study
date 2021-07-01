package com.cm.cloud.basic.service.mapper;

import com.cm.cloud.basic.intf.pojo.SysAccess;
import com.cm.cloud.basic.intf.pojo.SysAccessRole;
import com.cm.cloud.commons.db.CommonMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysAccessRoleMapper extends CommonMapper<SysAccessRole> {
    List<SysAccess> selectAccessByRoleId(@Param("roleId") Long roleId);
}
