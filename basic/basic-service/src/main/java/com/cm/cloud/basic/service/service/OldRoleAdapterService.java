package com.cm.cloud.basic.service.service;


import com.cm.cloud.basic.intf.pojo.User;
import com.cm.cloud.basic.intf.pojo.UserRole;

public interface OldRoleAdapterService {
    void save(UserRole userRole);

    void update(UserRole userRole);

    String select(User user);
}
