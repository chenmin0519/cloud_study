package com.cm.cloud.basic.intf.clients;

import com.cm.cloud.basic.intf.BasicConstant;
import com.cm.cloud.basic.intf.pojo.Role;
import com.cm.cloud.basic.intf.pojo.User;
import com.cm.cloud.basic.intf.pojo.dto.UserAuthority;
import com.cm.cloud.basic.intf.pojo.dto.WechatLoginVO;
import com.cm.cloud.basic.intf.pojo.vo.UserVO;
import com.cm.cloud.commons.pojo.dto.PageQuery;
import com.cm.cloud.comons.intf.BasicClient;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(value = BasicConstant.SERVICE_NAME)
@RequestMapping("/user")
public interface UserClient extends BasicClient<User> {

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    UserVO mySave(@RequestBody User model);

    @ApiOperation("获取当前用户信息")
    @RequestMapping(value = "/info/{id}",method = RequestMethod.POST)
    UserVO getUserInfo(@RequestParam("id") Long id);

    @RequestMapping(value = "", method = RequestMethod.GET)
    User findByUsernameOrPhone(@RequestParam("username") String username);

    @RequestMapping(value = "/wechat", method = RequestMethod.POST)
    @ApiOperation("通过微信查询")
    User addByWeixin(@RequestBody WechatLoginVO loginVO);

    @ApiOperation("通过主键in查询")
    @RequestMapping(value = "/_search_ids", method = RequestMethod.POST)
    List<User> loadByIds(@RequestBody List<Long> ids);

    @ApiOperation("分页查询用户所有数据")
    @RequestMapping(value = "/search_user_info",method = RequestMethod.POST)
    PageInfo<UserVO> loadVoPage(@RequestBody PageQuery<User> query);

    @ApiOperation("部分修改用户所有数据")
    @RequestMapping(value = "/_update",method = RequestMethod.PATCH)
    UserVO updateVOSelective(@RequestBody User user);

    @ApiOperation("获取用户角色")
    @RequestMapping(value = "/{userId}/roles", method = RequestMethod.GET)
    List<Role> selectUserRole(@PathVariable("userId") Long userId);

    @ApiOperation("获取用户权限")
    @RequestMapping(value = "/{userId}/authority", method = RequestMethod.GET)
    UserAuthority selectUserAuthority(@PathVariable("userId") Long userId);
}
