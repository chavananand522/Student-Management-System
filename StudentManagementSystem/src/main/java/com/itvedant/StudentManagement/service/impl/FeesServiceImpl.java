package com.itvedant.StudentManagement.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.model.FeePayment;
import com.itvedant.StudentManagement.reposatory.FeePaymentRepository;
import com.itvedant.StudentManagement.services.FeesService;

@Service
public class FeesServiceImpl implements FeesService {

    private final FeePaymentRepository feePaymentRepository;

    public FeesServiceImpl(FeePaymentRepository feePaymentRepository) {
        this.feePaymentRepository = feePaymentRepository;
    }

    @Override
    public List<FeePayment> getPaymentsByStudent(long studentId) {
        return feePaymentRepository.findByStudentIdOrderByPaymentDateDesc(studentId);
    }

    @Override
    public double getTotalPaid(long studentId) {
        return feePaymentRepository.getTotalPaidByStudent(studentId);
    }
}