package com.itvedant.StudentManagement.reposatory;

import com.itvedant.StudentManagement.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {


    List<Test> findByStatusIgnoreCase(String status);


    Test findFirstByStatusIgnoreCaseOrderByIdDesc(String status);
}