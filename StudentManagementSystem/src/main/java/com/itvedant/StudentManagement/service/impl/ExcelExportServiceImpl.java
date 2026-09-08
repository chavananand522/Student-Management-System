package com.itvedant.StudentManagement.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.itvedant.StudentManagement.dto.EnrollmentExcelDTO;
import com.itvedant.StudentManagement.dto.StudentExcelDTO;
import com.itvedant.StudentManagement.model.Enrollment;
import com.itvedant.StudentManagement.model.Students;
import com.itvedant.StudentManagement.reposatory.EnrollmentRepository;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.services.ExcelExportServices;

@Service
public class ExcelExportServiceImpl implements ExcelExportServices {

    private final StudentRepositiry studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public ExcelExportServiceImpl(StudentRepositiry studentRepository,
                                  EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public byte[] exportStudentsAndEnrollments() throws Exception {

        List<Students> students = studentRepository.findAll();
        List<Enrollment> enrollments = enrollmentRepository.findAll();

        // Convert Students to DTO
        List<StudentExcelDTO> studentDTOs = students.stream()
                .map(student -> new StudentExcelDTO(
                        student.getId(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getEmail(),
                        student.getPhoneNumber()
                ))
                .collect(Collectors.toList());

        // Convert Enrollments to DTO
        List<EnrollmentExcelDTO> enrollmentDTOs = enrollments.stream()
                .map(enrollment -> {
                    Long courseId = null;
                    String courseName = "";
                    Long studentId = null;
                    String studentName = "";

                    if (enrollment.getCourse() != null) {
                        courseId = enrollment.getCourse().getId();
                        courseName = enrollment.getCourse().getCourseName();
                    }

                    if (enrollment.getStudent() != null) {
                        Students student = enrollment.getStudent();
                        studentId = student.getId();

                        String firstName = student.getFirstName() != null ? student.getFirstName() : "";
                        String lastName = student.getLastName() != null ? student.getLastName() : "";
                        studentName = (firstName + " " + lastName).trim();
                    }

                    return new EnrollmentExcelDTO(
                            enrollment.getId(),
                            courseId,
                            courseName,
                            studentId,
                            studentName
                    );
                })
                .collect(Collectors.toList());

        Workbook workbook = new XSSFWorkbook();

        createStudentsSheet(workbook, studentDTOs);
        createEnrollmentsSheet(workbook, enrollmentDTOs);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private void createStudentsSheet(Workbook workbook, List<StudentExcelDTO> students) {
        Sheet sheet = workbook.createSheet("Students");

        String[] headers = {
                "Student ID",
                "First Name",
                "Last Name",
                "Email",
                "Phone"
        };

        createHeader(workbook, sheet, headers);

        int rowNumber = 1;
        for (StudentExcelDTO student : students) {
            Row row = sheet.createRow(rowNumber++);

            row.createCell(0).setCellValue(student.getStudentId() != null ? student.getStudentId() : 0);
            row.createCell(1).setCellValue(student.getFirstName() != null ? student.getFirstName() : "");
            row.createCell(2).setCellValue(student.getLastName() != null ? student.getLastName() : "");
            row.createCell(3).setCellValue(student.getEmail() != null ? student.getEmail() : "");
            row.createCell(4).setCellValue(student.getPhone() != null ? student.getPhone() : "");
        }

        autoSizeColumns(sheet, headers.length);
    }

    private void createEnrollmentsSheet(Workbook workbook, List<EnrollmentExcelDTO> enrollments) {
        Sheet sheet = workbook.createSheet("Enrollments");

        String[] headers = {
                "Enrollment ID",
                "Course ID",
                "Course Name",
                "Student ID",
                "Student Name"
        };

        createHeader(workbook, sheet, headers);

        int rowNumber = 1;
        for (EnrollmentExcelDTO enrollment : enrollments) {
            Row row = sheet.createRow(rowNumber++);

            row.createCell(0).setCellValue(enrollment.getEnrollmentId() != null ? enrollment.getEnrollmentId() : 0);
            row.createCell(1).setCellValue(enrollment.getCourseId() != null ? enrollment.getCourseId() : 0);
            row.createCell(2).setCellValue(enrollment.getCourseName() != null ? enrollment.getCourseName() : "");
            row.createCell(3).setCellValue(enrollment.getStudentId() != null ? enrollment.getStudentId() : 0);
            row.createCell(4).setCellValue(enrollment.getStudentName() != null ? enrollment.getStudentName() : "");
        }

        autoSizeColumns(sheet, headers.length);
    }

    private void createHeader(Workbook workbook, Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
