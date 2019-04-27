package com.likuncheng.shiro;

import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.likuncheng.entity.User;
import com.likuncheng.jwt.JWTTokenUtil;
import com.likuncheng.serverImpl.UserServerImpl;

import io.jsonwebtoken.Claims;


//去除所有警告
@SuppressWarnings(value= {"all"})
public class NewAuthenticationToken implements AuthenticationToken {

	@Autowired
	private UserServerImpl impl;
	
	// 密钥
    private String token;

    public NewAuthenticationToken(String token) {
        this.token = token;
    }

    @Override
    public Object getCredentials() {
    	//凭证保存token信息
        return token;
    }

	@Override
	public Object getPrincipal() {
		//主体就保存用户信息
		return token;
	}
}
