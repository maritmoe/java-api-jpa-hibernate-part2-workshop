package com.booleanuk.api.controller;

import com.booleanuk.api.model.Department;
import com.booleanuk.api.model.Employee;
import com.booleanuk.api.repository.DepartmentRepository;
import com.booleanuk.api.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("employees")
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping
    public List<Employee> getAllEmployees() {
        return this.employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable int id) {
        return ResponseEntity.ok(this.getEmployee(id));
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        this.checkHasRequiredValues(employee);
        Department tempDepartment = this.getDepartment(employee);
        employee.setDepartment(tempDepartment);
        return new ResponseEntity<>(this.employeeRepository.save(employee), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Employee> deleteEmployee(@PathVariable int id) {
        Employee employeeToDelete = this.getEmployee(id);
        this.employeeRepository.delete(employeeToDelete);
        return ResponseEntity.ok(employeeToDelete);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable int id, @RequestBody Employee employee) {
        this.checkHasRequiredValues(employee);
        Employee employeeToUpdate = this.getEmployee(id);
        employeeToUpdate.setFirstName(employee.getFirstName());
        employeeToUpdate.setLastName(employee.getLastName());
        Department tempDepartment = getDepartment(employee);
        employeeToUpdate.setDepartment(tempDepartment);
        return new ResponseEntity<>(this.employeeRepository.save(employeeToUpdate), HttpStatus.CREATED);
    }

    private Employee getEmployee(int id) {
        return this.employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No employee with that id found."));
    }
    private void checkHasRequiredValues(Employee employee) {
        if (employee.getFirstName() == null || employee.getLastName() == null || employee.getDepartment().getId() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please check all required fields are correct.");
        }
    }

    private Department getDepartment(Employee employee) {
        return this.departmentRepository.findById(employee.getDepartment().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No department with that id found."));
    }
}
