package com.cm.cloud.basic.service.controller;

import com.cm.cloud.basic.intf.clients.RoleClient;
import com.cm.cloud.basic.intf.pojo.Role;
import com.cm.cloud.basic.intf.pojo.dto.RoleAccessDTO;
import com.cm.cloud.basic.service.mapper.RoleMapper;
import com.cm.cloud.basic.service.service.OldAccessAdapterService;
import com.cm.cloud.basic.service.service.OldRoleAdapterService;
import com.cm.cloud.commons.db.controller.BasicController;
import com.cm.cloud.commons.pojo.dto.PageQuery;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/role")
@Api(value = "RoleController", description = "角色")
public class RoleController extends BasicController<Role> implements RoleClient {

    private RoleMapper roleMapper;

    @Autowired
    OldRoleAdapterService oldRoleAdapterService;

    @Autowired
    OldAccessAdapterService oldAccessAdapterService;

    @Autowired
    public RoleController(RoleMapper roleMapper) {
        super(roleMapper);
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = "/_saveAll",method = RequestMethod.POST)
    public Role saveAll(@RequestBody Role model) {
        Role role = super.save(model);
        Optional.ofNullable(model.getScenics()).ifPresent(o->{if(model.getScenics().size()!=0 && saveDataAuthority(model.getId(),model.getScenics())) role.setScenics(model.getScenics());});
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = "/_update",method = RequestMethod.PATCH)
    public Role updateAllSelective(@RequestBody Role model) {
        Role role = super.updateSelective(model);
        Optional.ofNullable(model.getScenics()).ifPresent(o->{if(model.getScenics().size()!=0 && this.saveDataAuthority(model.getId(),model.getScenics())) role.setScenics(model.getScenics());});
        return role;
    }

    @Override
    @RequestMapping(value = "/{id}/scenics", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDataAuthority(@PathVariable("id") Long roleId, @RequestBody List<Long> scenics) {

        roleMapper.deleteDataAuthority(roleId);
        roleMapper.insertScenics(roleId, scenics);
        return true;
    }

    @Override
    @ApiOperation("加载数据权限")
    @RequestMapping(value = "/{id}/scenics", method = RequestMethod.GET)
    public List<Long> getSaveDataAuthority(@PathVariable("id") Long roleId) {
        return roleMapper.selectScenics(roleId);
    }


    @Override
    @ApiOperation("加载角色资源")
    @RequestMapping(value = "/access", method = RequestMethod.GET)
    public List<RoleAccessDTO> roleAccess(@RequestParam("serviceId") String serviceId) {
        return roleMapper.selectRoleAccess(serviceId);
    }

    @Override
    @RequestMapping(value = "/_search_page", method = RequestMethod.POST)
    public PageInfo<Role> loadPage(@RequestBody PageQuery<Role> query) {
        PageInfo<Role> rolePageInfo = super.loadPage(query);
        rolePageInfo.getList().stream().map(role -> {
            role.setScenics(roleMapper.selectScenics(role.getId())); return role;}).collect(Collectors.toList());
        return rolePageInfo;
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Role loadById(@PathVariable("id") Long id) {
        Role role=super.loadById(id);
        role.setScenics(this.getSaveDataAuthority(role.getId()));
        return role;
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public boolean delete(@PathVariable("id") Long id) {
        oldAccessAdapterService.delete(id);
        return super.delete(id);
    }
}

