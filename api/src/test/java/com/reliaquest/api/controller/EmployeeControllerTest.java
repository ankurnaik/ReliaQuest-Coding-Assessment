package com.reliaquest.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    public void getAllEmployeeTest() throws Exception {
        when(employeeService.getAllEmployee()).thenReturn(employeeList);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk());

    }

    @Test
    public void getEmployeesByNameSearchTest() throws Exception {

        when(employeeService.getEmployeeByName(anyString())).thenReturn(employeeList);

        mockMvc.perform(get("/employees/search/ar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employee_name").value("Arnold"));
    }

    @Test
    public void getEmployeesByNameTest_NotFound() throws Exception {
        List<Employee> employeeList = new ArrayList<>();

        when(employeeService.getEmployeeByName(anyString())).thenReturn(employeeList);

        mockMvc.perform(get("/employees/search/ar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetEmployeeById() throws Exception {

        Employee employee = new Employee("163","Santosh",30213,33,"Staff","santosh@mail.com");

        when(employeeService.getEmployeeById(anyString())).thenReturn(Optional.of(employee));

        mockMvc.perform(get("/employees/163"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("Santosh"));
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        when(employeeService.getEmployeeById(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/employees/13"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetHighestSalary() throws Exception {

        when(employeeService.getAllEmployee()).thenReturn(employeeList);
        when(employeeService.getHighestSalary(employeeList)).thenReturn(Optional.of(50201));

        mockMvc.perform(get("/employees/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("50201"));
    }

    @Test
    void testTopTenEmployees() throws Exception {

        when(employeeService.getAllEmployee()).thenReturn(employeeList);
        when(employeeService.getTopTenEmployeeNames(employeeList))
                .thenReturn(List.of("Arnold", "Aarya","Santosh","Alice","Ankur","Preeti","Manasi","Shekhar","Dheeraj","Tejas"));

        mockMvc.perform(get("/employees/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(10));
    }

    @Test
    void testCreateEmployee() throws Exception {

        Employee employee = new Employee("153", "Aarya", 50201, 66, "Researcher", "aarya@mail.com");
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(employee.getEmployee_name(),employee.getEmployee_salary(),employee.getEmployee_age(),employee.getEmployee_title());

        when(employeeService.createEmployee(any(CreateEmployeeRequest.class)))
                .thenReturn(Optional.of(employee));

        mockMvc.perform(
                        post("/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createEmployeeRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employee_name").value(employee.getEmployee_name()));
    }

    @Test
    void testDeleteEmployee() throws Exception {

        when(employeeService.deleteById("111"))
                .thenReturn(Optional.of("Ankur"));

        mockMvc.perform(delete("/employees/111"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ankur"));
    }

}
