package com.realestate.due_diligence_agent.dto;

public record DueDiligenceSection(String status, Object data, String error) {
    public static DueDiligenceSection success(Object data) {
        return new DueDiligenceSection("SUCCESS", data, null);
    }
    public static DueDiligenceSection failure(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getMessage() == null) cause = cause.getCause();
        return new DueDiligenceSection("FAILED", null,
                cause.getMessage() == null ? "Service failed" : cause.getMessage());
    }
}
