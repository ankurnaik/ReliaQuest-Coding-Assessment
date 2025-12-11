package com.reliaquest.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeResponse<T> {
    T data;
    String status;
    String error;
}
