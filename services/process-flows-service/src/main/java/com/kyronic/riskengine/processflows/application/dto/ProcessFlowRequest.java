package com.kyronic.riskengine.processflows.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class ProcessFlowRequest {

    @NotBlank
    private String processFlowName;

    @NotNull
    private Long departmentId;

    @Size(max = 4000)
    private String description;

    @NotNull
    private LocalDate validFromDate;

    @NotNull
    private LocalDate validToDate;

    private MultipartFile document;

    public String getProcessFlowName() {
        return processFlowName;
    }

    public void setProcessFlowName(String processFlowName) {
        this.processFlowName = processFlowName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(LocalDate validFromDate) {
        this.validFromDate = validFromDate;
    }

    public LocalDate getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(LocalDate validToDate) {
        this.validToDate = validToDate;
    }

    public MultipartFile getDocument() {
        return document;
    }

    public void setDocument(MultipartFile document) {
        this.document = document;
    }
}
