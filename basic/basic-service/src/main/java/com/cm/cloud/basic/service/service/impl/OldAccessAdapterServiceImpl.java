package com.cm.cloud.basic.service.service.impl;


import com.cm.cloud.basic.intf.pojo.RoleAccess;
import com.cm.cloud.basic.intf.pojo.SysAccessRole;
import com.cm.cloud.basic.intf.pojo.vo.RoleAccessVO;
import com.cm.cloud.basic.service.mapper.SysAccessMapper;
import com.cm.cloud.basic.service.mapper.SysAccessRoleMapper;
import com.cm.cloud.basic.service.service.OldAccessAdapterService;
import com.cm.cloud.commons.db.util.ExampleBuiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OldAccessAdapterServiceImpl implements OldAccessAdapterService {

    @Autowired
    SysAccessRoleMapper sysAccessRoleMapper;

    @Autowired
    SysAccessMapper sysAccessMapper;

    @Override
    public RoleAccessVO select(Long roleId) {
        RoleAccessVO roleAccessVO = new RoleAccessVO();
        String accessId = "";
        Example example = ExampleBuiler.cls(SysAccessRole.class);
        example.createCriteria().andEqualTo("roleId",roleId);
        List<SysAccessRole> sysUserRoleList = sysAccessRoleMapper.selectByExample(example);
        if (sysUserRoleList.size()>0)
            for (SysAccessRole temp : sysUserRoleList ) {
                accessId += temp.getAccessId().toString() + ",";
            }
        else
            return null;
        roleAccessVO.setAccessId(accessId.substring(0,accessId.length()-1));
        roleAccessVO.setSysAccessList(sysAccessRoleMapper.selectAccessByRoleId(roleId));
        return roleAccessVO;
    }

    @Override
    public Boolean insert(RoleAccess roleAccess) {
        List<SysAccessRole> sysAccessRoleList = new ArrayList<>();
        String[] temp = Optional.ofNullable(roleAccess.getAccessId()).orElse("").split(",");
        for (String i: temp ) {
            SysAccessRole sysAccessRole = new SysAccessRole();
            sysAccessRole.setAccessId(Long.valueOf(i));
            sysAccessRole.setRoleId((roleAccess.getId()));
            sysAccessRoleList.add(sysAccessRole);
        }
        sysAccessRoleMapper.insertList(sysAccessRoleList);
        return true;
    }

    @Override
    public Boolean delete(Long roleId) {
        Example example = ExampleBuiler.cls(SysAccessRole.class);
        example.createCriteria().andEqualTo("roleId",roleId);
        sysAccessRoleMapper.deleteByExample(example);
        return true;
    }
}
