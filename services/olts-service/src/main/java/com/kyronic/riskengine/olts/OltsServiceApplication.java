package com.kyronic.riskengine.olts;

import com.kyronic.riskengine.common.observability.ServletAuditAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ServletAuditAutoConfiguration.class)
public class OltsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OltsServiceApplication.class, args);
    }
}
