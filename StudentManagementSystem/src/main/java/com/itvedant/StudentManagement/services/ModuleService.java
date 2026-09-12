package com.itvedant.StudentManagement.services;

import com.itvedant.StudentManagement.model.Module;

import org.springframework.data.domain.Page;

import java.util.List;

public interface ModuleService {

	Page<Module> getModules(int page, int size);

	List<Module> getAllModules();

	List<Module> getModulesBySubject(String subject);

	Module getModuleById(Long id);

	Module createModule(Module module);

	Module updateModule(Long id, Module module);

	void deleteModule(Long id);

}