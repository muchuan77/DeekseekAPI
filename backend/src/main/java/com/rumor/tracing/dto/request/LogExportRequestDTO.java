package com.rumor.tracing.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class LogExportRequestDTO {
    private String format; // CSV, EXCEL, PDF
    private List<String> fields;
    private LogSearchRequestDTO searchCriteria;
}