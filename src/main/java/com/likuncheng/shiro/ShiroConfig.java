package com.likuncheng.shiro;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


import javax.servlet.Filter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

	
	/** 
	  * 权限管理 核心安全事务管理器
	 * @param realm
	 * @return
	 */
    @Bean("securityManager")
    public DefaultWebSecurityManager getManager(MyRealm realm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        // 使用自己的realm
        manager.setRealm(realm);
        /*
         * 关闭shiro自带的session，详情见文档
         * http://shiro.apache.org/session-management.html#SessionManagement-StatelessApplications%28Sessionless%29
         */
//        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
//        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
//        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
//        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
//        manager.setSubjectDAO(subjectDAO);
        //这是有关于使用shiro-redis的
//        SecurityUtils.setSecurityManager(manager);
//        manager.setCacheManager(cacheManager());
//        manager.setSessionManager(sessionManager());
        return manager;
    }

    //Filter工厂，设置对应的过滤条件和跳转条件
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean factory(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
//        //登录页面的地址 不指定的话就会直接默认寻找
//        factoryBean.setLoginUrl(loginUrl);
//        //登录成功后的地址
//        factoryBean.setSuccessUrl(successUrl);
//        //未授权的请求地址
//        factoryBean.setUnauthorizedUrl(unauthorizedUrl);
        Map<String, Filter> filterMap = new HashMap<>();
        //过滤器地址 取名为 jwt 
        filterMap.put("jwt", new JWTFilter());
        //退出登录地址
        filterMap.put("logout", shiroLogoutFilter());
        //也就是自定的的一些过滤条件
        factoryBean.setFilters(filterMap);
        //必须设置 SecurityManager,Shiro的核心安全接口
        factoryBean.setSecurityManager(securityManager);
        //错误地址
        factoryBean.setUnauthorizedUrl("/401");

        /*
                * 自定义url规则
         * http://shiro.apache.org/web.html#urls-
         */
        Map<String, String> filterRuleMap = new HashMap<>();
        // 所有请求通过我们自己的过滤器
        filterRuleMap.put("/**", "jwt");
        // 访问401和404页面不通过我们的Filter
        //配置不登录可以访问的资源，anon 表示资源都可以匿名访问（也就是说 都可以进行访问 
        filterRuleMap.put("/401", "anon");
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return factoryBean;
    }

    /**
     * 下面的代码是添加注解支持 aop（用于解决注解不生效的原因
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
        // https://zhuanlan.zhihu.com/p/29161098
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * 管理shirobean的生命周期
     * @return
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 加入注解
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
    
    //
    /**
     * 配置用户的退出登录后需要转向的地址
     * @return
     */
    public ShiroLogoutFilter shiroLogoutFilter () {
    	ShiroLogoutFilter shiroLogoutFilter = new ShiroLogoutFilter();
    	shiroLogoutFilter.setRedirectUrl("127.0.0.1:8091/login");
    	return shiroLogoutFilter;
    }
    
    
//    /**
//     * 配置会话管理器，设定会话超时及保存
//     * @return
//     */
//    @Bean("sessionManager")
//    public SessionManager sessionManager() {
//        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
////        Collection<SessionListener> listeners = new ArrayList<SessionListener>();
//        //配置监听
//        sessionManager.setSessionDAO(sessionDAO());
//        sessionManager.setCacheManager(cacheManager());
//        return sessionManager;
//
//    }
//    
//
//    
//    /**
//     * shiro缓存管理器;
//     * 需要添加到securityManager中
//     * @return
//     */
//    public RedisCacheManager cacheManager(){
//        RedisCacheManager redisCacheManager = new RedisCacheManager();
//        redisCacheManager.setRedisManager(redisManager());
//        redisCacheManager.setPrincipalIdFieldName("phone");
//        //用户权限信息缓存时间
//        redisCacheManager.setExpire(20000);
//        return redisCacheManager;
//    }
//    
//    /**
//     * SessionDAO的作用是为Session提供CRUD并进行持久化的一个shiro组件
//     * MemorySessionDAO 直接在内存中进行会话维护
//     * EnterpriseCacheSessionDAO  提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
//     * @return
//     */
//    @Bean
//    public SessionDAO sessionDAO() {
//        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
//        redisSessionDAO.setRedisManager(redisManager());
//        //session在redis中的保存时间,最好大于session会话超时时间
//        //如果这里采用默认的 时间也就是不设置 那么 session.setTimeout 一定要大于1000
//        //是1000的几倍 就是在redis中保存多少秒
//        //如果设置的值 不是 -1 并且 setExpire的值乘于 1000 要小于 session.getTimeout 的话 就不会保存成功
//        //除了上面两种情况下 就是 输入多少 就在数据库存在多长时间 单位为int
//        redisSessionDAO.setExpire(12000);
//        return redisSessionDAO;
//    }
//    
//    
//    /**
//     * redis的连接信息
//     * @return
//     */
//    public RedisManager redisManager(){
//        RedisManager redisManager = new RedisManager();
//        //这里可以不用设置 因为 默认的连接地址就是 127.0.0.1:6379
//        redisManager.setHost("127.0.0.1");
//        redisManager.setPort(6379);
//        //这里其实也可以不用指定 默认超时时间也是 2000
//        redisManager.setTimeout(2000);
//        //这里我们设置shiro-redis的缓存是保存在第一个库的 默认也是第0个库
//        redisManager.setDatabase(1);
//        redisManager.setPassword("123456");
//        return redisManager;
//    }
//    
    /**
     * 限制同一账号登录同时登录人数控制
     *
     * @return
     */
//    @Bean
//    public KickoutSessionControlFilter kickoutSessionControlFilter() {
//        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
//        kickoutSessionControlFilter.setCacheManager(cacheManager());
//        kickoutSessionControlFilter.setSessionManager(sessionManager());
//        //是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；
//        kickoutSessionControlFilter.setKickoutAfter(false);
//        //同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
//        kickoutSessionControlFilter.setMaxSession(1);
//        //被踢出后重定向到的地址；
//        kickoutSessionControlFilter.setKickoutUrl("127.0.0.1:8091/login");
//        return kickoutSessionControlFilter;
//    }

}
