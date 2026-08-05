package com.kyronic.riskengine.notifications;

import com.kyronic.riskengine.common.observability.ServletAuditAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ServletAuditAutoConfiguration.class)
public class NotificationsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationsServiceApplication.class, args);
    }
}
