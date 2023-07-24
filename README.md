# Java APIs Using JPA and Hibernate - Part 2

## Learning Objectives
- Deal with foreign key relationships between tables using Hibernate, JPA and the Spring Framework

## Instructions

1. Fork this repository
2. Clone your fork to your machine
3. Open the project in IntelliJ
4. Copy the application.yml.example file and paste a new version as application.yml into the resources folder
5. Double check it isn't being tracked by Git before doing the next step
6. Add your database connection details to the application.yml file

## Building our tables

We are going to have two simple database tables employees and departments with the following details:

| Employees     |        |                      |
|---------------|--------|----------------------|
| id            | SERIAL | PRIMARY KEY          |
| first_name    | TEXT   | NOT NULL             |
| last_name     | TEXT   | NOT NULL             |
| department_id | INT    | FK -> DEPARTMENTS ID |

| Departments |        |             |
|-------------|--------|-------------|
| id          | SERIAL | PRIMARY KEY |
| name        | TEXT   |             |
| location    | TEXT   |             |

We want to be able to enter an employee id and get back their details, but with the department id replaced with the name and location of the department.

We also want to be able to search for all employees in a given department and get a list of them back (this is not as straightforward as you may think).

Let's look at the code to implement the first of these, and then look at the problems:

Department Class

```java
package com.booleanuk.api.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    public Department() {
        super();
    }

    public Department(String name, String location) {
        super();
        this.name = name;
        this.location = location;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
```

Employee Class

```java
package com.booleanuk.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    public Employee() {
        super();
    }

    public Employee(String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
```

DepartmentRepository Interface

```java
package com.booleanuk.api.repository;

import com.booleanuk.api.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

}
```

EmployeeRepository Interface

```java
package com.booleanuk.api.repository;

import com.booleanuk.api.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

}
```
EmployeeController Class

```java
package com.booleanuk.api.controller;

import com.booleanuk.api.model.Employee;
import com.booleanuk.api.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("employees")
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public List<Employee> getAllEmployees() {
        return this.employeeRepository.findAll();
    }

}
```

DepartmentController Class

```java
package com.booleanuk.api.controller;

import com.booleanuk.api.model.Department;
import com.booleanuk.api.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
```

Set up the Main class as previously and run it. Visiting the `employees` endpoint you'll see all of the employees with their departments listed as expected, but visiting the `departments` endpoint you'll find that the employees for each department are missing. To fix this you might try something like the following (This doesn't work by the way) by adding a list of employees to the department class as a field along with its getter and setter.

```java
    @OneToMany(mappedBy = "department")
    private List<Employee> employees;
```

```java
    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
```

Rerunning it the code will appear to work until you try to visit either of the endpoints at which point you'll find that you get errors. Basically you are getting circular references between the Employee and Department and the people in the department each time so that it never finishes the query.

## Exercise 1

In your pairs explore the documentation and use whatever search engines you prefer to find 
a) a solution to being able to display our employees as a detail of the department table
b) the best solution to this (if one exists)

## Exercise 2

Implement the rest of the endpoints we normally build for our Employees/Departments classes. The spec can be found [here](https://boolean-uk.github.io/java-api-jpa-hibernate-part2-workshop/).



