package com.kyronic.riskengine.processflows.application.service;

import com.kyronic.riskengine.processflows.domain.ProcessFlowRecord;

public interface ProcessFlowNotificationPublisher {

    void publishApproved(ProcessFlowRecord record);

    void publishRejected(ProcessFlowRecord record);

    void publishReturned(ProcessFlowRecord record);
}
