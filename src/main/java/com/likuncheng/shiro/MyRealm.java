package com.likuncheng.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.likuncheng.entity.User;
import com.likuncheng.jwt.JWTTokenUtil;
import com.likuncheng.serverImpl.UserServerImpl;

import io.jsonwebtoken.Claims;
import io.netty.util.internal.StringUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class MyRealm extends AuthorizingRealm {



	@Autowired
	private UserServerImpl userServerImpl;

    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof NewAuthenticationToken;
    }
    

    /**
         * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     *   principals 就是相当于token信息（也就是前面保存的 JWTToken token = new JWTToken(authorization);
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    	
    	
    	/*
    	 * PrincipalCollection是下面 SimpleAuthenticationInfo 创建了一个子类SimplePrincipalCollection 在这里面保存了
    	  * 用户的主体 在这里我们都是 保存的token 所以可以直接 转换成Token
    	 * 
    	 */
    	Claims claims = JWTTokenUtil.parseJWT(principals.toString());
    	String userSubject = claims.getSubject();
    	JSONObject jsonObject = JSON.parseObject(userSubject);
    	Integer userId = (Integer) jsonObject.get("id");
    	User userById = userServerImpl.getUserById(userId.toString());
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        //设置用户角色
        simpleAuthorizationInfo.addRole(userById.getRole());
        //保存用户所有的权限 因为我在数据库中设计的就是 所有权限用逗号隔开 如 xxx,xxx,xxx 所以需要分割保存
        Set<String> permission = new HashSet<>(Arrays.asList(userById.getPermission().split(",")));
        //保存用户权限
        simpleAuthorizationInfo.addStringPermissions(permission);
        return simpleAuthorizationInfo;
    }

    /** 
         *      默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
         *     可以通过这个方法进行用户的身份验证  并且保存到SimplePrincipalCollection对象中 
         *   实例化 SimplePrincipalCollection这个对象是在 SimpleAuthenticationInfo 内部进行的
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        //这里也是我们之前JWTToken 中保存的 返回我们生成的token
    	String token = (String) auth.getPrincipal();
    	Claims claims = JWTTokenUtil.parseJWT(token);
    	String userSubject = claims.getSubject();
    	JSONObject jsonObject = JSON.parseObject(userSubject);
    	Integer userId = (Integer) jsonObject.get("id");
    	User userById = userServerImpl.getUserById(userId.toString());
    	if(userById == null ) {
    		throw new AuthenticationException("用户为空");
    	}
    	if(StringUtil.isNullOrEmpty(token)) {
    		throw new AuthenticationException("token不存在，或者已经过期");
    	}
    	/*
    	  * 返回一个存放用户主题与凭证的 对象 也就是在这里创建了一个 PrincipalCollection 对象 
    	  * 并且保存了 用户的信息 我们这里是保存的token
    	 */
        return new SimpleAuthenticationInfo(token, token, "my_realm");
    }
    
//    /**
//     * 重写方法,清除当前用户的的 授权缓存
//     * @param principals
//     */
//    @Override
//    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
//        super.clearCachedAuthorizationInfo(principals);
//    }
//
//    /**
//     * 重写方法，清除当前用户的 认证缓存
//     * @param principals
//     */
//    @Override
//    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
//        super.clearCachedAuthenticationInfo(principals);
//    }
//
//    @Override
//    public void clearCache(PrincipalCollection principals) {
//        super.clearCache(principals);
//    }
//
//    /**
//     * 自定义方法：清除所有 授权缓存
//     */
//    public void clearAllCachedAuthorizationInfo() {
//        getAuthorizationCache().clear();
//    }
//
//    /**
//     * 自定义方法：清除所有 认证缓存
//     */
//    public void clearAllCachedAuthenticationInfo() {
//        getAuthenticationCache().clear();
//    }
//
//    /**
//     * 自定义方法：清除所有的  认证缓存  和 授权缓存
//     */
//    public void clearAllCache() {
//        clearAllCachedAuthenticationInfo();
//        clearAllCachedAuthorizationInfo();
//    }

}
