package com.kyronic.riskengine.document;

import com.kyronic.riskengine.common.observability.ServletAuditAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ServletAuditAutoConfiguration.class)
public class DocumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentServiceApplication.class, args);
    }
}
