package com.reliaquest.api.exception;

public class EmployeeNotFound extends RuntimeException {
    public EmployeeNotFound(String msg){
        super(msg);
    }
}
