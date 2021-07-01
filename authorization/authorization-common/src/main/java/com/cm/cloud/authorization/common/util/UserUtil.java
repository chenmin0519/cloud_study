package com.cm.cloud.authorization.common.util;

import com.cm.cloud.authorization.common.userdetails.UserDetails;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Optional;

public final class UserUtil {

    private static final String PRINCIPAL_KEY = "principal";

    /**
     * 从Principal对象解析UserDetails
     *
     * @param principal
     * @return
     */
    private static UserDetails getUserDetail(Principal principal) {
        if (principal == null) {
            return null;
        }
        if (principal instanceof OAuth2Authentication) {
            LinkedHashMap details = (LinkedHashMap) ((OAuth2Authentication) principal).getUserAuthentication()
                    .getDetails();
            if (!details.containsKey(PRINCIPAL_KEY))
                return null;

            UserDetails userDetails = new UserDetails();
            BeanMap beanMap = BeanMap.create(userDetails);
            LinkedHashMap hashMap = (LinkedHashMap) details.get(PRINCIPAL_KEY);
            hashMap.put("id", Long.valueOf(hashMap.get("id").toString()));
            hashMap.put("userId", Long.valueOf(hashMap.get("userId").toString()));
            beanMap.putAll(hashMap);
            return userDetails;
        } else if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        } else {
            return null;
        }
    }

    /**
     * 从SecurityContextHolder解析UserDetails
     *
     * @return
     */
    private static UserDetails getUserDetail() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication) {
            return null;
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return getUserDetail(authentication);
    }

    /**
     * 获取当前用户id
     *
     * @param principal
     * @return
     */
    public static Long getUserId(Principal principal) {
        return Optional.ofNullable(getUserDetail(principal)).orElse(new UserDetails()).getUserId();
    }

    /**
     * 获取当前用户id
     *
     * @return
     */
    public static Long getUserId() {
        return Optional.ofNullable(getUserDetail()).orElse(new UserDetails()).getUserId();
    }

    /**
     * 获取认证用户资料
     *
     * @param principal
     * @return
     */
    public static UserDetails getUser(Principal principal) {
        return Optional.ofNullable(getUserDetail(principal)).orElse(new UserDetails());
    }


    /**
     * 获取认证用户资料
     *
     * @return
     */
    public static UserDetails getUser() {
        return Optional.ofNullable(getUserDetail()).orElse(new UserDetails());
    }

    /**
     * 获取当前用户名
     *
     * @param principal
     * @return
     */
    public static String getUserName(Principal principal) {
        return Optional.ofNullable(getUserDetail(principal)).orElse(new UserDetails()).getUsername();
    }

    /**
     * 获取当前用户名
     *
     * @return
     */
    public static String getUserName() {
        return Optional.ofNullable(getUserDetail()).orElse(new UserDetails()).getUsername();
    }

//    /**
//     * 判断是否拥有权限
//     *
//     * @param role
//     * @return
//     */
//    public static boolean isGranted(String role) {
//        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null && auth.getPrincipal() != null) {
//            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
//            if (authorities == null) {
//                return false;
//            } else {
//                Iterator var4 = authorities.iterator();
//                GrantedAuthority grantedAuthority;
//                do {
//                    if (!var4.hasNext()) {
//                        return false;
//                    }
//                    grantedAuthority = (GrantedAuthority) var4.next();
//                } while (!role.equals(grantedAuthority.getAuthority()));
//                return true;
//            }
//        } else {
//            return false;
//        }
//    }


    /**
     * 判断当前用户是不是超级管理员
     *
     * @return
     */
    public static boolean isCurrentSuperusr() {
        return Optional.ofNullable(getUserDetail()).orElse(new UserDetails()).isSuperUser();
    }

    /**
     * 判断当前用户是不是超级管理员
     *
     * @param principal
     * @return
     */
    public static boolean isCurrentSuperusr(Principal principal) {
        return Optional.ofNullable(getUserDetail(principal)).orElse(new UserDetails()).isSuperUser();
    }

    /**
     * 获取当前用户主要角色
     *
     * @return
     */
    public static Integer getCurrentPrimaryRole() {
        return Optional.ofNullable(getUserDetail()).orElse(new UserDetails()).getPrimaryRole();
    }

    /**
     * 获取当前用户主要角色
     *
     * @param principal
     * @return
     */
    public static Integer getCurrentPrimaryRole(Principal principal) {
        return Optional.ofNullable(getUserDetail(principal)).orElse(new UserDetails()).getPrimaryRole();
    }

}
