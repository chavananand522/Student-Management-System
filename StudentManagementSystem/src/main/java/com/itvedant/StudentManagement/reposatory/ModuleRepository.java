package com.itvedant.StudentManagement.reposatory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itvedant.StudentManagement.model.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findBySubjectIgnoreCaseOrderByModuleNumberAsc(
            String subject
    );

    Optional<Module> findBySubjectIgnoreCaseAndNameIgnoreCase(
            String subject,
            String name
    );
}