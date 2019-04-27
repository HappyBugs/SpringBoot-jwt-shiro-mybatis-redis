package com.likuncheng.server;

import org.apache.ibatis.annotations.Param;

import com.likuncheng.entity.User;

public interface UserServer {
	
	/**
	  * 用户登录
	 * @param phone
	 * @param passWord
	 */
	public User login(@Param("phone")String phone , @Param("passWord")String passWord);
	
	/**
	  * 用户注册
	 * @param user
	 * @return 返回受影响的行数
	 */
	public Integer register(@Param("user")User user);
	
	/**
	  * 根据用户id得到用户信息
	 * @param id
	 * @return
	 */
	public User getUserById(@Param("id")String id);
	
	/**
	   * 根据用户电话号码进行查询用户信息
	 * @param phone
	 * @return
	 */
	public User getUserByPhone(@Param("phone")String phone);
	
	/**
	 * 
	 * @param user 需要修改的用户信息
	 * @return 返回受影响的行数
	 */
	public Integer updUser(@Param("user") User user);

	/**
	 * 根据用户id和密码确认用户userName
	 * @param passWord 密码
	 * @param id 
	 * @return userName 用户名称
	 */
	public String getUserNameByIdAndPassWord(@Param("passWord") String passWord,
			@Param("id") String id);


}
