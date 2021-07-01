package com.cm.cloud.basic.service.service;


import com.cm.cloud.basic.intf.pojo.RoleAccess;
import com.cm.cloud.basic.intf.pojo.vo.RoleAccessVO;

public interface OldAccessAdapterService {

    RoleAccessVO select(Long roleId);

    Boolean insert(RoleAccess roleAccess);

    Boolean delete(Long roleId);
}
