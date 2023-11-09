package com.vrv.vap.data.component;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ESClient {

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

    private static ESClient esClient;



    private static final Logger log = LoggerFactory.getLogger(ESClient.class);


    private static final int CONNECT_TIME_OUT = 30000;
    private static final int SOCKET_TIME_OUT = 30000;
    private static final int CONNECTION_REQUEST_TIME_OUT = 30000;

    private static final int MAX_CONNECT_NUM = 100;
    private static final int MAX_CONNECT_PER_ROUTE = 100;

    private ESClient() {
    }

    public static void closeClient() {
        log.info("-------------------closeClient-----------------");
        if (null != ESClient.getInstance()) {
            log.info("----------------ClientInstance:"+ ESClient.getInstance());
            //ESClient.getInstance().close();
            log.info("----------------ClientInstance close:"+ ESClient.getInstance());
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

    private static RestClient client = null;

    private static Map<String,RestClient> clusterClients = new HashMap<>();

    private static RestClient initClient() {
        if (null != client) {
            log.info("------------------client already exist");
            return client;
        }
        client =initClient(esClient.CLUSTERNAME,esClient.IPS,esClient.PORT,esClient.USER,esClient.PASSWORD);
        return  client;
    }

    private static RestClient initClient(String clusterName,String ips,int port,String user,String pwd) {
        log.info("------------------ClusterName："+clusterName);
        log.info("------------------User:"+user);
        String[] esIPS =ips.split(",");
        RestClientBuilder builder = RestClient.builder(Arrays.asList(esIPS).stream().map(m -> {
            log.info("elasticsearch node : " + m + ":" + port);
            return new HttpHost(m,port);
        }).collect(Collectors.toList()).toArray(new HttpHost[0]));

        // 服务认证
        if (StringUtils.isNotEmpty(user) && StringUtils.isNotEmpty(pwd)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pwd));
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

        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                requestConfigBuilder.setConnectTimeout(CONNECT_TIME_OUT);
                requestConfigBuilder.setSocketTimeout(SOCKET_TIME_OUT);
                requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);
                return requestConfigBuilder;
            }
        });


        return  builder.build();
    }



    public String getCLUSTERNAME() {
        return CLUSTERNAME;
    }

    public String getIPS() {
        return IPS;
    }

    public int getPORT() {
        return PORT;
    }

    public String getUSER() {
        return USER;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public static RestClient getInstance() {
        log.info("--------------getInstance start--------------");
        if (null == client) {
            initClient();
        }
        log.info("--------------getInstance end----------------");
        return client;
    }

    public static RestClient getClusterInstance(String clusterName,String ips,int port,String user,String password) {
        log.info("--------------getClusterInstance start--------------");
        if (!clusterClients.containsKey(clusterName)) {
            RestClient client5 = initClient(clusterName,ips,port,user,password);
            clusterClients.put(clusterName,client5);
        }
        log.info("--------------getClusterInstance end----------------");
        return clusterClients.get(clusterName);
    }
}
