package com.kyronic.riskengine.processflows;

import com.kyronic.riskengine.common.observability.ServletAuditAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ServletAuditAutoConfiguration.class)
public class ProcessFlowsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProcessFlowsServiceApplication.class, args);
    }
}
