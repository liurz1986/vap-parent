package com.vrv.vap.xc.client;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class  EsClient{

    @Value("${elk.cluster.name}")
    private String CLUSTERNAME;
    @Value("${elk.ip}")
    private String IPS;
    @Value("${elk.port}")
    private int PORT;
    @Value("${elk.xpack.security.user}")
    private String USER;
    @Value("${elk.xpack.security.password}")
    private String PASSWORD;

    private static EsClient esClient;

    private static final Logger log = LoggerFactory.getLogger(EsClient.class);


    private static final int CONNECT_TIME_OUT = 300000;
    private static final int SOCKET_TIME_OUT = 600000;
    private static final int CONNECTION_REQUEST_TIME_OUT = 300000;
    private static final int MAX_RETRY_TIME_OUT = 300000;

    private static final int MAX_CONNECT_NUM = 100;
    private static final int MAX_CONNECT_PER_ROUTE = 100;

    private EsClient() {
    }

    public static void closeClient() {
        log.info("-------------------closeClient-----------------");
        if (null != EsClient.getInstance()) {
            log.info("----------------ClientInstance:" + EsClient.getInstance());
            //ESClient.getInstance().close();
            log.info("----------------ClientInstance close:" + EsClient.getInstance());
        }
    }

    @PostConstruct
    public void init() {
        esClient = this;
        esClient.CLUSTERNAME = this.CLUSTERNAME;
        esClient.IPS = this.IPS;
        esClient.PORT = this.PORT;
        esClient.USER = this.USER;
    }

    private static RestHighLevelClient client = null;

    private static void initClient() {
        //log.info("------------------ClusterName：" + esClient.CLUSTERNAME);
        //log.info("------------------User:" + esClient.USER);
        if (null != client) {
            return;
        }

        RestClientBuilder builder = RestClient.builder(Arrays.asList(esClient.IPS.split(",")).stream().map(m -> {
            //log.info("elasticsearch node : " + m + ":" + esClient.PORT);
            return new HttpHost(m, esClient.PORT,null);
        }).collect(Collectors.toList()).toArray(new HttpHost[0]));

        // 服务认证
        if (StringUtils.isNotEmpty(esClient.USER) && StringUtils.isNotEmpty(esClient.PASSWORD)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esClient.USER, esClient.PASSWORD));
            builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    httpClientBuilder.setMaxConnTotal(MAX_CONNECT_NUM);
                    httpClientBuilder.setMaxConnPerRoute(MAX_CONNECT_PER_ROUTE);
                    httpClientBuilder.disableAuthCaching();
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        }

        builder
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        requestConfigBuilder.setConnectTimeout(CONNECT_TIME_OUT);
                        requestConfigBuilder.setSocketTimeout(SOCKET_TIME_OUT);
                        requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);
                        return requestConfigBuilder;
                    }
                });


        client = new RestHighLevelClient(builder);
    }


    public static RestHighLevelClient getInstance() {
        if (null == client) {
            initClient();
        }
        return client;
    }

    public String getIPS() {
        return IPS;
    }

    public void setIPS(String IPS) {
        this.IPS = IPS;
    }
}
