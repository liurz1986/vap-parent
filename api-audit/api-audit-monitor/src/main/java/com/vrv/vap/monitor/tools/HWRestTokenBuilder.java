package com.vrv.vap.monitor.tools;

import com.sun.security.auth.module.Krb5LoginModule;
import com.vrv.vap.monitor.es.client.EsClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.security.authentication.util.KerberosUtil;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginException;

/**
 * 华为云ES https token生成器
 */
public class HWRestTokenBuilder {
    private static Log LOG = LogFactory.getLog(HWRestTokenBuilder.class);
    private static final boolean IS_IBM_JDK = System.getProperty("java.vendor").contains("IBM");
    private static HttpHost[] HOSTS;

    private static String keytab;
    private static String principal;
    private static String jaasPath;

    private static volatile boolean runningRefresh = false;

    private static volatile String securityToken;

    private static ExecutorService esService = Executors.newFixedThreadPool(1, r->{
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });

    /**
     * 获取ES 认证token
     *
     * @return
     */
    public synchronized static String getSecurityToken(boolean refresh) {
       /* if (!refresh && securityToken != null) {
            return securityToken;
        }*/
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
        //设置ES认证
        try {
            setEsConfig();
        } catch (IOException e) {
        }
        AppConfigurationEntry[] entries = Configuration.getConfiguration().getAppConfigurationEntry("EsClient");
        LOG.info(entries[0].getOptions());
        securityToken = RestClientTokenBuilder.build(getHostArray()[0].toHostString()).getSecurityToken();
        LOG.info("securityToken已刷新:" + securityToken);
        return securityToken;
    }

    private static void setEsConfig() throws IOException {
        System.setProperty("elasticsearch.kerberos.jaas.appname", "EsClient");
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
        System.setProperty("java.security.auth.login.config", jaasPath);
        /*String userKeytabPath = new File(keytab).getAbsolutePath();
        String userPrincipal = principal;
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            userKeytabPath = "/" + userKeytabPath.replaceAll("\\\\", "/");
        }
        setJaasConf("EsClient", userPrincipal, userKeytabPath);*/
    }

    public static HttpHost[] getHostArray() {
        return HOSTS;
    }

    public static void setHostArray(String esServerHost, int port, String schema) {
        List<HttpHost> hosts = new ArrayList<HttpHost>();
        String[] hostArray1 = esServerHost.split(",");

        for (String host : hostArray1) {
            HttpHost hostNew = new HttpHost(host, port, schema);
            hosts.add(hostNew);
        }
        HOSTS = hosts.toArray(new HttpHost[]{});
    }

    public static void setOnceKeyTabConfig(String keytabPath, String principalName) {
        keytab = keytabPath;
        principal = principalName;
        //设置ES认证
        try {
            setEsConfig();
        } catch (IOException e) {
            LOG.error("input principal,keytabPath is invalid.");
        }
    }

    public static void setOnceKeyTabConfig(String keytabPath, String principalName, String jaasPath) {
        keytab = keytabPath;
        principal = principalName;
        HWRestTokenBuilder.jaasPath = jaasPath;
        //设置ES认证
        try {
            setEsConfig();
            if ((jaasPath == null) || (jaasPath.length() <= 0)) {
                LOG.error("input jaasPath is invalid.");
            }

            if (jaasPath != null && !runningRefresh) {
                System.setProperty("java.security.auth.login.config", jaasPath);
                System.setProperty("es.security.indication", "true");
                System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
                esService.execute(()->{
                    runningRefresh = true;
                    while(true){
                        try {
                            TimeUnit.HOURS.sleep(1);
                            //getSecurityToken(true);
                            EsClient.refreshClientToken();
                        } catch (Exception e) {
                        }
                    }
                });
                esService.shutdown();
            }


        } catch (IOException e) {
            LOG.error("input principal,keytabPath is invalid.");
        }
    }

    private static void setJaasConf(String loginContextName, String principal, String keytabFile)
            throws IOException {
        if ((loginContextName == null) || (loginContextName.length() <= 0)) {
            LOG.error("input loginContextName is invalid.");
            throw new IOException("input loginContextName is invalid.");
        }

        if ((principal == null) || (principal.length() <= 0)) {
            LOG.error("input principal is invalid.");
            throw new IOException("input principal is invalid.");
        }

        if ((keytabFile == null) || (keytabFile.length() <= 0)) {
            LOG.error("input keytabFile is invalid.");
            throw new IOException("input keytabFile is invalid.");
        }

        File userKeytabFile = new File(keytabFile);
        if (!userKeytabFile.exists()) {

            LOG.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
            throw new IOException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
        }
        keytabFile = userKeytabFile.getAbsolutePath();
        Configuration.setConfiguration(new JaasConfiguration(loginContextName, principal,
                keytabFile));

        Configuration conf = Configuration.getConfiguration();
        if (!(conf instanceof JaasConfiguration)) {
            LOG.error("javax.security.auth.login.Configuration is not JaasConfiguration.");
            throw new IOException("javax.security.auth.login.Configuration is not JaasConfiguration.");
        }

        AppConfigurationEntry[] entrys = conf.getAppConfigurationEntry(loginContextName);
        if (entrys == null) {
            LOG.error("javax.security.auth.login.Configuration has no AppConfigurationEntry named " + loginContextName
                    + ".");
            throw new IOException("javax.security.auth.login.Configuration has no AppConfigurationEntry named "
                    + loginContextName + ".");
        }

        boolean checkPrincipal = false;
        boolean checkKeytab = false;
        for (int i = 0; i < entrys.length; i++) {
            if (entrys[i].getOptions().get("principal").equals(principal)) {
                checkPrincipal = true;
            }

            if (IS_IBM_JDK) {
                if (entrys[i].getOptions().get("useKeytab").equals(keytabFile)) {
                    checkKeytab = true;
                }
            } else {
                if (entrys[i].getOptions().get("keyTab").equals(keytabFile)) {
                    checkKeytab = true;
                }
            }

        }

        if (!checkPrincipal) {
            LOG.error("AppConfigurationEntry named " + loginContextName + " does not have principal value of "
                    + principal + ".");
            throw new IOException("AppConfigurationEntry named " + loginContextName
                    + " does not have principal value of " + principal + ".");
        }

        if (!checkKeytab) {
            LOG.error("AppConfigurationEntry named " + loginContextName + " does not have keyTab value of "
                    + keytabFile + ".");
            throw new IOException("AppConfigurationEntry named " + loginContextName + " does not have keyTab value of "
                    + keytabFile + ".");
        }

    }

    /**
     * copy from hbase zkutil 0.94&0.98 A JAAS configuration that defines the login modules that we want to use for
     * login.
     */
    private static class JaasConfiguration extends Configuration {
        private static final Map<String, String> BASIC_JAAS_OPTIONS = new HashMap<String, String>();

        static {
            String jaasEnvVar = System.getenv("HBASE_JAAS_DEBUG");
            if (jaasEnvVar != null && "true".equalsIgnoreCase(jaasEnvVar)) {
                BASIC_JAAS_OPTIONS.put("debug", "true");
            }
        }

        private static final Map<String, String> KEYTAB_KERBEROS_OPTIONS = new HashMap<String, String>();

        static {
            if (IS_IBM_JDK) {
                KEYTAB_KERBEROS_OPTIONS.put("credsType", "both");
            } else {
                KEYTAB_KERBEROS_OPTIONS.put("useKeyTab", "true");
                KEYTAB_KERBEROS_OPTIONS.put("useTicketCache", "false");

                KEYTAB_KERBEROS_OPTIONS.put("storeKey", "true");
            }

            KEYTAB_KERBEROS_OPTIONS.putAll(BASIC_JAAS_OPTIONS);
        }


        private static final AppConfigurationEntry KEYTAB_KERBEROS_LOGIN = new AppConfigurationEntry(
                KerberosUtil.getKrb5LoginModuleName(), AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, KEYTAB_KERBEROS_OPTIONS);

        private static final AppConfigurationEntry[] KEYTAB_KERBEROS_CONF =
                new AppConfigurationEntry[]{KEYTAB_KERBEROS_LOGIN};

        private Configuration baseConfig;

        private final String loginContextName;

        private final boolean useTicketCache;

        private final String keytabFile;

        private final String principal;


        public JaasConfiguration(String loginContextName, String principal, String keytabFile) throws IOException {
            this(loginContextName, principal, keytabFile, keytabFile == null || keytabFile.length() == 0);
        }

        private JaasConfiguration(String loginContextName, String principal, String keytabFile, boolean useTicketCache) throws IOException {
            try {
                this.baseConfig = Configuration.getConfiguration();
            } catch (SecurityException e) {
                this.baseConfig = null;
            }
            this.loginContextName = loginContextName;
            this.useTicketCache = useTicketCache;
            this.keytabFile = keytabFile;
            this.principal = principal;

            initKerberosOption();
            LOG.info("JaasConfiguration loginContextName=" + loginContextName + " principal=" + principal
                    + " useTicketCache=" + useTicketCache + " keytabFile=" + keytabFile);
        }

        private void initKerberosOption() throws IOException {
            if (!useTicketCache) {
                KEYTAB_KERBEROS_OPTIONS.put("storeKey", "true");
                KEYTAB_KERBEROS_OPTIONS.put("keyTab", keytabFile);
                KEYTAB_KERBEROS_OPTIONS.put("useKeyTab", "true");
                KEYTAB_KERBEROS_OPTIONS.put("useTicketCache", useTicketCache ? "true" : "false");
//                KEYTAB_KERBEROS_OPTIONS.put("useTicketCache",  "true");
            }

            if (("Client").equals(loginContextName)) {
                //zookeeper需要设置true
                KEYTAB_KERBEROS_OPTIONS.put("doNotPrompt", "true");
            }
            KEYTAB_KERBEROS_OPTIONS.put("debug", "false");
            KEYTAB_KERBEROS_OPTIONS.put("principal", principal);
        }

        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String appName) {
            if (loginContextName.equals(appName)) {
                return KEYTAB_KERBEROS_CONF;
            }
            if (baseConfig != null){
                return baseConfig.getAppConfigurationEntry(appName);
            }
            return (null);
        }
    }


    private static class RestClientTokenBuilder {
        private static final Log LOG = LogFactory.getLog(RestClientTokenBuilder.class);
        public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 1000;
        public static final int DEFAULT_SOCKET_TIMEOUT_MILLIS = 30000;
        public static final int DEFAULT_MAX_RETRY_TIMEOUT_MILLIS = 30000;
        public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT_MILLIS = 500;
        public static final int DEFAULT_MAX_CONN_PER_ROUTE = 10;
        public static final int DEFAULT_MAX_CONN_TOTAL = 30;
        private static final Header[] EMPTY_HEADERS = new Header[0];
        private static final boolean isSecureMode = System.getProperty("java.security.auth.login.config") != null;
        private static final String AUTH_USE_SUBJECT_CREDS_ONLY = "javax.security.auth.useSubjectCredsOnly";
        private static final String SPNEGO_OID = "1.3.6.1.5.5.2";
        private static final String KERBEROS_V5_PRINCIPAL_NAME = "1.2.840.113554.1.2.2.1";
        private static final String SERVICE_PRINCIPAL_NAME_PREFIX = "HTTP/";
        private static String SCHEME_HTTPS = "https://";
        private static final String ELASTICSEARCH_SERVERREALM_PATH = System.getProperty("elasticsearch.server.realm.path", "/elasticsearch/serverrealm");
        private int maxRetryTimeout = 30000;
        private Header[] defaultHeaders;
        private HttpClientConfigCallback httpClientConfigCallback;
        private RequestConfigCallback requestConfigCallback;
        private String pathPrefix;
        private static final Lookup<AuthSchemeProvider> AUTH_SCHEME_REGISTRY = (Lookup) RegistryBuilder.create().register("Negotiate", new SPNegoSchemeFactory(true)).build();
        private static final BasicCredentialsProvider CREDENTIALS_PROVIDER = new BasicCredentialsProvider();
        private static final SSLContext SSL_CONTEXT;
        private static final HostnameVerifier HOSTNAME_VERIFIER = new NoopHostnameVerifier();
        private String hostAnPort;

        public static RestClientTokenBuilder build(String hostAnPort){
            RestClientTokenBuilder builder = new RestClientTokenBuilder();
            Header[] defaultHeaders = new Header[] { new BasicHeader("Accept", "application/json"),
                    new BasicHeader("Content-type", "application/json") };
            builder.setRequestConfigCallback(new RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                    return requestConfigBuilder.setConnectTimeout(10000).setSocketTimeout(60000);
                }
            }).setMaxRetryTimeoutMillis(10000).setDefaultHeaders(defaultHeaders);
            builder.hostAnPort = hostAnPort;
            return builder;
        }

        public String getSecurityToken(String ... hosts) {
            String host = hosts.length>0?hosts[0]:this.hostAnPort;
            String jaasAppName = System.getProperty("elasticsearch.kerberos.jaas.appname", "Client");
            AppConfigurationEntry[] entries = Configuration.getConfiguration().getAppConfigurationEntry(jaasAppName);
            Map<String, String> options = new HashMap();
            AppConfigurationEntry[] var5 = entries;
            int var6 = entries.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                AppConfigurationEntry entry = var5[var7];
                options.putAll((Map<? extends String, ? extends String>) entry.getOptions());
            }

            Subject subj = new Subject();
            Krb5LoginModule krb5 = new Krb5LoginModule();
            krb5.initialize(subj, (CallbackHandler)null, (Map)null, options);

            try {
                krb5.login();
                krb5.commit();
                LOG.info("Get kerberos TGT successfully.");
            } catch (LoginException var9) {
                LOG.error("Get kerberos TGT failed.");
                throw new RuntimeException(var9);
            }

            String serverRealm = this.getServerRealm( host);
            if (serverRealm == null) {
                throw new IllegalArgumentException("Get security realm failed.");
            } else {
                byte[] token = this.initiateSecurityContext(subj,System.getProperty("elasticsearch.kerberos.ServerRealm.prefix", "") + serverRealm);
                if (token == null) {
                    throw new IllegalArgumentException("Get security token failed.");
                } else {
                    LOG.info("Get security token successfully.");
                    return (new Base64(0)).encodeToString(token);
                }
            }
        }

        private byte[] initiateSecurityContext(Subject subject, final String servicePrincipalName) {
            byte[] token = (byte[])Subject.doAs(subject, new PrivilegedAction<byte[]>() {
                @Override
                public byte[] run() {
                    GSSContext context = null;

                    Object var3;
                    try {
                        context = RestClientTokenBuilder.this.getGssContext(servicePrincipalName);
                        context.requestMutualAuth(true);
                        context.requestCredDeleg(true);
                        byte[] token = new byte[0];
                        byte[] var15 = context.initSecContext(token, 0, token.length);
                        return var15;
                    } catch (GSSException var13) {
                        RestClientTokenBuilder.LOG.error("Init secure context failed.", var13);
                        var3 = null;
                    } finally {
                        if (context != null) {
                            try {
                                context.dispose();
                            } catch (GSSException var12) {
                                RestClientTokenBuilder.LOG.error("Dispose secure context failed.", var12);
                            }
                        }

                    }

                    return (byte[])var3;
                }
            });
            return token;
        }

        private GSSContext getGssContext(String servicePrincipalName) throws GSSException {
            GSSManager manager = GSSManager.getInstance();
            GSSName serverName = manager.createName(servicePrincipalName, new Oid("1.2.840.113554.1.2.2.1"));
            Oid oid = new Oid("1.3.6.1.5.5.2");
            GSSContext context = manager.createContext(serverName.canonicalize(oid), oid, (GSSCredential)null, 0);
            return context;
        }

        private String getServerRealm( String hostAndPort) {
            String serverRealm = null;
            InputStream is = null;

            try {
                HttpClientBuilder builder = HttpClientBuilder.create();
                builder.setDefaultAuthSchemeRegistry(AUTH_SCHEME_REGISTRY);
                builder.setDefaultCredentialsProvider(CREDENTIALS_PROVIDER);
                builder.setSSLHostnameVerifier(HOSTNAME_VERIFIER);
                builder.setSSLContext(SSL_CONTEXT);
                HttpClient client = builder.build();
                HttpGet httpGet = new HttpGet(SCHEME_HTTPS + hostAndPort + ELASTICSEARCH_SERVERREALM_PATH);
                HttpResponse response = client.execute(httpGet);
                int httpStatus = response.getStatusLine().getStatusCode();
                if (200 == httpStatus) {
                    is = response.getEntity().getContent();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    byte[] buffer = new byte[64];
                    boolean var12 = true;

                    int size;
                    while(-1 != (size = is.read(buffer))) {
                        os.write(buffer, 0, size);
                    }

                    serverRealm = os.toString(StandardCharsets.UTF_8.displayName());
                    LOG.info("Get the service realm " + serverRealm);
                } else {
                    LOG.error("Cannot get server realm.");
                }
            } catch (Throwable var21) {
                LOG.error("Get server realm failed.", var21);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException var20) {
                        LOG.error("Close http response input stream failed.", var20);
                    }
                }

            }

            return serverRealm;
        }

        private String getServerRealm2( String hostAndPort) {
            return hostAndPort+"@HADOOP.COM";
        }

        public RestClientTokenBuilder setRequestConfigCallback(RequestConfigCallback requestConfigCallback) {
            Objects.requireNonNull(requestConfigCallback, "requestConfigCallback must not be null");
            this.requestConfigCallback = requestConfigCallback;
            return this;
        }

        static {
            CREDENTIALS_PROVIDER.setCredentials(AuthScope.ANY, new Credentials() {
                @Override
                public Principal getUserPrincipal() {
                    return null;
                }

                @Override
                public String getPassword() {
                    return null;
                }
            });
            TrustStrategy trustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    return true;
                }
            };

            try {
                SSL_CONTEXT = (new SSLContextBuilder()).loadTrustMaterial((KeyStore)null, trustStrategy).build();
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException var2) {
                LOG.error("Init ssl context failed.", var2);
                throw new RuntimeException(var2);
            }

            if (isSecureMode) {
                System.setProperty("javax.security.auth.useSubjectCredsOnly", System.getProperty("javax.security.auth.useSubjectCredsOnly", "false"));
            }

        }

        public interface HttpClientConfigCallback {
            HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder var1);
        }

        public interface RequestConfigCallback {
            RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder var1);
        }


        public RestClientTokenBuilder setMaxRetryTimeoutMillis(int maxRetryTimeoutMillis) {
            if (maxRetryTimeoutMillis <= 0) {
                throw new IllegalArgumentException("maxRetryTimeoutMillis must be greater than 0");
            } else {
                this.maxRetryTimeout = maxRetryTimeoutMillis;
                return this;
            }
        }

        public RestClientTokenBuilder setDefaultHeaders(Header[] defaultHeaders) {
            Objects.requireNonNull(defaultHeaders, "defaultHeaders must not be null");
            Header[] var2 = defaultHeaders;
            int var3 = defaultHeaders.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Header defaultHeader = var2[var4];
                Objects.requireNonNull(defaultHeader, "default header must not be null");
            }

            this.defaultHeaders = defaultHeaders;
            return this;
        }
    }
}
