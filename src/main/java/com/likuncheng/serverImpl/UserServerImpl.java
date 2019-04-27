package com.likuncheng.serverImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likuncheng.entity.User;
import com.likuncheng.mapper.UserMapper;
import com.likuncheng.server.UserServer;

@Service
public class UserServerImpl implements UserServer {

	@Autowired
	private UserMapper userMapper;

	@Override
	public User login(String phone, String passWord) {
		User user = null;
		try {
			user = userMapper.login(phone, passWord);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return user;
	}

	@Override
	public Integer register(User user) {
		Integer resule = 0;
		try {
			resule = userMapper.register(user);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return resule;
	}

	@Override
	public User getUserById(String id) {
		User userById = null;
		try {
			userById = userMapper.getUserById(id);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return userById;
	}

	@Override
	public User getUserByPhone(String phone) {
		User user = null;
		try {
			user = userMapper.getUserByPhone(phone);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return user;
	}

	@Override
	public Integer updUser(User user) {
		Integer result = 0;
		try {
			result = userMapper.updUser(user);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	@Override
	public String getUserNameByIdAndPassWord(String passWord, String id) {
		String userName = "";
		try {
			userName = userMapper.getUserNameByIdAndPassWord(passWord, id);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return userName;
	}

}
