package com.reviewyme.userservice.existinsetup.exception;

public class EmailAlreadyExistException extends RuntimeException{
    public EmailAlreadyExistException (String message){
        super(message);
    }
}
