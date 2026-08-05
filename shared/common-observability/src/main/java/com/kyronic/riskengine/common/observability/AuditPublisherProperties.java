package com.kyronic.riskengine.common.observability;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kyronic.audit")
public class AuditPublisherProperties {

    private boolean enabled = true;
    private String serviceUrl = "http://localhost:8083";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
}
