package com.reliaquest.api.client;

import com.reliaquest.api.config.WebClientConfiguration;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.exception.EmployeeNotFound;
import com.reliaquest.api.exception.TooManyRequestException;
import com.reliaquest.api.model.DeleteEmployeeRequest;
import com.reliaquest.api.model.CreateEmployeeRequest;
import com.reliaquest.api.model.EmployeeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class EmployeeClient {

    @Autowired
    WebClientConfiguration webClientConfiguration;

    @Value("${retry.maxAttempts}")
    private int maxAttempts;

    @Value("${retry.seconds}")
    private int seconds;

    public EmployeeResponse<List<Employee>> getAll(){
        WebClient webClient = webClientConfiguration.getClient();
        EmployeeResponse<List<Employee>> employeeResponse = webClient.get()
                .uri("/employee")
                .retrieve()
                .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals,
                        response -> Mono.error(new TooManyRequestException("Too Many Request")))
                .bodyToMono(new ParameterizedTypeReference<EmployeeResponse<List<Employee>>>(){})
                .retryWhen(
                        Retry.backoff(maxAttempts, Duration.ofSeconds(seconds))
                                .doBeforeRetry(retrySignal->log.warn("Retry attempt while getting all the employee: {} {} ", retrySignal.totalRetriesInARow(), LocalDateTime.now()))
                                .filter(throwable-> throwable instanceof TooManyRequestException)
                )
                .block();
        return employeeResponse;
    }

    public EmployeeResponse<Employee> getById(String id) {
        EmployeeResponse<Employee> employeeById = webClientConfiguration.getClient()
                .get()
                .uri("/employee/{id}",id)
                .retrieve()
                .onStatus(
                        HttpStatus.NOT_FOUND::equals,
                        clientResponse -> Mono.error(new EmployeeNotFound("Employee Not Found"))
                )
                .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals,
                        clientResponse -> Mono.error(new TooManyRequestException("Too Many Request")))
                .bodyToMono(new ParameterizedTypeReference<EmployeeResponse<Employee>>() {})
                .retryWhen(
                        Retry.backoff(maxAttempts, Duration.ofSeconds(seconds))
                                .doBeforeRetry(retrySignal->log.warn("Retry attempt while getting the Employee by Id: {} {}", retrySignal.totalRetriesInARow(), LocalDateTime.now()))
                                .filter(throwable-> throwable instanceof TooManyRequestException)
                )
                .block();
        return employeeById;
    }

    public EmployeeResponse<Employee> create(CreateEmployeeRequest employeeInput) {
        WebClient webClient = webClientConfiguration.getClient();
        EmployeeResponse<Employee> createdEmployee = webClient.post()
                .uri("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(employeeInput)
                .retrieve()
                .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals,
                        response -> Mono.error(new TooManyRequestException("Too Many Request")))
                .bodyToMono(new ParameterizedTypeReference<EmployeeResponse<Employee>>(){})
                .retryWhen(
                        Retry.backoff(maxAttempts, Duration.ofSeconds(seconds))
                                .doBeforeRetry(retrySignal->log.warn("Retry Attempt while trying to create: {} {}", retrySignal.totalRetriesInARow(), LocalDateTime.now()))
                                .filter(throwable-> throwable instanceof TooManyRequestException)
                )
                .block();
        return createdEmployee;
    }

    public EmployeeResponse<Boolean> deleteByName(String name) {
        System.out.println("Name: "+name);
        EmployeeResponse<Boolean> isEmployeeDeleted = webClientConfiguration.getClient()
                .method(HttpMethod.DELETE)
                .uri("/employee")
                .bodyValue(new DeleteEmployeeRequest(name))
                .retrieve()
                .onStatus(
                        HttpStatus.TOO_MANY_REQUESTS::equals,
                        clientResponse -> Mono.error(new TooManyRequestException("Too Many Request"))
                )
                .bodyToMono(new ParameterizedTypeReference<EmployeeResponse<Boolean>>() {})
                .retryWhen(
                        Retry.backoff(maxAttempts, Duration.ofSeconds(seconds))
                                .doBeforeRetry(retrySignal -> log.warn("Retry from delete: {} {} ",retrySignal.totalRetriesInARow(),LocalDateTime.now()))
                                .filter(throwable -> throwable instanceof TooManyRequestException)
                )
                .block();
        return isEmployeeDeleted;
    }
}
