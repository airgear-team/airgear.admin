package com.airgear.admin.config.security.properties;

import javax.validation.constraints.NotEmpty;

public class AirGearJWTProperties {

    @NotEmpty(message = "secret must not be empty")
    private String secret;

    @NotEmpty(message = "key must not be empty")
    private String key;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
