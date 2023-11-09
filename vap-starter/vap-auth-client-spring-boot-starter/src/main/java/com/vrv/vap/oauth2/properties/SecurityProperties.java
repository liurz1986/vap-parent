package com.vrv.vap.oauth2.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wh1107066
 * @date 2021/6/24 14:39
 */
@ConfigurationProperties(prefix = "vap.security")
@Setter
@Getter
public class SecurityProperties {

    private AuthProperties auth = new AuthProperties();

    private PermitProperties ignore = new PermitProperties();

}
