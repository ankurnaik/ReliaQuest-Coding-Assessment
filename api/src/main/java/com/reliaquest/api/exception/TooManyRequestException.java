package com.reliaquest.api.exception;

public class TooManyRequestException extends RuntimeException{
    public TooManyRequestException(String s){
        super(s);
    }
}
