package com.cm.cloud.basic.intf.clients;

import com.cm.cloud.basic.intf.BasicConstant;
import com.cm.cloud.basic.intf.pojo.Role;
import com.cm.cloud.basic.intf.pojo.dto.RoleAccessDTO;
import com.cm.cloud.comons.intf.BasicClient;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(value = BasicConstant.SERVICE_NAME)
@RequestMapping("/role")
public interface RoleClient extends BasicClient<Role> {

    @ApiOperation("保存数据权限")
    @RequestMapping(value = "/{id}/scenics", method = RequestMethod.POST)
    boolean saveDataAuthority(@PathVariable("id") Long roleId, @RequestBody List<Long> scenics);

    @ApiOperation("新增角色，并设置数据权限")
    @RequestMapping(value = "/_saveAll",method = RequestMethod.POST)
    Role saveAll(@RequestBody Role model);

    @ApiOperation("部分修改角色，（包括数据权限修改）")
    @RequestMapping(value = "/_update",method = RequestMethod.PATCH)
    Role updateAllSelective(@RequestBody Role model);

    @ApiOperation("加载数据权限")
    @RequestMapping(value = "/{id}/scenics", method = RequestMethod.GET)
    List<Long> getSaveDataAuthority(@PathVariable("id") Long roleId);

    @ApiOperation("加载角色资源")
    @RequestMapping(value = "/access", method = RequestMethod.GET)
    List<RoleAccessDTO> roleAccess(@RequestParam("serviceId") String serviceId);
}

