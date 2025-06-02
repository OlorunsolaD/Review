package com.Sola.user_service.exception;

public class EmailAlreadyExistException extends RuntimeException{
    public EmailAlreadyExistException (String message){
        super(message);
    }
}
