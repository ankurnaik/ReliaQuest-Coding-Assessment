package com.reliaquest.api.exception;

import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.model.EmployeeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalEmployeeExceptionHandler {

    @ExceptionHandler(EmployeeNotFound.class)
    public ResponseEntity<String> handleNotFound(EmployeeNotFound employeeNotFound){
        log.error("The Employee was not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(employeeNotFound.getMessage());
    }

    @ExceptionHandler(TooManyRequestException.class)
    public ResponseEntity<EmployeeResponse<Employee>> handleTooManyRequest(TooManyRequestException tooManyRequestException){
        Employee dummyEmployee = new Employee("Dummy1",
                "Dummy1",
                0,
                18,
                "Dummy Title",
                "dummy@company.com"
        );
        log.error("Server not reachable: {}",tooManyRequestException.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(new EmployeeResponse<>(dummyEmployee,"429","Server is not reachable"));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleInvalidArgument(MethodArgumentNotValidException methodArgumentNotValidException){
        log.error("Invalid argument passed :{}", methodArgumentNotValidException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Argument passed");
    }
}
