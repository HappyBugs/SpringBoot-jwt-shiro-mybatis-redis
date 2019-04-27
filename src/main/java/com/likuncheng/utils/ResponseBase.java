package com.likuncheng.utils;

import lombok.Data;

/**
 * 相应封装
 * 
 * @author Administrator
 *
 */
@Data
public class ResponseBase {

	// 响应code
	private Integer code;
	// 消息内容
	private String msg;
	// 返回data
	private Object data;
	
	public ResponseBase() {}
	
	public ResponseBase(Integer code , String msg , Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

}
