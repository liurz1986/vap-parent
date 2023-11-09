//package com.vrv.vap.server.config;
//
//import feign.Client;
//import feign.Request;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.cloud.netflix.feign.ribbon.CachingSpringLoadBalancerFactory;
//import org.springframework.cloud.netflix.feign.ribbon.LoadBalancerFeignClient;
//import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.net.ssl.*;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//
///**
// * fegin相关配置
// *
// * @author xw
// * @date 2018年5月3日
// */
//@Configuration
//public class FeignConfig {
//    // /**
//    // * 配置请求重试
//    // *
//    // */
//    // @Bean
//    // public Retryer feignRetryer() {
//    // return new Retryer.Default(200, 2000, 3);
//    // }
//    //
//
//    /**
//     * 设置请求超时时间 默认 public Options() { this(10 * 1000, 60 * 1000); }
//     */
//    @Bean
//    Request.Options feignOptions() {
//        return new Request.Options(120 * 1000, 120 * 1000);
//    }
//
//    /**
//     * 打印请求日志
//     *
//     * @return
//     */
//    @Bean
//    public feign.Logger.Level multipartLoggerLevel() {
//        return feign.Logger.Level.FULL;
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory, SpringClientFactory clientFactory) throws NoSuchAlgorithmException, KeyManagementException {
//        // "SSL" 信任证书(绕过验证)
//        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
//        X509TrustManager tm = new X509TrustManager() {
//            @Override
//            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
//
//            }
//
//            @Override
//            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
//
//            }
//
//            @Override
//            public X509Certificate[] getAcceptedIssuers() {
//                return new X509Certificate[0];
//            }
//        };
//        ctx.init(null, new TrustManager[]{tm}, null);
//        return new LoadBalancerFeignClient(new Client.Default(ctx.getSocketFactory(),
//                new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession sslSession) {
//                        return true;
//                    }
//                }),
//                cachingFactory, clientFactory);
//    }
//}
