package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeClient;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.EmployeeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class EmployeeService{

    @Autowired
    EmployeeClient employeeClient;


    public List<Employee> getAllEmployee() {
        EmployeeResponse<List<Employee>> employeeResponse = employeeClient.getAll();
        return employeeResponse.getData();
    }


    public List<Employee> getEmployeeByName(String searchString) {
        List<Employee> employeeList = employeeClient.getAll().getData();
        List<Employee> employeeNameList = employeeList.stream()
                .filter(employee -> Objects.nonNull(searchString) && employee.getEmployee_name().toLowerCase().contains(searchString.toLowerCase()))
                .toList();
        log.debug("Name of Employees: {}",employeeNameList);
        return employeeNameList;
    }


    public Optional<Employee> getEmployeeById(String id) {
        Optional<Employee> employee = Optional.of(employeeClient.getById(id).getData());
        return employee;
    }

    public List<String> getTopTenEmployeeNames(List<Employee> employeeList) {
        return employeeList
                .stream()
                .sorted(Comparator.comparing(Employee::getEmployee_salary).reversed())
                .map(Employee::getEmployee_name)
                .limit(10)
                .toList();
    }

    public Optional<Integer> getHighestSalary(List<Employee> employeeList) {
        return employeeList.stream()
                .max(Comparator.comparing(Employee::getEmployee_salary))
                .map(Employee::getEmployee_salary);
    }

    public Optional<Employee> createEmployee(CreateEmployeeRequest employeeInput) {
        Optional<Employee> employee = Optional.of(employeeClient.create(employeeInput).getData());
        return employee;
    }

    public Optional<String> deleteById(String id) {
        Optional<Employee> employee = getEmployeeById(id);

        if(employee.isEmpty()){
            log.debug("No Employee found");
            return Optional.empty();
        }

        log.debug("We have found the employee: {}",employee.get());
        Boolean isEmployeeDeleted = employeeClient.deleteByName(employee.get().getEmployee_name()).getData();
        log.debug("The employee was deleted: {}",isEmployeeDeleted);

        return isEmployeeDeleted ? Optional.of(employee.get().getEmployee_name()) : Optional.empty();
    }
}
