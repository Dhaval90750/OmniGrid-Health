package com.medcore.his.service;

import com.medcore.his.domain.master.Department;
import com.medcore.his.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(UUID id, Department updateParams) {
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
                
        existing.setName(updateParams.getName());
        existing.setCode(updateParams.getCode());
        existing.setDescription(updateParams.getDescription());
        existing.setActive(updateParams.isActive());
        
        return departmentRepository.save(existing);
    }
}
