package com.itvedant.StudentManagement.reposatory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itvedant.StudentManagement.model.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {

    List<Test> findByStatusIgnoreCase(String status);

}