package com.vrv.vap.admin.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vap.register")
public class RegisterProperties {
    private String name;
    private String manufacturer;
    private String device_type;
    private String service_type;
    private String username;
    private String security_key;
    private String token_url;
    private String login_url;
    private String online_url;
    private String description;
    private String region;
    private String zone;
    private boolean enabled;
    private String extra;
    private String register_url;
    private String client_id;
    private String client_secret;
}
