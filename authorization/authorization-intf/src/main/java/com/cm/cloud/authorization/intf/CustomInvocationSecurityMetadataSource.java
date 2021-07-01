package com.cm.cloud.authorization.intf;

import com.cm.cloud.basic.intf.clients.RoleClient;
import com.cm.cloud.basic.intf.pojo.dto.RoleAccessDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
    * @Description: TODO
    * @Author:      chenmin
    * @CreateDate:  2019-06-29 2019-06-29
    * @Version:     1.0
    * @JDK:         10
    */
@Service
@Slf4j
public class CustomInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {


    public CustomInvocationSecurityMetadataSource() {
        //超级管理员
        Collection<ConfigAttribute> array = new ArrayList<>();
        ConfigAttribute cfg = new SecurityConfig("ROLE_ADMIN");
        array.add(cfg);
        HashMap<String, Collection<ConfigAttribute>> map = new HashMap<>();
        map.put("/**", array);

        //TODO Ignored is must anonymous
        ConfigAttribute anonymous = new SecurityConfig("ROLE_ANONYMOUS");
        this.initMap = map;

    }

    private Map<String, Collection<ConfigAttribute>> map = new ConcurrentHashMap<>();
    private boolean isloadSuccess = false;

    @Value("${spring.application.name}")
    private String serviceId = null;

    @Value("${security.ignored:/css/**, /js/**,/images/**, /webjars/**, /**/favicon.ico,/assets/**}")
    String[] antPatterns;

    @Autowired
    private RoleClient roleClient;

//    @PostConstruct
//    public void post() {
//        loadResourceDefine();
//    }

    /**
     * 初始配置
     */
    private HashMap<String, Collection<ConfigAttribute>> initMap;

    public void reload() {

        isloadSuccess = false;
        loadResourceDefine();
    }

    private boolean isIgnored(HttpServletRequest request) {

        if (Objects.isNull(antPatterns) || antPatterns.length == 0) {
            return false;
        }

        for (String igored : antPatterns) {
            AntPathRequestMatcher matcher = new AntPathRequestMatcher(igored);
            if (matcher.matches(request)) {
                return true;
            }
        }
        if (new AntPathRequestMatcher("/actuator/**").matches(request)) {
            return true;
        }
        return false;
    }

    /**
     * 加载资源，初始化资源变量
     */
    public synchronized void loadResourceDefine() {
        if (isloadSuccess) {
            return;
        }
        map = new HashMap<>();
        log.info("加载微服务：" + serviceId + " 的权限资源");

        List<RoleAccessDTO> roleAccessDTOS = roleClient.roleAccess(serviceId);

        //将,号隔开的路径分离
        Map<String, List<String>> reduce = roleAccessDTOS.stream()
                .filter(roleAccessDTO -> StringUtils.isNotBlank(roleAccessDTO.getAccessAdress())).reduce(new
                                ArrayList<RoleAccessDTO>(),
                        (a, b) -> {
                            if (StringUtils.contains(b.getAccessAdress(), ",")) {
                                a.addAll(Stream.of(b.getAccessAdress().split(","))
                                        .map(address -> new RoleAccessDTO(b.getRoleCode(), address))
                                        .collect(Collectors.toList()));
                            } else {
                                a.add(b);
                            }
                            return a;
                        }, (a, v) -> {
                            a.addAll(v);
                            return a;
                        }).stream()
                .collect(Collectors.groupingBy(RoleAccessDTO::getAccessAdress,
                        Collectors.mapping(RoleAccessDTO::getRoleCode, Collectors.toList())));


        reduce.forEach((key, value) -> map.put(key, value.stream().map(SecurityConfig::new)
                .collect(Collectors.toList())));

        //合并
        if (Objects.nonNull(initMap) && !initMap.isEmpty()) {

            initMap.forEach((key, value) ->
                    map.compute(key,
                            (newVal, oldVal) -> {
                                if (oldVal == null) {
                                    return value;
                                }
                                oldVal.addAll(value);
                                return oldVal.stream().distinct().collect(Collectors.toList());
                            })
            );
        }

        isloadSuccess = true;
    }

    /**
     * 根据路径获取访问权限的集合接口
     *
     * @return
     * @paramobject
     * @throwsIllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if (!isloadSuccess) {
            loadResourceDefine();
        }
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();

        if (isIgnored(request)) {
            return null;
        }
        AntPathRequestMatcher matcher;
        String resUrl;

        List<ConfigAttribute> objects = new ArrayList<>();

        for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
            resUrl = iter.next();
            if (StringUtils.isEmpty(resUrl)) {
                log.warn("权限配置有异常数据 出现空的表达式");
                continue;
            }
            matcher = new AntPathRequestMatcher(resUrl);
            if (matcher.matches(request)) {
                objects.addAll(map.get(resUrl));
            }
        }

        return objects.stream().distinct().collect(Collectors.toList());
    }

    /**
     * @return
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}