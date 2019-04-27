package com.likuncheng.jwt;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 创建登录token令牌
 * 
 * @author Administrator
 *
 */
public class JWTTokenUtil {

//	  "iss":"Issuer —— 用于说明该JWT是由谁签发的", 
//    "sub":"Subject —— 用于说明该JWT面向的对象", 
//    "aud":"Audience —— 用于说明该JWT发送给的用户", 
//    "exp":"Expiration Time —— 数字类型，说明该JWT过期的时间", 
//    "nbf":"Not Before —— 数字类型，说明在该时间之前JWT不能被接受与处理", 
//    "iat":"Issued At —— 数字类型，说明该JWT何时被签发", 
//    "jti":"JWT ID —— 说明标明JWT的唯一ID", 
//    "user-definde1":"自定义属性举例", 
//    "user-definde2":"自定义属性举例"
	
	
	//过期时间就是 15分钟之后
	private static long OUT_TIME = 900000;
	
	
	//读取配置文件 秘匙 （这是用来后面接收到token的时候用于解密用的秘匙
	private static final String secretKey = "JwtToken";

	/**
	 * 
	 * @param subject 需要进行传递的值
	 * @return 产生的token令牌
	 */
	public static String createJWT(String subject) {

		// 我们将用于签名令牌的JWT签名算法
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		//获取当前系统时间戳
		long nowMillis = System.currentTimeMillis();

		// 就是将我们自己的密匙进行转成base64的byte类型的字符串
		System.err.println(secretKey);
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
		//创建我们的密匙 密匙为我们自己指定的值 加密方式为我们指定的 HS256 算法
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		//创建过期时间
		Date exp = new Date((nowMillis+OUT_TIME));
		
		// 让我们设定JWT的要求 这里只封装了  sub 和 exp (详情请看最顶端有详细说明)
		JwtBuilder builder = Jwts.builder()
				.setSubject(subject)
				.setExpiration(exp)
				.signWith(signatureAlgorithm, signingKey);

		// 如果 bulider已经创建 那么久生成一个 jwt的字符串 也就是 token
		return builder.compact();
	}

	
	/**
	 * 解析jwttoken	
	 * @param jwtToken jwt生成的token字符串 
	 */
	public static Claims parseJWT(String jwtToken) {
		//如果这里的 secretKey(也就是前面自己创建的密匙 那么就会报错)
	    Claims claims = Jwts.parser()         
	       .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
	       .parseClaimsJws(jwtToken).getBody();
	    //这样就可以得到之前我们保存的 sub之中的值 和设置的过期时间
//	    System.out.println("Subject: " + claims.getSubject());
//	    System.out.println("Expiration: " + claims.getExpiration());
	    return claims;
	    
	}
	

}
