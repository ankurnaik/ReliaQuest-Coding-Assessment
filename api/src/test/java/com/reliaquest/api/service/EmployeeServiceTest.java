package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeClient;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.EmployeeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    EmployeeClient employeeClient;

    @InjectMocks
    EmployeeService employeeService;

    List<Employee> employeeList = new ArrayList<>();

    @BeforeEach
    void setup(){
        employeeList = List.of(
                new Employee("143","Arnold",40203,43,"Trainer","arnold@mail.com"),
                new Employee("153","Aarya",50201,66,"Researcher","aarya@mail.com"),
                new Employee("163","Santosh",30213,33,"Staff","santosh@mail.com"),
                new Employee("173","Alice",30303,35,"Staff","alice@mail.com"),
                new Employee("111","Ankur",432410,27,"Engineer","ankur@google.com"),
                new Employee("222","Preeti",432111,18,"Advocate","preeti@google.com"),
                new Employee("183","Manasi",45213,33,"Doctor","manasi@mail.com"),
                new Employee("193","Shekhar",20303,35,"Staff","Shekhar@mail.com"),
                new Employee("121","Dheeraj",512410,27,"Engineer","dheeraj@google.com"),
                new Employee("242","Tejas",552111,18,"Architect","tejas@google.com")
        );
    }

    private <T> EmployeeResponse<T> buildResponse(T data,String status, String error) {
        EmployeeResponse<T> res = new EmployeeResponse<>();
        res.setData(data);
        res.setStatus(status);
        res.setError(error);
        return res;
    }

    @Test
    public void getAllEmployeeTest(){

        EmployeeResponse<List<Employee>> response =buildResponse(employeeList,"200",null);
        when(employeeClient.getAll()).thenReturn(response);

        List<Employee> allEmployeeList = employeeService.getAllEmployee();

        assertEquals("success",10,allEmployeeList.size());
    }

    @Test
    public void getEmployeeByNameTest(){
        EmployeeResponse<List<Employee>> response =buildResponse(employeeList,"200",null);
        when(employeeClient.getAll()).thenReturn(response);

        List<Employee> allEmployeeList = employeeService.getEmployeeByName("ar");

        assertEquals("success",3,allEmployeeList.size());
    }

    @Test
    public void getEmployeeByNameTest_NotFound(){
        EmployeeResponse<List<Employee>> response =buildResponse(employeeList,"404","Employee Not Found");
        when(employeeClient.getAll()).thenReturn(response);

        List<Employee> allEmployeeList = employeeService.getEmployeeByName("abcde");

        assertEquals("fail",0,allEmployeeList.size());
    }

    @Test
    public void getEmployeeByIdTest(){
        Employee mockEmployee = new Employee("143","Arnold",40203,43,"Trainer","arnold@mail.com");
        EmployeeResponse<Employee> response =buildResponse(mockEmployee,"200",null);

        when(employeeClient.getById(anyString())).thenReturn(response);

        Optional<Employee> employee = employeeService.getEmployeeById("143");

        assertTrue("success", Objects.nonNull(employee));
    }

    @Test
    public void getTopTenEmployeeNamesTest(){
        List<String> topTenEmployeeNames = employeeService.getTopTenEmployeeNames(employeeList);

        assertEquals("success",10, topTenEmployeeNames.size());
    }

    @Test
    public void getHighestSalaryTest(){
        Optional<Integer> highestSalary = employeeService.getHighestSalary(employeeList);

        assertEquals("success",552111,highestSalary.get());
    }

    @Test
    public void createEmployeeTest(){
        Employee employee = new Employee("153", "Aarya", 50201, 66, "Researcher", "aarya@mail.com");
        EmployeeResponse<Employee> employeeResponse = buildResponse(employee,"201",null);

        when(employeeClient.create(any(CreateEmployeeRequest.class))).thenReturn(employeeResponse);

        Optional<Employee> createdEmployee = employeeService.createEmployee(new CreateEmployeeRequest("Aarya", 50201, 66, "Resaearch"));

        assertTrue("Created",Objects.nonNull(createdEmployee));
    }

    @Test
    public void deleteEmployeeTest(){
        Employee mockEmployee = new Employee("143","Arnold",40203,43,"Trainer","arnold@mail.com");
        EmployeeResponse<Employee> employeeResponse = buildResponse(mockEmployee,"200",null);

        EmployeeResponse<Boolean> booleanEmployeeResponse = buildResponse(true,"200",null);

        when(employeeClient.getById(anyString())).thenReturn(employeeResponse);
        when(employeeClient.deleteByName(anyString())).thenReturn(booleanEmployeeResponse);

        Optional<String> s = employeeService.deleteById("143");

        assertEquals("success","Arnold",s.get());
    }
}
