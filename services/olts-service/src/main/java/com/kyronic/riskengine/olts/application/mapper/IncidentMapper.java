package com.kyronic.riskengine.olts.application.mapper;

import com.kyronic.riskengine.olts.application.dto.IncidentResponse;
import com.kyronic.riskengine.olts.domain.model.OltsIncident;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IncidentMapper {

    IncidentResponse toResponse(OltsIncident incident);
}
