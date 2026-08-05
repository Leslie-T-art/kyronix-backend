package com.kyronic.riskengine.common.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class MakerCheckerMetrics {

    private final Counter submittedCounter;
    private final Counter approvedCounter;
    private final Counter rejectedCounter;

    public MakerCheckerMetrics(MeterRegistry meterRegistry, String serviceName) {
        this.submittedCounter = Counter.builder("kyronic.authorization.submitted")
                .tag("service", serviceName)
                .register(meterRegistry);
        this.approvedCounter = Counter.builder("kyronic.authorization.approved")
                .tag("service", serviceName)
                .register(meterRegistry);
        this.rejectedCounter = Counter.builder("kyronic.authorization.rejected")
                .tag("service", serviceName)
                .register(meterRegistry);
    }

    public void submitted() {
        submittedCounter.increment();
    }

    public void approved() {
        approvedCounter.increment();
    }

    public void rejected() {
        rejectedCounter.increment();
    }
}
