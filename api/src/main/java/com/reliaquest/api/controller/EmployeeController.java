package com.reliaquest.api.controller;

import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("employees")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeRequest>{

    @Autowired
    EmployeeService  employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employeeList = employeeService.getAllEmployee();
        if (employeeList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employeeList);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        log.info("Search String: {}",searchString);
        List<Employee> employeenameList = employeeService.getEmployeeByName(searchString);
        if(employeenameList.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employeenameList);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        log.info("The request for getting employee with id: {} has been received",id);
        ResponseEntity<Employee> employeebyId = employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build());

        return employeebyId;
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        List<Employee> employeeList = employeeService.getAllEmployee();
        ResponseEntity<Integer> highestSalary = employeeService.getHighestSalary(employeeList)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
        log.info("The Employee with the highest salary is : {}",highestSalary.getBody());
        return highestSalary;
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<Employee> employeeList = employeeService.getAllEmployee();
        List<String> topSalaryEmployeeName = employeeService.getTopTenEmployeeNames(employeeList);
        log.info("The top 10 employees are : {}",topSalaryEmployeeName);
        if (topSalaryEmployeeName.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(topSalaryEmployeeName);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@Valid CreateEmployeeRequest employeeInput) {
        log.info("The request for creating employee : {} has been received",employeeInput);
        ResponseEntity<Employee> employee = employeeService.createEmployee(employeeInput)
                .map(employeeResponse -> ResponseEntity.status(HttpStatus.CREATED).body(employeeResponse))
                .orElseGet(()->ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
        return employee;
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(@Valid String id) {
        log.info("The request for deleting employee with id: {} has been received",id);
        ResponseEntity<String> employeeName = employeeService.deleteById(id)
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build());

        return employeeName;
    }
}
