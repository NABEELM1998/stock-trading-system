package com.nabeel.order_service.exceptions;

public class FraudCheckFailedException extends RuntimeException{

    public FraudCheckFailedException(String message){
        super(message);
    }
}
