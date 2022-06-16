package org.joe.cloud.config;

import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tianze Zhu
 * @since 2022-05-03
 */
@Configuration
public class ShiroConfig {
    /**
     * 配置Filter的路径
     * <a href="https://shiro.apache.org/web.html#default_filters">https://shiro.apache.org/web.html#default_filters</a>
     *
     * @param securityManager 系统安全Manager
     * @return 设置了Filter的ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        Map<String, String> filterRuleMap = new HashMap<>();
        // 不必验证
        filterRuleMap.put("/login", "anon");
        filterRuleMap.put("/install", "anon");
        filterRuleMap.put("/who", "anon");

        filterRuleMap.put("/doc.html", "anon");
        filterRuleMap.put("/webjars/**", "anon");
        filterRuleMap.put("/favicon.ico", "anon");
        filterRuleMap.put("/v2/**", "anon");
        filterRuleMap.put("/swagger**/**", "anon");
        filterRuleMap.put("/transfer/share/download", "anon");


        // JWT 验证过滤器
        filterRuleMap.put("/**", "authcBearer");

        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public Authorizer authorizer() {
        return new ModularRealmAuthorizer();
    }
}
