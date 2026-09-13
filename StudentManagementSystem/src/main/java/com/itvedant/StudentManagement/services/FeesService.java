package com.itvedant.StudentManagement.services;

import java.util.List;

import com.itvedant.StudentManagement.model.FeePayment;

public interface FeesService {

    List<FeePayment> getPaymentsByStudent(long studentId);

    double getTotalPaid(long studentId);
}