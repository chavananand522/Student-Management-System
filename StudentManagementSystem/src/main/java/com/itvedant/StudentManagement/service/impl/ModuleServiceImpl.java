package com.itvedant.StudentManagement.service.impl;

import com.itvedant.StudentManagement.model.Module;
import com.itvedant.StudentManagement.reposatory.ModuleRepository;
import com.itvedant.StudentManagement.services.ModuleService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {

	private final ModuleRepository moduleRepository;

	public ModuleServiceImpl(ModuleRepository moduleRepository) {

		this.moduleRepository = moduleRepository;
	}

	@Override
	public Page<Module> getModules(int page, int size) {

		return moduleRepository.findAll(PageRequest.of(page, size));
	}

	@Override
	public List<Module> getAllModules() {

		return moduleRepository.findAll();
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

		existingModule.setSubject(module.getSubject());

		existingModule.setName(module.getName());

		existingModule.setDescription(module.getDescription());

		return moduleRepository.save(existingModule);
	}

	@Override
	public void deleteModule(Long id) {

		Module module = getModuleById(id);

		moduleRepository.delete(module);
	}

}