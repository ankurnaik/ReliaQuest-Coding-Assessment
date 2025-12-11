package com.reliaquest.api.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private String id;
    private String employee_name;
    private Integer employee_salary;
    private Integer employee_age;
    private String employee_title;
    private String employee_email;
}
