package com.cm.cloud.basic.service.service;


import com.cm.cloud.basic.intf.pojo.User;
import com.cm.cloud.basic.intf.pojo.vo.UserVO;

public interface UserExtendsTableBandService {

    UserVO saveExtends(User user);

    UserVO updateExtends(User user);

    UserVO selectExtends(User user);

    UserVO selectAllExtends(User user);
}
