package com.itvedant.StudentManagement.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.itvedant.StudentManagement.dto.EnrollmentExcelDTO;
import com.itvedant.StudentManagement.model.Courses;
import com.itvedant.StudentManagement.model.Enrollment;
import com.itvedant.StudentManagement.model.Students;
import com.itvedant.StudentManagement.reposatory.EnrollmentRepository;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.services.ExcelExportServices;
import java.math.BigDecimal;

@Service
public class ExcelExportServiceImpl implements ExcelExportServices {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                // Group all of a student's courses together: sort by Student ID first,
                // then by Enrollment ID so a newly added course still lands next to
                // that student's existing rows instead of at the end of the sheet.
                .sorted(Comparator
                        .comparing(EnrollmentExcelDTO::getStudentId,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(EnrollmentExcelDTO::getEnrollmentId,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        Workbook workbook = new XSSFWorkbook();

        createStudentsSheet(workbook, students);
        createEnrollmentsSheet(workbook, enrollmentDTOs);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private void createStudentsSheet(Workbook workbook, List<Students> students) {
        Sheet sheet = workbook.createSheet("Students");

        String[] headers = {
                "ID",
                "Active",
                "Address",
                "Created At",
                "Email",
                "First Name",
                "Last Name",
                "Phone Number",
                "Course Code",
                "Course Name",
                "Course Created At",
                "No. of Courses Enrolled",
                "Total Fee"
        };

        createHeader(workbook, sheet, headers);

        int rowNumber = 1;
        for (Students student : students) {
            Row row = sheet.createRow(rowNumber++);

            List<Enrollment> studentEnrollments = student.getEnrollments();

            String courseCodes = studentEnrollments.stream()
                    .map(Enrollment::getCourse)
                    .filter(c -> c != null)
                    .map(c -> c.getCourseCode() != null ? c.getCourseCode() : "")
                    .collect(Collectors.joining(", "));

            String courseNames = studentEnrollments.stream()
                    .map(Enrollment::getCourse)
                    .filter(c -> c != null)
                    .map(c -> c.getCourseName() != null ? c.getCourseName() : "")
                    .collect(Collectors.joining(", "));

            String courseCreatedDates = studentEnrollments.stream()
                    .map(Enrollment::getCourse)
                    .filter(c -> c != null && c.getCreatedAt() != null)
                    .map(c -> c.getCreatedAt().format(DATE_FMT))
                    .collect(Collectors.joining(", "));

            int courseCount = studentEnrollments.size();

            BigDecimal totalFee = studentEnrollments.stream()
                    .map(Enrollment::getCourse)
                    .filter(c -> c != null && c.getFee() != null)
                    .map(Courses::getFee)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            row.createCell(0).setCellValue(student.getId());
            row.createCell(1).setCellValue(student.isActive() ? "Yes" : "No");
            row.createCell(2).setCellValue(student.getAddress() != null ? student.getAddress() : "");
            row.createCell(3).setCellValue(
                    student.getCreatedAt() != null ? student.getCreatedAt().format(DATE_FMT) : "");
            row.createCell(4).setCellValue(student.getEmail() != null ? student.getEmail() : "");
            row.createCell(5).setCellValue(student.getFirstName() != null ? student.getFirstName() : "");
            row.createCell(6).setCellValue(student.getLastName() != null ? student.getLastName() : "");
            row.createCell(7).setCellValue(student.getPhoneNumber() != null ? student.getPhoneNumber() : "");
            row.createCell(8).setCellValue(courseCodes);
            row.createCell(9).setCellValue(courseNames);
            row.createCell(10).setCellValue(courseCreatedDates);
            row.createCell(11).setCellValue(courseCount);
            row.createCell(12).setCellValue(totalFee.doubleValue());
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