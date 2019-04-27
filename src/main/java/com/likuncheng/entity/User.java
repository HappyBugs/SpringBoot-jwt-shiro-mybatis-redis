package com.likuncheng.entity;


import lombok.Data;

@Data
public class User {
	
	private Integer id;
	private String userName;
	private String passWord;
	private String phone;
	//用户角色
	private String role;
	//用户权限 可能有很多的权限
	private String permission;
	
}
