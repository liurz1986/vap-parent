package com.vrv.vap.monitor.es.client;

import com.vrv.vap.monitor.es.compatible.VersionFit5To7;
import com.vrv.vap.monitor.tools.EsCurdTools;
import com.vrv.vap.monitor.tools.HWRestTokenBuilder;
import com.vrv.vap.toolkit.tools.HttpTools;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

@Component
public class EsClient {

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

    private static String version;

    private static String krb5Path;

    private static String jaasPath;

    private static String keytab;

    private static EsClient esClient;

    private static final Log log = LogFactory.getLog(EsClient.class);


    private static final int CONNECT_TIME_OUT = 300000;
    private static final int SOCKET_TIME_OUT = 600000;
    private static final int CONNECTION_REQUEST_TIME_OUT = 300000;
    private static final int MAX_RETRY_TIME_OUT = 300000;

    private static final int MAX_CONNECT_NUM = 100;
    private static final int MAX_CONNECT_PER_ROUTE = 100;

    private CloseableHttpAsyncClient httpAsyncClient;
    private static ScheduledExecutorService excutor = new ScheduledThreadPoolExecutor(2,
            new BasicThreadFactory.Builder().namingPattern("es-schedule-pool-%d").daemon(true).build());

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
        //VersionFit5To7.adaptiveVersion();
        initClient();

        //自动检测es版本并适配
        detectVersion();
        //开启监控线程
        excutor.scheduleAtFixedRate(() -> watchClient(), 5, 1 * 120, TimeUnit.SECONDS);
    }

    private void detectVersion() {
        try {
            MainResponse info = client.info(RequestOptions.DEFAULT);
            String esVersion = info.getVersion().toString().substring(0, 1);
            EsClient.version = esVersion;
            VersionFit5To7.adaptiveVersion();
            refreshClientToken();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    private void watchClient() {
        log.debug("定期检查es连接是否失效(I/O reactor status: STOPPED)");
        RestClient restClient = getInstance().getLowLevelClient();
        try {
            if (httpAsyncClient == null) {
                Field clientField = RestClient.class.getDeclaredField("client");
                clientField.setAccessible(true);
                httpAsyncClient = (CloseableHttpAsyncClient) clientField.get(restClient);
            }
            if (!httpAsyncClient.isRunning()) {
                log.warn("es连接已失效,将重新创建");
                httpAsyncClient = null;
                refreshClientToken();
            }
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
    }

    private static RestHighLevelClient client = null;
    private static RestClientBuilder builder = null;

    private static void initClient() {
        log.info("------------------ClusterName：" + esClient.CLUSTERNAME);
        log.info("------------------User:" + esClient.USER);
        if (null != client) {
            return;
        }
        String scheme = "http";
        if (StringUtils.isNotEmpty(krb5Path)) {
            System.setProperty("elasticsearch.kerberos.ServerRealm.prefix", System.getenv("elasticsearch.kerberos.ServerRealm.prefix") != null ? "HTTP/" : "");
            //华为云es认证模式
            setSecConfig();
            scheme = "https";
        }

        String finalScheme = scheme;
        if (esClient.IPS.contains(":")) {
            builder = RestClient.builder(Arrays.asList(esClient.IPS.split(",")).stream().map(m -> {
                String[] ipInfo = m.split(":");
                log.error("elasticsearch node : " + ipInfo[0] + ":" + ipInfo[1]);
                return new HttpHost(ipInfo[0], Integer.valueOf(ipInfo[1]), finalScheme);
            }).collect(Collectors.toList()).toArray(new HttpHost[0]));
        } else {
            builder = RestClient.builder(Arrays.asList(esClient.IPS.split(",")).stream().map(m -> {
                log.info("elasticsearch node : " + m + ":" + esClient.PORT);
                return new HttpHost(m, esClient.PORT, finalScheme);
            }).collect(Collectors.toList()).toArray(new HttpHost[0]));
        }

        // 服务认证
        if (StringUtils.isEmpty(krb5Path) && StringUtils.isNotEmpty(esClient.USER) && StringUtils.isNotEmpty(esClient.PASSWORD)) {
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

        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                requestConfigBuilder.setConnectTimeout(CONNECT_TIME_OUT);
                requestConfigBuilder.setSocketTimeout(SOCKET_TIME_OUT);
                requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);
                return requestConfigBuilder;
            }
        });

        refreshClientToken();
    }

    private static Header[] addBasicHeader() {

        Header[] defaultHeaders = new Header[]{new BasicHeader("Accept", "application/json"), new BasicHeader("Content-type", "application/json")};
        builder.setDefaultHeaders(defaultHeaders);
        return defaultHeaders;
    }

    private static Header[] addAuthHeader() {
        Header[] defaultHeaders = null;
        if (StringUtils.isNotEmpty(krb5Path)) {

            System.setProperty("java.security.auth.login.config", jaasPath);
            System.setProperty("es.security.indication", "true");
            System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");

            String securityToken = HWRestTokenBuilder.getSecurityToken(true);
            log.info("----------------securityToken通用:" + securityToken);
            defaultHeaders = new Header[]{
                    new BasicHeader("Accept", "application/json"),
                    new BasicHeader("Content-type", "application/json"),
                    new BasicHeader("Authorization", "Negotiate " + securityToken),
                    new BasicHeader("Accept", "application/json")
            };

            builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    httpClientBuilder.setMaxConnTotal(MAX_CONNECT_NUM);
                    httpClientBuilder.setMaxConnPerRoute(MAX_CONNECT_PER_ROUTE);
                    //httpClientBuilder.setDefaultHeaders(Arrays.asList(defaultHeaders));
                    //httpClientBuilder.disableAuthCaching();
                    try {
                        //绕过https证书
                        httpClientBuilder.setSSLStrategy(HttpTools.getSchemeIOSessionStrategy());
                    } catch (Exception e) {
                        log.error("设置ssl无证书失败", e);
                    }
                    return httpClientBuilder;
                }
            });

            builder.setDefaultHeaders(defaultHeaders);
            RestHighLevelClient tmpClient = new RestHighLevelClient(builder);
            String cookie = "";
            try {
                Response response = tmpClient.getLowLevelClient().performRequest(new Request("get", "/"));
                //首次发起es请求时会返回set-cookie, 这个cookie在后续请求时必须带上
                cookie = response.getHeader("Set-Cookie");
                log.info("security es set-cookie:" + cookie);
            } catch (IOException e) {
                log.error("", e);
            }
            defaultHeaders[3] = new BasicHeader("Cookie", cookie);
            builder.setDefaultHeaders(defaultHeaders);


        } else {
            defaultHeaders = new Header[]{new BasicHeader("Accept", "application/json"), new BasicHeader("Content-type", "application/json")};
            builder.setDefaultHeaders(defaultHeaders);
        }

        return defaultHeaders;
    }

    public static void refreshClientToken() {
        addAuthHeader();
        //重新构建RestClient
        client = new RestHighLevelClient(builder);
        EsCurdTools.setClient(client);
    }

    /**
     * Set configurations about authentication.
     *
     * @throws Exception
     */
    private static void setSecConfig() {
        log.info("krb5ConfFile: " + krb5Path);
        System.setProperty("java.security.krb5.conf", krb5Path);
        log.info("jaasPath: " + jaasPath);

        System.setProperty("java.security.auth.login.config", jaasPath);
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");

        // add for ES security indication
        System.setProperty("es.security.indication", "true");
        System.setProperty("elasticsearch.kerberos.jaas.appname", "EsClient");

        log.info("es.security.indication is  " + System.getProperty("es.security.indication"));
        EsCurdTools.SCHEMA = "https";
        HWRestTokenBuilder.setHostArray(esClient.IPS, esClient.PORT, "https");
        HWRestTokenBuilder.setOnceKeyTabConfig(null, null, jaasPath);
    }


    public synchronized static RestHighLevelClient getInstance() {
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

    public String getKrb5Path() {
        return krb5Path;
    }

    @Value("${elk.krb5:}")
    public void setKrb5Path(String krb5Path) {
        EsClient.krb5Path = krb5Path;
    }

    public static String getJaasPath() {
        return jaasPath;
    }

    @Value("${elk.jaas:}")
    public void setJaasPath(String jaasPath) {
        EsClient.jaasPath = jaasPath;
    }

    public static String getKeytab() {
        return keytab;
    }

    @Value("${elk.keytab:}")
    public static void setKeytab(String keytab) {
        EsClient.keytab = keytab;
    }

    public static String getVersion() {
        return version;
    }

    @Value("${elk.version:7}")
    public void setVersion(String version) {
        EsClient.version = version;
    }
}
