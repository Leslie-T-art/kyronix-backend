package com.kyronic.riskengine.common.authorization;

import com.kyronic.riskengine.common.api.ErrorCodes;

public class SegregationOfDutiesPolicy {

    public void validate(Long inputterUserId, Long lastModifiedBy, Long authorizerUserId) {
        if (inputterUserId.equals(authorizerUserId)) {
            throw new AuthorizationException("The inputter cannot authorize the same record", ErrorCodes.MAKER_CANNOT_AUTHORIZE);
        }
        if (lastModifiedBy.equals(authorizerUserId)) {
            throw new AuthorizationException("The last editor cannot authorize the same record", ErrorCodes.LAST_EDITOR_CANNOT_AUTHORIZE);
        }
    }
}
