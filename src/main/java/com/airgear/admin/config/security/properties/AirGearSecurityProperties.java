package com.airgear.admin.config.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "airgear.admin.security")
public class AirGearSecurityProperties {

    @Valid
    @NestedConfigurationProperty
    private AirGearJWTProperties jwt;

    private Map<@NotBlank String, @Valid AirGearAdminProperties> admins;

    public AirGearJWTProperties getJwt() {
        return jwt;
    }

    public void setJwt(AirGearJWTProperties jwt) {
        this.jwt = jwt;
    }

    public Map<String, AirGearAdminProperties> getAdmins() {
        return admins;
    }

    public void setAdmins(Map<String, AirGearAdminProperties> admins) {
        this.admins = admins;
    }
}
