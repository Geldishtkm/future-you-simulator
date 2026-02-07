package org.example.dto;

import java.time.LocalDate;

/**
 * DTO for export data response.
 */
public class ExportDataDto {
    private LocalDate exportDate;
    private String format;
    private String data;

    public ExportDataDto() {
    }

    public LocalDate getExportDate() { return exportDate; }
    public void setExportDate(LocalDate exportDate) { this.exportDate = exportDate; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}

