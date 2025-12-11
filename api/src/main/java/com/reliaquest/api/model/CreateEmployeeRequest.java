package com.reliaquest.api.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateEmployeeRequest {
    @NotBlank
    String name;
    @Positive @NotNull
    Integer salary;
    @Min(16) @Max(75) @NotNull
    Integer age;
    @NotBlank
    String title;
}
