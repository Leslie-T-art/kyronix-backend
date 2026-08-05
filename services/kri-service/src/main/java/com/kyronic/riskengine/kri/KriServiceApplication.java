package com.kyronic.riskengine.kri;

import com.kyronic.riskengine.common.observability.ServletAuditAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ServletAuditAutoConfiguration.class)
public class KriServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KriServiceApplication.class, args);
    }
}
