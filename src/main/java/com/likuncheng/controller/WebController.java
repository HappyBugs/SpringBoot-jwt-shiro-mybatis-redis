package com.likuncheng.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.likuncheng.entity.User;
import com.likuncheng.jwt.JWTTokenUtil;
import com.likuncheng.serverImpl.UserServerImpl;
import com.likuncheng.utils.BaseController;
import com.likuncheng.utils.BaseRedisService;
import com.likuncheng.utils.ResponseBase;

import io.jsonwebtoken.Claims;
import io.netty.util.internal.StringUtil;

@SuppressWarnings(value= {"all", "unused"})
@RestController
public class WebController {

	// 注入restful返回格式控制层
	@Autowired
	private BaseController baseController;

	// 注入usermapper
	@Autowired
	private UserServerImpl userServerImpl;

	// 注入redis封装类
	@Autowired
	private BaseRedisService baseRedisService;
	
//	private ShiroLogoutFilter shiroLogoutFilter;

	// 保存在redis中的数据的过期时间 因为要和shiro中的过期时间一致
	private long outTime = 900000;
	
	/**
	 * 因为我们这里没有前端项目 所以无法跳转到登录页面 我这里写了一个地址 模拟一下跳转到页面
	 * 因为我们需要测试一下 退出登录 因为在退出的登录的时候需要指定一个地址
	 */
	@GetMapping(value="/login")
	public String login() {
		return "这是用户登录页面";
	}

	/**
	 * 登陆验证 也就是用户点击登录的时候
	 * @param phone
	 * @param passWord
	 * @param request
	 * @return
	 */
	@PostMapping("/loginVerification")
	public ResponseBase loginVerification(@RequestParam("phone") String phone, @RequestParam("passWord") String passWord,
			HttpServletRequest request) {
		//验证请求头中是否有Authorization字段存在
		String header = request.getHeader("Authorization");
		if(! StringUtil.isNullOrEmpty(header)) {
			//如果不是 空或者null 那就是在header中还存在token信息 就直接返回信息就好 表示已经登陆
			return baseController.setResultSuccess(header);
		}
		// 1.查询redis缓存 使用phone作为key保证唯一性
		String token = baseRedisService.getString(phone);
		// 如果找到 token
		if (! StringUtil.isNullOrEmpty(token)) {
			// 直接返回数据给客户端
			return baseController.setResultSuccess(token);
		}
		// 2.没有在redis中找打token 查询数据库
		User user = userServerImpl.getUserByPhone(phone);
		// 如果没找到user
		if (user == null) {
			return baseController.setResultError("请输入正确的账号");
		}
		if (! user.getPassWord().equals(passWord)) {
			return baseController.setResultError("密码错误");
		}
		JSONObject jsonObject = new JSONObject();
		// 保存用户信息
		jsonObject.put("id", user.getId());
		jsonObject.put("userName", user.getUserName());
		// 创建token
		String createJWT = JWTTokenUtil.createJWT(jsonObject.toString());
		// 保存到redis中  使用电话号码 保证唯一性
		baseRedisService.setString(user.getPhone(), createJWT, outTime);
		return baseController.setResultSuccess(createJWT);
	}
	

	//因为我们已经使用shiro进行拦截 写了这个 我测试也没用
//	/**
//         * 用户退出登录
//	 * @return 
//	 * @throws IOException 
//	 */
//	@GetMapping(value="logout")
//	public void logOut(HttpServletRequest request , HttpServletResponse response) throws IOException {
//		String header = request.getHeader("Authorization");
//		//如果http请求头中没有用户token信息 就表示未登录
//		if(StringUtil.isNullOrEmpty(header)) {
//			return baseController.setResultError("用户未登录");
//		}
//		Claims claims = JWTTokenUtil.parseJWT(header);
//		String subject = claims.getSubject();
//		JSONObject jsonObject = JSON.parseObject(subject);
//		//得到用户id用于获取用户的信息
//		Integer userId =  (Integer) jsonObject.get("id");
//		User userById = userServerImpl.getUserById(userId.toString());
//		String phone = userById.getPhone();
//		//删除在redis中的信息
//		baseRedisService.delKey(phone);
//		Subject userSubject = SecurityUtils.getSubject();
//		//如果用户已经登录
//		if(userSubject.isAuthenticated()) {
//			//shiro退出登录
//			userSubject.logout();
//		}
//		try {
//			shiroLogoutFilter.preHandle(request, response);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		//重定向到
//		response.sendRedirect("127.0.0.1:8091/login");
//		return baseController.setResultSuccess("用户成功退出登录");
//	}

	@GetMapping("/article")
	public ResponseBase article() {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			return baseController.setResultSuccess("您已登录");
		} else {
			return baseController.setResultSuccess("您是游客");
		}
	}

	@GetMapping("/require_auth")
	@RequiresAuthentication
	public ResponseBase requireAuth() {
		return baseController.setResultSuccess("您已经通过身份验证");
	}

	@GetMapping("/require_role")
	@RequiresRoles("admin") // 需要用户角色为admin
	public ResponseBase requireRole() {
		return baseController.setResultError("您正在访问需要角色为admin 您允许访问");
	}

	@GetMapping("/require_permission")
	@RequiresPermissions(logical = Logical.AND, value = { "view", "edit" }) // 需要同时拥有 view 和edit
	public ResponseBase requirePermission() {
		return baseController.setResultSuccess("您正在访问权限需要view和edit 您允许访问");
	}

	// 错误请求地址
	@RequestMapping(value = "/401")
	public ResponseBase Exception(Throwable e) {
		return baseController.setResultError(e.getMessage());
	}

}
