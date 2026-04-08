package com.example.GrowLink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReportDto {

    @NotNull(message = "Reported user is required")
    private Long reportedUserId;

    @NotBlank(message = "Reason is required")
    @Size(max = 255, message = "Reason must not exceed 255 characters")
    private String reason;

    @Size(max = 500, message = "Details must not exceed 500 characters")
    private String details;

    public ReportDto() {
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public String getReason() {
        return reason;
    }

    public String getDetails() {
        return details;
    }

    public void setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}