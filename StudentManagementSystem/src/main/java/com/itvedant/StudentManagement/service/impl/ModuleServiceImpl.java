package com.itvedant.StudentManagement.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.model.Module;
import com.itvedant.StudentManagement.reposatory.ModuleRepository;
import com.itvedant.StudentManagement.services.ModuleService;

@Service
public class ModuleServiceImpl implements ModuleService {

	private final ModuleRepository moduleRepository;

	public ModuleServiceImpl(ModuleRepository moduleRepository) {
		this.moduleRepository = moduleRepository;
	}

	@Override
	public List<Module> getModulesBySubject(String subject) {
		return moduleRepository.findBySubjectIgnoreCaseOrderByModuleNumberAsc(subject);
	}

	@Override
	public Module getModuleById(Long id) {
		return moduleRepository.findById(id).orElseThrow(() -> new RuntimeException("Module not found with id: " + id));
	}

	@Override
	public Module createModule(Module module) {
		return moduleRepository.save(module);
	}

	@Override
	public Module updateModule(Long id, Module module) {

		Module existingModule = getModuleById(id);

		existingModule.setModuleNumber(module.getModuleNumber());
		existingModule.setName(module.getName());
		existingModule.setSubject(module.getSubject());
		existingModule.setDescription(module.getDescription());

		return moduleRepository.save(existingModule);
	}

	@Override
	public void deleteModule(Long id) {

		Module module = getModuleById(id);

		moduleRepository.delete(module);
	}
}