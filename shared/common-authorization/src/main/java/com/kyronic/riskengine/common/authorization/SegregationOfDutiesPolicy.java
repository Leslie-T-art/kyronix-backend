package com.kyronic.riskengine.common.authorization;

import com.kyronic.riskengine.common.api.ErrorCodes;

import java.util.UUID;

public class SegregationOfDutiesPolicy {

    public void validate(UUID inputterUserId, UUID lastModifiedBy, UUID authorizerUserId) {
        if (inputterUserId.equals(authorizerUserId)) {
            throw new AuthorizationException("The inputter cannot authorize the same record", ErrorCodes.MAKER_CANNOT_AUTHORIZE);
        }
        if (lastModifiedBy.equals(authorizerUserId)) {
            throw new AuthorizationException("The last editor cannot authorize the same record", ErrorCodes.LAST_EDITOR_CANNOT_AUTHORIZE);
        }
    }
}
