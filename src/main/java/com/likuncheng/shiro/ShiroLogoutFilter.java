package com.likuncheng.shiro;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;

public class ShiroLogoutFilter extends LogoutFilter {


	@Override
	public boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		Subject subject = getSubject(request, response);
		String redirectUrl = getRedirectUrl(request, response, subject);
		try {
			// 退出
//			System.err.println("进来了");
			getSubject(request, response).logout();
		} catch (SessionException ise) {
		}
		//返回false表示不执行后续的过滤器，直接返回跳转到登录页面
		issueRedirect(request, response, redirectUrl);
		return false;
	}

}
