package com.vrv.vap.admin.common.util;

import com.alibaba.fastjson.util.IOUtils;
import okhttp3.internal.platform.Platform;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.ejlchina.okhttps.OkHttps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.admin.vo.supervise.BaseResult;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class SuperviseHttpsUtils {

	private static Logger logger = LoggerFactory.getLogger(SuperviseHttpsUtils.class);
	 
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	
	public static String  getToken(String serverUrl,String clientId,String clientSecret)
	{
		//假设是客户端模式
		String token = OAuth2Utils.clientCredentialsLogin(serverUrl, clientId, clientSecret);
		return token;
	}
	
	
	public static String Post(String token,String pathurl,String postData)
	{

		if(!StringUtils.isEmpty(token))
		{
			 RequestBody body = RequestBody.create( MediaType.parse("application/json; charset=utf-8"),encryptDatas(postData));
			 Response response =null;
			try {
				
/*				  String result =
				  OkHttps.sync(pathurl).addHeader("Authorization","Bearer "+token)
				  .addBodyPara("data", encryptDatas(postData))//传输的数据加密 .post() .getBody()
				  .toString();
				  return decryptDatas(result);*/
				OkHttpClient client = new OkHttpClient();
				Request request = new Request.Builder().addHeader("Authorization","Bearer "+token).url(pathurl).post(body).build();
				response = client.newCall(request).execute();

				// 数据解密
				return decryptDatas(response.body().toString());
			}catch (Exception e) {
				BaseResult result=new BaseResult();
				result.setCode("-1");
				result.setCode("接口调用异常："+e.getMessage());
				logger.error("接口调用异常：", e);
				
				return gson.toJson(result);
			}finally {
				if(response!=null)
				{
					response.close();
				}
			}
		}
		
		return null;
	}

	public static String post(String url, String param, String token) {
		String result = "";
		InputStream caInput =null;
		Response response =null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			caInput = new BufferedInputStream(new FileInputStream("F:\\opt\\test.cer"));
			Certificate ca = cf.generateCertificate(caInput);
			String keyStoreType = KeyStore.getDefaultType();
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);

			X509TrustManager trustManager = new X509TrustManager() {
				private X509Certificate[] certificates;
				@Override
				public void checkClientTrusted(X509Certificate certificates[],
											   String authType) {
					if (this.certificates == null) {
						this.certificates = certificates;
						System.out.println("init at checkClientTrusted");
					}
				}

				@Override
				public void checkServerTrusted(X509Certificate[] ax509certificate,
											   String s) {
					if (this.certificates == null) {
						this.certificates = ax509certificate;
						System.out.println("init at checkServerTrusted");
					}
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}
			};

			RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), param);
			//RequestBody body = RequestBody.create(null, "");
			Request request = new Request.Builder().url(url).post(body)
					//.addHeader("clientId", "204")
					//.addHeader("Client-Type", "android")
					//.addHeader("Client-Version", "2.2.6")
					//.addHeader("plain-text-transfer", "true")
					.addHeader("Content-Type", "application/json")
					.addHeader("Authorization", "Bearer " + token).build();

			OkHttpClient client = new OkHttpClient.Builder()
					.sslSocketFactory(context.getSocketFactory(), trustManager)
					.connectTimeout(15, TimeUnit.SECONDS)
					.readTimeout(30, TimeUnit.SECONDS)
					.hostnameVerifier(new TrustAnyHostnameVerifier())
					.build();

			response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				result = response.body().string();
			} else {

			}
			caInput.close();
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(caInput!=null){
				IOUtils.close(caInput);
			}
			if(response!=null){
				IOUtils.close(response);
			}
		}
		return result;
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	public static String Post(String pathurl,String postData)
	{

		RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
				encryptDatas(postData));
		Response response = null;
		try {
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(pathurl).post(body).build();
			response = client.newCall(request).execute();

			// 数据解密
			return decryptDatas(response.body().toString());
		} catch (Exception e) {
			// TODO: handle exception
			BaseResult result=new BaseResult();
			result.setCode("-1");
			result.setMsg("接口调用异常："+e.getMessage());
			logger.error("接口调用异常：", e);
			
			return gson.toJson(result);
		} finally {
			if (response != null) {
				response.close();
			}
		} 
	}
	/**
	 * 数据加密
	 * @param data
	 * @return
	 */
	public static String encryptDatas(String data)
	{
		
		return data;
		
	}
	
	/**
	 * 数据解密
	 * @param data
	 * @return
	 */
	
	public static String decryptDatas(String data)
	{
		
		return data;
		
	}
	
}
