package com.vrv.vap.alarmdeal.frameworks.feign;


import feign.Client;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


//@Configuration
public class FeignHttpsConfig {

	 	@Bean
	    public Client feignClient() {
	        Client trustSSLSockets = new Client.Default(getSSLSocketFactory(), new NoopHostnameVerifier());
	        return trustSSLSockets;
	    }
	 	
	 	// 方便查看日志，可不用编写
//	    @Bean
//	    Logger.Level feignLoggerLevel() {
//	        return Logger.Level.FULL;
//
//	    }

	 
	    public static  SSLSocketFactory  getSSLSocketFactory(){
	        try {
				SSLContext sc = SSLContext.getInstance("TLS");
	            sc.init(null, trustAllCerts, new SecureRandom());
				return sc.getSocketFactory();
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        } catch (KeyManagementException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    static TrustManager[] trustAllCerts = new TrustManager[]{ new X509TrustManager() {
	        @Override
	        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            // TODO Auto-generated method stub
	        }

	        @Override
	        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            // TODO Auto-generated method stub
	        }

	        @Override
	        public X509Certificate[] getAcceptedIssuers() {
	            // TODO Auto-generated method stub
				X509Certificate[] x509Certificates = new X509Certificate[]{};
				return x509Certificates;
	        }
	    }};


}
