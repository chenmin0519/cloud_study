package com.cm.cloud.basic.service.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.cm.cloud.basic.intf.clients.UserClient;
import com.cm.cloud.basic.intf.enums.EnumUserType;
import com.cm.cloud.basic.intf.pojo.Role;
import com.cm.cloud.basic.intf.pojo.User;
import com.cm.cloud.basic.intf.pojo.dto.UserAuthority;
import com.cm.cloud.basic.intf.pojo.dto.WechatLoginVO;
import com.cm.cloud.basic.intf.pojo.dto.WechatOpenidDTO;
import com.cm.cloud.basic.intf.pojo.dto.WeixinUserInfoDTO;
import com.cm.cloud.basic.intf.pojo.vo.UserVO;
import com.cm.cloud.basic.service.mapper.RoleMapper;
import com.cm.cloud.basic.service.mapper.UserMapper;
import com.cm.cloud.basic.service.service.UserExtendsTableBandService;
import com.cm.cloud.basic.service.util.WechatCommonClient;
import com.cm.cloud.basic.service.util.WechatMiniClient;
import com.cm.cloud.commons.db.controller.BasicController;
import com.cm.cloud.commons.db.util.ExampleBuiler;
import com.cm.cloud.commons.enums.EnumState;
import com.cm.cloud.commons.excption.ParameterNotValidException;
import com.cm.cloud.commons.pojo.dto.PageQuery;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;


@RestController
@Api(description = "用户管理")
@RequestMapping("/user")
@Slf4j
public class UserController extends BasicController<User> implements UserClient {

    @Autowired
    UserExtendsTableBandService userExtendsTableBandService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private WechatCommonClient wechatCommonClient;
    @Autowired
    private WechatMiniClient wechatMiniClient;

    @Autowired
    public UserController(UserMapper userMapper) {
        super(userMapper);
        this.userMapper = userMapper;
    }

    /**
     * 新增
     *
     * @return
     */
    @Override
    @RequestMapping(value = "", method = RequestMethod.POST)
    public User save(@RequestBody User model) {

        //处理密码
        if (StringUtils.isNotBlank(model.getPassword())) {
            model.setPassword(passwordEncoder.encode(model.getPassword().trim()));
        }

        //如果没有用户名 设置用户名为手机号
        if (StringUtils.isBlank(model.getUsername())
                && StringUtils.isNotBlank(model.getPhone())) {
            model.setUsername(model.getPhone());
        }

        if (StringUtils.isBlank(model.getAvatar())) {
            model.setAvatar("https://aistatic-dev.huiqulx.com/avatar/defaults.png");
        }
        return super.save(model);
    }

    @Override
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public UserVO mySave(@RequestBody User model) {
        //处理密码
        if (StringUtils.isNotBlank(model.getPassword())) {
            model.setPassword(passwordEncoder.encode(model.getPassword().trim()));
        }

        //如果没有用户名 设置用户名为手机号
        if (StringUtils.isBlank(model.getUsername())
                && StringUtils.isNotBlank(model.getPhone())) {
            model.setUsername(model.getPhone());
        }
        if (StringUtils.isBlank(model.getAvatar())) {
            model.setAvatar("https://aistatic-dev.huiqulx.com/avatar/defaults.png");
        }
        model.setId(super.save(model).getId());
        return userExtendsTableBandService.saveExtends(model);

    }

    /**
     * 全量修改
     *
     * @return
     */
    @Override
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public User update(@RequestBody User model) {

        //处理密码
        if (StringUtils.isNotBlank(model.getPassword())) {
            model.setPassword(passwordEncoder.encode(model.getPassword().trim()));
        }
        return super.update(model);
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User loadById(@PathVariable("id") Long id) {
        return super.loadById(id);
    }

    /**
     * 局部修改
     *
     * @param model
     * @return
     */

    @Override
    @RequestMapping(value = "", method = RequestMethod.PATCH)
    public User updateSelective(@RequestBody User model) {
        //处理密码
        if (StringUtils.isNotBlank(model.getPassword())) {
            model.setPassword(passwordEncoder.encode(model.getPassword().trim()));
        }
        return super.updateSelective(model);
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.GET)
    public User findByUsernameOrPhone(@RequestParam("username") String username) {
        Example example = ExampleBuiler.cls(User.class);
        example.createCriteria().andEqualTo("phone", username)
                .orEqualTo("username", username);

        List<User> users = userMapper.selectByExample(example);

        if (users.size() > 0) {

            if (users.size() > 1) {
                log.warn("通过用户名或手机号码查询发现多行数据:%s", username);
            }
            return users.get(0);
        }

        return null;
    }

    @Override
    @RequestMapping(value = "/wechat", method = RequestMethod.POST)
    @ApiOperation("通过微信查询")
    public User addByWeixin(@RequestBody WechatLoginVO loginVO) {
        String currentOpenId = "";
        WeixinUserInfoDTO userInfoDTO = null;
        try {
            if ("MINI".equalsIgnoreCase(loginVO.getSource())) {

                WechatOpenidDTO dto = wechatMiniClient.jscode2session(loginVO.getCode(), loginVO.getAppid());
                log.info("小程序微信登录:" + dto);
                currentOpenId = dto.getOpenid();
                String unionid = StringUtils.isNotBlank(dto.getUnionid()) ? dto.getUnionid() : dto.getOpenid();

                if (StringUtils.isNotBlank(unionid)) {

                    userInfoDTO = new WeixinUserInfoDTO();
                    userInfoDTO.setOpenid(dto.getOpenid());
                    userInfoDTO.setNickname(loginVO.getNickName());
                    userInfoDTO.setHeadimgurl(loginVO.getAvatarUrl());
                    userInfoDTO.setUnionid(unionid);
                    userInfoDTO.setSessionKey(dto.getSession_key());
                }

            } else {
                if (StringUtils.isBlank(loginVO.getUnionid())) {
                    throw new ParameterNotValidException("unionid不能为空！");
                }
                userInfoDTO = new WeixinUserInfoDTO();
                userInfoDTO.setNickname(loginVO.getNickName());
                userInfoDTO.setHeadimgurl(loginVO.getAvatarUrl());
                userInfoDTO.setUnionid(loginVO.getUnionid());
                userInfoDTO.setOpenid(loginVO.getUnionid());
//                userInfoDTO = wechatClient.code2UserInfo(loginVO.getCode());
            }
        } catch (Exception e) {
            log.error("获取微信信息失败", e);
            throw new ParameterNotValidException("获取微信信息失败！");
        }

        User query = new User();
        query.setUsername(userInfoDTO.getUnionid());
        User result = userMapper.selectOne(query);

        JSONObject json = new JSONObject();
        json.put(loginVO.getAppid(), currentOpenId);
        if (Objects.nonNull(result)) {
            if ("MINI".equalsIgnoreCase(loginVO.getSource())) {

                User update = new User();
                update.setId(result.getId());
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result.getWxOpenid());
                    if (!jsonObject.containsKey(loginVO.getAppid())) {
                        jsonObject.put(loginVO.getAppid(), currentOpenId);
                        update.setWxOpenid(jsonObject.toJSONString());
                        this.updateVOSelective(update);
                    }
                } catch (JSONException a) {//老数据 不是json

                    if (StringUtils.isNotBlank(currentOpenId)) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(loginVO.getAppid(), currentOpenId);
                        update.setWxOpenid(jsonObject.toJSONString());
                        this.updateSelective(update);
                    }
                }

            }
            result.setSessionKey(userInfoDTO.getSessionKey());
            result.setMiniOpenId(userInfoDTO.getOpenid());
            return result;
        }
        User user = new User();
        user.setNickname(userInfoDTO.getNickname());
        user.setAvatar(userInfoDTO.getHeadimgurl());
        user.setWxOpenid(json.toJSONString());
        user.setUserType(EnumUserType.APP.getKey());
        user.setUsername(userInfoDTO.getUnionid());
        user.setSource("WX-" + loginVO.getSource());
        user.setState(EnumState.NORMAL.getKey());
        user.setLastLoginTime(LocalDateTime.now());
        user.setSessionKey(userInfoDTO.getSessionKey());
        user.setMiniOpenId(json.toJSONString());
        return this.save(user);
    }

    @Override
    @ApiOperation("通过主键in查询")
    @RequestMapping(value = "/_search_ids", method = RequestMethod.POST)
    public List<User> loadByIds(@RequestBody List<Long> ids) {

        Example example = new Example(User.class);
        example.createCriteria().andIn("id", ids);
        return userMapper.selectByExample(example);
    }

    @Override
    @ApiOperation("获取当前用户信息")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.POST)
    public UserVO getUserInfo(@RequestParam("id") Long id) {
        return Optional.ofNullable(userMapper.selectByPrimaryKey(id)).map
                (userExtendsTableBandService::selectAllExtends).orElse(null);
    }

    @Override
    @ApiOperation("分页查询")
    @RequestMapping(value = "/_search_page", method = RequestMethod.POST)
    public PageInfo<User> loadPage(@RequestBody PageQuery<User> query) {
        PageInfo<User> userPageInfo = super.loadPage(query);
        userPageInfo.getList().forEach(user -> {
            userExtendsTableBandService.selectAllExtends(user);
        });
        return userPageInfo;
    }

    @Override
    @ApiOperation("分页查询用户所有数据")
    @RequestMapping(value = "/search_user_info", method = RequestMethod.POST)
    public PageInfo<UserVO> loadVoPage(@RequestBody PageQuery<User> query) {
        PageInfo<User> userPageInfo = super.loadPage(query);
        PageInfo<UserVO> userVOPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(userPageInfo, userVOPageInfo);
        userVOPageInfo.setList(userPageInfo.getList().stream().map(user -> userExtendsTableBandService.selectExtends
                (user)).collect(toList()));
        return userVOPageInfo;
    }

    @Override
    @ApiOperation("部分修改用户数据")
    @RequestMapping(value = "/_update", method = RequestMethod.PATCH)
    public UserVO updateVOSelective(@RequestBody User user) {
        user = updateSelective(user);
        return userExtendsTableBandService.updateExtends(user);
    }

    @ApiOperation("获取用户角色")
    @Override
    @RequestMapping(value = "/{userId}/roles", method = RequestMethod.GET)
    public List<Role> selectUserRole(@PathVariable("userId") Long userId) {

        return roleMapper.selectRoles(userId);
    }

    @Override
    @ApiOperation("获取用户权限(角色 数据)")
    @RequestMapping(value = "/{userId}/authority", method = RequestMethod.GET)
    public UserAuthority selectUserAuthority(@PathVariable("userId") Long userId) {

        List<Role> roles = this.selectUserRole(userId);
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setRoles(roles);
        if (CollectionUtils.isEmpty(roles)) {
            userAuthority.setScenics(Collections.emptyList());
            return userAuthority;
        }
        List<Long> scenics = roles.stream().map(Role::getId).map(roleMapper::selectScenics)
                .reduce(new ArrayList<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                }).stream().distinct().collect(toList());
        userAuthority.setScenics(scenics);
        return userAuthority;
    }

}
