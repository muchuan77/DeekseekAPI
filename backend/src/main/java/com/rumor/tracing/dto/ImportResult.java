package com.rumor.tracing.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResult {
    private int successCount = 0;
    private int errorCount = 0;
    private List<String> errors = new ArrayList<>();
} 