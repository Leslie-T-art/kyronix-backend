package com.kyronic.riskengine.olts.application.mapper;

import com.kyronic.riskengine.olts.application.dto.IncidentResponse;
import com.kyronic.riskengine.olts.domain.model.OltsIncident;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IncidentMapper {

    @Mapping(target = "departmentName", ignore = true)
    @Mapping(target = "branchName", ignore = true)
    IncidentResponse toResponse(OltsIncident incident);
}
