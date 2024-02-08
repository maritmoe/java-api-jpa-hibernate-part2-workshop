package com.booleanuk.api.controller;

import com.booleanuk.api.model.Department;
import com.booleanuk.api.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("departments")
public class DepartmentController {
    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping
    public List<Department> getAllDepartments() {
        return this.departmentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getById(@PathVariable int id) {
        Department department = this.getDepartment(id);
        return ResponseEntity.ok(department);
    }

    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        this.checkHasRequiredValues(department);
        return new ResponseEntity<>(this.departmentRepository.save(department), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Department> deleteDepartment(@PathVariable int id) {
        Department departmentToDelete = this.getDepartment(id);
        this.departmentRepository.delete(departmentToDelete);
        return ResponseEntity.ok(departmentToDelete);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable int id, @RequestBody Department department) {
        this.checkHasRequiredValues(department);
        Department departmentToUpdate = this.getDepartment(id);
        departmentToUpdate.setName(department.getName());
        departmentToUpdate.setLocation(department.getLocation());
        return new ResponseEntity<>(this.departmentRepository.save(departmentToUpdate), HttpStatus.CREATED);
    }

    private Department getDepartment(int id) {
        return this.departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No departments matching that id were found"));
    }

    private void checkHasRequiredValues(Department department) {
        if (department.getLocation() == null || department.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please check all required fields are correct.");
        }
    }

}
