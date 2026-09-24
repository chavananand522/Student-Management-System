package com.itvedant.StudentManagement.services;

import java.util.List;

import com.itvedant.StudentManagement.model.Module;

public interface ModuleService {

    List<Module> getModulesBySubject(String subject);

    Module getModuleById(Long id);

    Module createModule(Module module);

    Module updateModule(Long id, Module module);

    void deleteModule(Long id);
}