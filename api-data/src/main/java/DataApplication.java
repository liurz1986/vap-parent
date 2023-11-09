import com.github.tobato.fastdfs.FdfsClientConfig;
import com.vrv.vap.data.component.ESTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author liliang
 */
@MapperScan(basePackages = "com.vrv.vap.data.mapper", sqlSessionFactoryRef = "sqlSessionFactory")
@SpringBootApplication
@EnableDiscoveryClient
@EnableRedisHttpSession
@EnableFeignClients
@ComponentScan({"com.vrv.vap", "com.vrv.vap.syslog.*"})
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@Import(FdfsClientConfig.class)
public class DataApplication {

    @Value("${vap.common.session-base64:false}")
    private Boolean sessionBase64;

    public static void main(String[] args) {
        ApplicationContext appCtx = SpringApplication.run(DataApplication.class, args);
        ESTools esTools = appCtx.getBean(ESTools.class);
        esTools.startWatcher();
    }

    @Bean
    public DefaultCookieSerializer getDefaultCookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setUseBase64Encoding(sessionBase64);
        return cookieSerializer;
    }
}
