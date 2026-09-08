
package com.itvedant.StudentManagement.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.itvedant.StudentManagement.services.ExcelExportServices;

@Controller
public class SettingsController {

    private final ExcelExportServices excelExportService;

    public SettingsController(ExcelExportServices excelExportService) {
        this.excelExportService = excelExportService;
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        return "settings";
    }

    @GetMapping("/settings/download-students")
    public ResponseEntity<byte[]> downloadStudentsExcel() throws Exception {

        byte[] file = excelExportService.exportStudentsAndEnrollments();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=students.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }
}

