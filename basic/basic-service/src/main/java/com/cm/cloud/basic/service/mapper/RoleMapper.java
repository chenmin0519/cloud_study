package com.cm.cloud.basic.service.mapper;

import com.cm.cloud.basic.intf.pojo.Role;
import com.cm.cloud.basic.intf.pojo.dto.RoleAccessDTO;
import com.cm.cloud.commons.db.CommonMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMapper extends CommonMapper<Role> {

    /**
     * 删除某个角色的数据权限
     *
     * @param roleId
     * @return
     */
    @Delete("delete from sys_role_scenic where role_id = #{roleId}")
    int deleteDataAuthority(@Param("roleId") Long roleId);

    /**
     * 新增数据权限
     *
     * @param roleId
     * @param scenics
     */
    int insertScenics(@Param("roleId") Long roleId, @Param("scenics") List<Long> scenics);

    @ResultType(Long.class)
    @Select("SELECT scenic_id from sys_role_scenic where role_id=#{roleId} ")
    List<Long> selectScenics(@Param("roleId") Long roleId);

    @ResultType(Role.class)
    @Select("SELECT sr.* FROM sys_user_role sur INNER join sys_role sr on sur.role_id=sr.id " +
            "where sur.user_id=#{userId}")
    List<Role> selectRoles(@Param("userId") Long userId);

    List<RoleAccessDTO> selectRoleAccess(@Param("serviceId") String serviceId);
}