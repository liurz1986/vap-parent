package com.vrv.vap.admin.common.util;

import com.ejlchina.okhttps.OkHttps;

public class OAuth2Utils {
	
/*	OAuth2.0中grant_type说明
	1、authorization_code 授权码模式(即先登录获取code,再获取token)
	2、password 密码模式(将用户名,密码传过去,直接获取token)
	3、client_credentials 客户端模式(无用户,用户向客户端注册,然后客户端以自己的名义向’服务端’获取资源)
	4、implicit 简化模式(在redirect_uri 的Hash传递token; Auth客户端运行在浏览器中,如JS,Flash)
	5、refresh_token 刷新access_token*/

 
	
	// 授权码模式 
	// 根据Code码进行登录，获取 Access-Token 和 openid  

	/**
	 * 授权码模式
	 * @param serverUrl
	 * @param clientId
	 * @param clientSecret
	 * @param code
	 * @return
	 */
	public static String authorizationCodeLogin(String serverUrl,String clientId,String clientSecret,String code) {
		// 调用Server端接口，获取 Access-Token 以及其他信息 
		String str = OkHttps.sync(serverUrl + "/oauth2/token")
				.addBodyPara("grant_type", "authorization_code")
				.addBodyPara("code", code)
				.addBodyPara("client_id", clientId)
				.addBodyPara("client_secret", clientSecret)
				.post()
				.getBody()
				.toString();
	 
		return str;
	}
	
 /**
  * 密码模式
  * @param serverUrl
  * @param clientId
  * @param username
  * @param password
  * @return
  */
	public static String passwordLogin(String serverUrl,String clientId,String username, String password) {
		// 模式三：密码式-授权登录
		String str = OkHttps.sync(serverUrl + "/oauth2/token")
				.addBodyPara("grant_type", "password")
				.addBodyPara("client_id", clientId)
				.addBodyPara("username", username)
				.addBodyPara("password", password)
				.post()
				.getBody()
				.toString();
		return str;
	}
	
 
	public static String clientCredentialsLogin(String serverUrl,String clientId,String clientSecret) {
		// 调用Server端接口
		String str = OkHttps.sync(serverUrl + "/oauth2/client_token")
				.addBodyPara("grant_type", "client_credentials")
				.addBodyPara("client_id", clientId)
				.addBodyPara("client_secret", clientSecret)
				.post()
				.getBody()
				.toString();
		 
		return str;
	}
	
 
	/**
	 * 刷新token
	 * @param serverUrl
	 * @param clientId
	 * @param clientSecret
	 * @param refreshToken
	 * @return
	 */
	public static String refreshToken(String serverUrl,String clientId,String clientSecret,String refreshToken) {
		// 调用Server端接口，通过 Refresh-Token 刷新出一个新的 Access-Token 
		String str = OkHttps.sync(serverUrl + "/oauth2/refresh")
				.addBodyPara("grant_type", "refresh_token")
				.addBodyPara("client_id", clientId)
				.addBodyPara("client_secret", clientSecret)
				.addBodyPara("refresh_token", refreshToken)
				.post()
				.getBody()
				.toString();
		 
		return str;
	}
	
}
