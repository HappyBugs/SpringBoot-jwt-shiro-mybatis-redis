package com.likuncheng.shiro;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTFilter extends BasicHttpAuthenticationFilter {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Authorization字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        //判断请求头中是否存在 Authorization
        // 可以在cookie 或者在前端将我们使用jwt生成的token 保存在http请求中 
        //这样之后每次在客户端发起请求的时候就会带上我们生成的token
        String authorization = req.getHeader("Authorization");
        return authorization != null;
    }

    /**
     * 登录
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("Authorization");
        // 将token保存在 shiro 中的 AuthenticationToken 后面会用到
        NewAuthenticationToken newAuthenticationToken = new NewAuthenticationToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(newAuthenticationToken);
//        SecurityUtils.getSubject().login(newAuthenticationToken);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 这里我们详细说明下为什么最终返回的都是true，即允许访问
     * 例如我们提供一个地址 GET /article
     * 登入用户和游客看到的内容是不同的
     * 如果在这里返回了false，请求会被直接拦截，用户看不到任何东西
     * 所以我们在这里返回true，Controller中可以通过 subject.isAuthenticated() 来判断用户是否登入
     * 如果有些资源只有登入用户才能访问，我们只需要在方法上面加上 @RequiresAuthentication 注解即可
     * 但是这样做有一个缺点，就是不能够对GET,POST等请求进行分别过滤鉴权(因为我们重写了官方的方法)，但实际上对应用影响不大
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        //如果是登录过的话 并且有效期花没有过期
    	if (isLoginAttempt(request, response)) {
            try {
            	//登录
                executeLogin(request, response);
            } catch (Exception e) {
                response401(request, response);
            }
        }
        return true;
    }

//    /**
//     * 对跨域提供支持
//     */
//    @Override
//    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
//        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
//        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
//        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
//        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
//            httpServletResponse.setStatus(HttpStatus.OK.value());
//            return false;
//        }
//        return super.preHandle(request, response);
//    }

    /**
     * 将非法请求跳转到 /401
     */
    private void response401(ServletRequest req, ServletResponse resp) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
           /*
                      *   地址的转向 跟request.getRequestDispatcher(跳转页面的Url).foward(request,response);
                      *   使用sendRedirect 就相当于是浏览器重新发起的请求 所以地址会变 而且 
                      *   以前的session获取request都是可以用的 但是使用request重定向是不可以的
                      *   但是速度相对于来说没有request快 因为不会隐藏地址 所以安全性 较request较低
            */
            httpServletResponse.sendRedirect("127.0.0.1:8091/401");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
